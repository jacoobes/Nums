
class DefaultFunctionVisitor(
    private val fn: FFunction,
    private val f: NumsWriter,
    private val semantics: Semantics,
    ) {
    fun start() {
        stmtVisitor.onFn(fn)
    }
    private val stmtVisitor = object : StatementVisitor {
        override fun visit(item: Statement) {
            when (item) {
                is Iif -> visit(item, ::onIf)
                is Loop -> visit(item, ::onLoop)
                is ExpressionStatement -> visit(item, ::onExprStmt)
                is Block -> visit(item,::onBlock)
                is Val -> visit(item, ::onVal)
                is Return -> visit(item, ::onReturn)
                is Skip -> {}
                is Assign -> visit(item, ::onAssign)
                else -> {}
            }
        }

        override fun onFn(fn: FFunction) {
            if(fn.main) {
                f.writeln("@__entry\n    r0 <- call main\n    exit")
            }
            f.writeln("func ${fn.token.name}")
            fn.args.forEach {
                val reg = semantics.addRegister(it)
                semantics.addLocal(it.name, reg, false)
            }
            visit(fn.block)
            semantics.clearRegisters()
            f.writeln("exit")
            f.writeln("end")
        }

        override fun onIf(iif: Iif) {
            exprVisitor.visit(iif.condition)
            val eReg = semantics.topMostReg()
            val thenLabel = "then.${iif.thenBody.hashCode()}"
            val elseLabel = "else.${iif.elseBody.hashCode()}"
            f.writeln("bb <- ${r(eReg)} $elseLabel $thenLabel", semantics.scopeDepth)
            f.writeln("@$thenLabel", semantics.scopeDepth)
            semantics.incDepth()
            visit(iif.thenBody)
            semantics.decDepth()
            f.writeln("jump @$elseLabel", semantics.scopeDepth)
            semantics.incDepth()
            visit(iif.elseBody)
            semantics.decDepth()
        }

        override fun onLoop(loop: Loop) {
            exprVisitor.visit(loop.condition)
            val curReg = semantics.topMostReg()
            val hash = loop.hashCode()
            f.writeln("bb ${r(curReg)} head.$hash exit.$hash", semantics.scopeDepth)
            f.writeln("@loop.$hash", semantics.scopeDepth)
            visit(loop.block)
            f.writeln("jump loop.$hash", semantics.scopeDepth)
            f.writeln("@exit.$hash", semantics.scopeDepth)
        }

        override fun onExprStmt(expressionStatement: ExpressionStatement) {
            if (expressionStatement.expr is Call) TODO()
            if(expressionStatement.expr is Variable) exprVisitor.visit(expressionStatement.expr)
        }

        override fun onBlock(block: Block) {
            semantics.incDepth()
            block.stmts.forEach(::visit)
            semantics.decDepth()
        }

        override fun onVal(valStmt: Val) {
            f.writeln("# val ${valStmt.token.name}", semantics.scopeDepth)
            exprVisitor.visit(valStmt.expr)
            val localReg = semantics.topMostReg()
            semantics.addLocal(valStmt.token.name, localReg, valStmt.isAssignable)
        }

        override fun onReturn(ret: Return) {
            exprVisitor.visit(ret.expr)
            f.writeln("ret ${r(semantics.topMostReg())}",semantics.topMostReg())
        }

        override fun onAssign(assign: Assign) {
            exprVisitor.visit(assign.newVal)
            val tReg = semantics.topMostReg()
            val local = semantics.getLocal(assign.tok)
            if(!local.isAssignable) throw Error("Cannot assign to val")
            f.writeln("${r(local.registerVal)} <- reg ${r(tReg)}", semantics.scopeDepth)
        }
    }

    private val exprVisitor = object : ExpressionVisitor {
        override fun onNumber(number: Number) {
            val ireg = semantics.addRegister(number)
            f.writeln("${r(ireg)} <- int ${number.value}", semantics.scopeDepth)
        }

        override fun onStr(stringLiteral: StringLiteral) {
            val ireg = semantics.addRegister(stringLiteral)
            f.writeln("${r(ireg)} <- str :${stringLiteral.str}", semantics.scopeDepth)
        }

        override fun onBinary(binary: Binary) {
            visit(binary.left)
            visit(binary.right)
            val iReg = semantics.addRegister(binary)
            f.writeln("${r(iReg)} <- ${binary.op} ${r(iReg - 2)} ${r(iReg - 1)}",semantics.scopeDepth)
        }

        override fun onCmp(cmp: Comparison) {
            visit(cmp.left)
            visit(cmp.right)
            val iReg = semantics.addRegister(cmp)
            val base = { op: String -> "<- call $op ${r(iReg - 2)} ${r(iReg - 1)}" }
            val instr = { str: String, invert:Boolean ->
                    "$str\n" + if(invert) "".padStart(semantics.scopeDepth*2) + "${r(iReg)} <- call not ${r(iReg)}" else ""
            }
            val cmpInstruction = when(cmp.op) {
                ComparisonOps.Eq -> instr(base("eq"), false)
                ComparisonOps.Neq -> instr(base("eq"), true)

                ComparisonOps.Lt -> instr(base("lt"), false)
                ComparisonOps.Gte -> instr(base("lt"), true)

                ComparisonOps.Gt -> instr(base("gt"), false)
                ComparisonOps.Lte -> instr(base("gt"), true)
            }
            f.writeln("${r(iReg)} $cmpInstruction", semantics.scopeDepth)
        }

        override fun onUnary(unary: Unary) {
            visit(unary.expr)
            val iReg = semantics.topMostReg()
            when(unary.op.name) {
                "not" -> {
                    val newReg = semantics.addRegister(unary)
                    f.writeln("${r(newReg)} <- call not ${r(iReg)}", semantics.scopeDepth)
                }
            }
        }

        override fun onBool(bool: Bool) {
            val ireg = semantics.addRegister(bool)
            f.writeln(r(ireg) + " <- int ${bool.bool}", semantics.scopeDepth)
        }

        override fun onVariable(variable: Variable) {
            val ireg = semantics.addRegister(variable)
            val local = semantics.getLocal(variable)
            f.writeln("${r(ireg)} <- reg ${r(local.registerVal)}", semantics.scopeDepth)
        }

        override fun onAnd(and: And) {
            visit(and.left)
            visit(and.right)
            val i = semantics.topMostReg()
            f.writeln("${r(i)} <- band ${r(i)} ${r(i-1)}",semantics.scopeDepth)
        }

        override fun onOr(or: Or) {
            visit(or.left)
            visit(or.right)
            val i = semantics.topMostReg()
            f.writeln("${r(i)} <- bor ${r(i)} ${r(i-1)}",semantics.scopeDepth)
        }

        override fun onCall(call: Call) {
            call.args.forEach(::visit)
            val storedRegs = call.args.map { "r${semantics.registers[it.hashCode()]}" }
            val regStr = storedRegs.joinToString(" ")
            val i = semantics.addRegister(call)
            f.writeln("${r(i)} <- call ${call.callee.name} $regStr", semantics.scopeDepth)
        }

        override fun onArrLiteral(arrayLiteral: ArrayLiteral) {
            throw Error("No array literals yet")
//            arrayLiteral.exprs.forEach(::visit)
//            val loopCode = "set.${arrayLiteral.hashCode()}"
//            val exitCode = "tes.${arrayLiteral.hashCode()}"
//            val areg = semantics.addRegister(arrayLiteral)
//            //len of arr
//            f.writeln("${r(areg)} <- int ${arrayLiteral.exprs.size}", semantics.scopeDepth)
//            f.writeln(
//                "${r(areg+1)} <- arr ${r(areg)}",
//                semantics.scopeDepth
//            )
        }

        override fun onGet(get: Get) {

        }

        override fun visit(item: Expr) {
            when(item) {
                is Number -> visit(item, ::onNumber)
                is StringLiteral -> visit(item, ::onStr)
                is Binary -> visit(item, ::onBinary)
                is Unary -> visit(item, ::onUnary)
                is Bool -> visit(item, ::onBool)
                is ArrayLiteral -> visit(item, ::onArrLiteral)
                is And -> visit(item, ::onAnd)
                is Or -> visit(item, ::onOr)
                is Variable -> visit(item, ::onVariable)
                is Comparison -> visit(item, ::onCmp)
                is Call -> visit(item, ::onCall)
                is Get -> visit(item, ::onGet)
            }
        }
    }
}

fun r(int: Int): String = "r$int"