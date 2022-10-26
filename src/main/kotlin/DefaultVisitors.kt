

object DefaultExprVisitor : ExpressionVisitor {
    override fun onNumber(number: Number) {
        println(number)
    }

    override fun onStr(stringLiteral: StringLiteral) {
        println(stringLiteral)
    }

    override fun onBinary(binary: Binary) {
        println(binary)
    }

    override fun onUnary(unary: Unary) {
        println(unary)
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

}


object DefaultStatementVisitor : StatementVisitor {
    override fun onFn(fn: FFunction) {
        println(fn)
    }

    override fun onIf(iif: Iif) {
        println(iif)
    }

    override fun onLoop(loop: Loop) {
        println(loop)
    }

    override fun onExprStmt(expressionStatement: ExpressionStatement) {
        println(expressionStatement)
    }

    override fun onBlock(block: Block) {
        println(block)
    }

    override fun onVal(valStmt: Val) {
        println(valStmt)
    }

}