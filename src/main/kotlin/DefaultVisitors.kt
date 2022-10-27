import java.io.BufferedWriter

class DefaultExprVisitor(val f: BufferedWriter) : ExpressionVisitor {
    override fun onNumber(number: Number) {
        f.write("int ${number.value}")
    }

    override fun onStr(stringLiteral: StringLiteral) {
        f.write("str :${stringLiteral.str}")
    }

    override fun onBinary(binary: Binary) {
        println(binary)
    }

    override fun onUnary(unary: Unary) {
        visit(unary.expr)
    }

    override fun onBool(bool: Bool) {
        println(bool)
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
        println(arrayLiteral)
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
        }
    }


}


class DefaultStatementVisitor(val f: BufferedWriter, val exprVisitor: DefaultExprVisitor) : StatementVisitor {
    override fun onFn(fn: FFunction) {
        if(fn.main) {
            f.write("@__entry\n    r0 <- call main\n    exit\n")
        }
        f.write("func ${fn.token.name}\n")
        visit(fn.block)
        f.write("end")
    }

    override fun onIf(iif: Iif) {
        println(iif)
    }

    override fun onLoop(loop: Loop) {
        println(loop)
    }

    override fun onExprStmt(expressionStatement: ExpressionStatement) {
        exprVisitor.visit(expressionStatement.expr)
    }

    override fun onBlock(block: Block) = block.stmts.forEach(::visit)

    override fun onVal(valStmt: Val) {
        println(valStmt)
    }

    override fun visit(item: Statement) {
        when (item) {
            is FFunction -> visit(item, this::onFn)
            is Iif -> visit(item, this::onIf)
            is Loop -> visit(item, this::onLoop)
            is ExpressionStatement -> visit(item, this::onExprStmt)
            is Block -> visit(item, this::onBlock)
            is Val -> visit(item, this::onVal)
        }
    }
}