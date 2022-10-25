

object DefaultExprVisitor : ExpressionVisitor {
    override fun onNumber(number: Number) {
        TODO("Not yet implemented")
    }

    override fun onStr(stringLiteral: StringLiteral) {
        TODO("Not yet implemented")
    }

    override fun onBinary(binary: Binary) {
        TODO("Not yet implemented")
    }

    override fun onUnary(unary: Unary) {
        TODO("Not yet implemented")
    }

    override fun onBool(bool: Bool) {
        TODO("Not yet implemented")
    }

    override fun onVariable(variable: Variable) {
        TODO("Not yet implemented")
    }

    override fun onAnd(and: And) {
        TODO("Not yet implemented")
    }

    override fun onOr(or: Or) {
        TODO("Not yet implemented")
    }

    override fun onArrLiteral(arrayLiteral: ArrayLiteral) {
        TODO("Not yet implemented")
    }

}


object DefaultStatementVisitor : StatementVisitor {
    override fun onFn(fn: FFunction) {
        TODO("Not yet implemented")
    }

    override fun onIf(iif: Iif) {
        TODO("Not yet implemented")
    }

    override fun onLoop(loop: Loop) {
        TODO("Not yet implemented")
    }

    override fun onExprStmt(expressionStatement: ExpressionStatement) {
        TODO("Not yet implemented")
    }

    override fun onBlock(block: Block) {
        TODO("Not yet implemented")
    }

    override fun onVal(valStmt: Val) {
        TODO("Not yet implemented")
    }

}