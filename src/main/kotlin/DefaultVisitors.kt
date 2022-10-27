import java.io.BufferedWriter

class DefaultExprVisitor(f: BufferedWriter) : ExpressionVisitor {
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
            f.write("@__entry")
        } else f.write("@__${fn.token.name}")
        visit(fn.block)
        f.write("exit")
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