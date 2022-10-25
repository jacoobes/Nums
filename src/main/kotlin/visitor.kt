import com.github.h0tk3y.betterParse.utils.Tuple2

interface StatementVisitor {
    fun onFn(fn : FFunction)
    fun onIf(iif: Iif)
    fun onLoop(loop: Loop)
    fun onExprStmt(expressionStatement: ExpressionStatement)
    fun onBlock(block: Block)
    fun onVal(valStmt: Val)
}

interface ExpressionVisitor {
    fun onNumber(number: Number)
    fun onStr(stringLiteral: StringLiteral)
    fun onBinary(binary: Binary)
    fun onUnary(unary: Unary)
    fun onBool(bool: Bool)
    fun onVariable(variable: Variable)
    fun onAnd(and: And)
    fun onOr(or: Or)
    fun onArrLiteral(arrayLiteral: ArrayLiteral)
}

fun visitor(tree: List<FFunction>, visitStrategy: Tuple2<StatementVisitor, ExpressionVisitor>) {
    for (node in tree) {
        visitStatement(node, visitStrategy.t1)
    }
}

fun visitStatement(statement : Statement, visitStrategy: StatementVisitor) {
    when (statement) {
        is FFunction -> visitStrategy.onFn(statement)
        is Iif -> visitStrategy.onIf(statement)
        is Loop -> visitStrategy.onLoop(statement)
        is ExpressionStatement -> visitStrategy.onExprStmt(statement)
        is Block -> visitStrategy.onBlock(statement)
        is Val -> visitStrategy.onVal(statement)
    }
}