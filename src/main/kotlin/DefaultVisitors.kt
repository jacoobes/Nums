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
            f.writeln("# if ", semantics.scopeDepth)
            expressionVisitor.visit(iif.condition)
            val expr = semantics.topMostReg()
            val iflabel = "@then${expr}"
            val elselabel = "@else${expr}"
            f.writeln("@then${expr}")

            visit(iif.thenBody)

            f.writeln("@else${expr}")
            visit(iif.elseBody)

        }

        override fun onLoop(loop: Loop) {
            println(loop)
        }

        override fun onExprStmt(expressionStatement: ExpressionStatement) {
            when(expressionStatement.expr) {
                is Call -> TODO()
            }
        }

        override fun onBlock(block: Block) {
            semantics.incDepth()
            block.stmts.forEach(::visit)
            f.writeln("\nexit")
            semantics.decDepth()
        }

        override fun onVal(valStmt: Val) {
            f.writeln("# val ${valStmt.token.name}", semantics.scopeDepth)
            expressionVisitor.visit(valStmt.expr)
            val localReg = semantics.addRegister()
            semantics.addLocal(valStmt.token.name, localReg)
            f.writeln("${reg(localReg)} <- reg ${reg(localReg - 1)}", semantics.scopeDepth)
        }
    }

    private val expressionVisitor = object : ExpressionVisitor {
        override fun onNumber(number: Number) {
            val ireg = semantics.addRegister()
            f.writeln("${reg(ireg)} <- int ${number.value}", semantics.scopeDepth)
        }

        override fun onStr(stringLiteral: StringLiteral) {
            val ireg = semantics.addRegister()
            f.writeln("${reg(ireg)} <- str :${stringLiteral.str}", semantics.scopeDepth)
        }

        override fun onBinary(binary: Binary) {
            visit(binary.left)
            visit(binary.right)
            val iReg = semantics.addRegister()
            f.writeln("${reg(iReg)} <- ${binary.op} ${reg(iReg - 2)} ${reg(iReg - 1)}",semantics.scopeDepth)
        }

        override fun onCmp(cmp: Comparison) {
            visit(cmp.left)
            visit(cmp.right)
            val iReg = semantics.addRegister()
            f.writeln("${reg(iReg)} <- ")
        }

        override fun onUnary(unary: Unary) {
            visit(unary.expr)
        }

        override fun onBool(bool: Bool) {
            val ireg = semantics.addRegister()
            f.writeln(reg(ireg) + " <- int ${if(bool.bool) "1" else "0"}", semantics.scopeDepth)
        }

        override fun onVariable(variable: Variable) {
            println(variable)
        }

        override fun onAnd(and: And) {
            println(and)
        }

        override fun onOr(or: Or) {
            println(or)
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

fun reg(int: Int): String = "r$int"