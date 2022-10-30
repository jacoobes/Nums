import java.io.BufferedWriter


class DefaultProgramVisitor(
    private val f: BufferedWriter,
    private val semantics: Semantics,
    ) {
    fun start(fn: FFunction) {
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
                else -> throw Error("Cannot have local functions")
            }
        }

        override fun onFn(fn: FFunction) {
            if(fn.main) {
                f.writeln("@__entry\n    r0 <- call main\n    exit")
            }
            f.writeln("func ${fn.token.name}")
            visit(fn.block)
            semantics.clearRegisters()
            f.writeln("end")
        }

        override fun onIf(iif: Iif) {

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
            semantics.addLocal(valStmt.token.name, localReg)
        }
    }

    private val exprVisitor = object : ExpressionVisitor {
        override fun onNumber(number: Number) {
            val ireg = semantics.addRegister()
            f.writeln("${r(ireg)} <- int ${number.value}", semantics.scopeDepth)
        }

        override fun onStr(stringLiteral: StringLiteral) {
            val ireg = semantics.addRegister()
            f.writeln("${r(ireg)} <- str :${stringLiteral.str}", semantics.scopeDepth)
        }

        override fun onBinary(binary: Binary) {
            visit(binary.left)
            visit(binary.right)
            val iReg = semantics.topMostReg()
            f.writeln("${r(iReg)} <- ${binary.op} ${r(iReg)} ${r(iReg - 1)}",semantics.scopeDepth)
        }

        override fun onCmp(cmp: Comparison) {
            visit(cmp.left)
            visit(cmp.right)
            val iReg = semantics.addRegister()
            val eq = "eq.${cmp.hashCode()}"
            val neq = "neq.${cmp.hashCode()}"
            val exit = "exit.${cmp.hashCode()}"
            f.writeln("beq ${r(iReg - 2)} ${r(iReg - 1)} $neq $eq", semantics.scopeDepth)
            f.writeln("@$eq",semantics.scopeDepth)
            f.writeln(" ${r(iReg)} <- int 1", semantics.scopeDepth)
            f.writeln("jump $exit", semantics.scopeDepth)
            f.writeln("@$neq", semantics.scopeDepth)
            f.writeln(" ${r(iReg)} <- int 0", semantics.scopeDepth)
            f.writeln("@$exit", semantics.scopeDepth)
        }

        override fun onUnary(unary: Unary) {
            visit(unary.expr)
            val iReg = semantics.topMostReg()
            when(unary.op.name) {
                "not" -> {
                    val newReg = semantics.addRegister()
                    f.writeln("${r(newReg)} <- int 1", semantics.scopeDepth)
                    f.writeln("${r(iReg)} <- bxor ${r(newReg)} ${r(iReg)}", semantics.scopeDepth)
                }
            }
        }

        override fun onBool(bool: Bool) {
            val ireg = semantics.addRegister()
            f.writeln(r(ireg) + " <- int ${if(bool.bool) "1" else "0"}", semantics.scopeDepth)
        }

        override fun onVariable(variable: Variable) {
            val ireg = semantics.addRegister()
            val local = semantics.getLocal(variable)
            f.writeln("${r(ireg)} <- reg ${r(local.registerVal)}", semantics.scopeDepth)
        }

        override fun onAnd(and: And) {
            visit(and.left)
            visit(and.right)
            val i = semantics.topMostReg()
            f.writeln("${r(i)} <- band ${r(i)} ${r(i-1)} ")
        }

        override fun onOr(or: Or) {
            visit(or.left)
            visit(or.right)
            val i = semantics.topMostReg()
            f.writeln("${r(i)} <- bor ${r(i)} ${r(i-1)} ")
        }

        override fun onArrLiteral(arrayLiteral: ArrayLiteral) {
            arrayLiteral.exprs.forEach(::visit)
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
                else -> throw Error("no")
            }
        }
    }
}
fun BufferedWriter.write(string: String, depth:Int= 0) {
    write("${"".padEnd(depth * 2)}$string")
}
fun BufferedWriter.writeln(string: String, depth:Int = 0) {
    write("${"".padEnd(depth * 2)}$string\n")
}

fun r(int: Int): String = "r$int"