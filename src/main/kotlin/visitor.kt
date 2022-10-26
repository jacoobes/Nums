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

fun <T: Node> visit(item : T, cb: (T) -> Unit): T {
    cb(item)
    return item
}

fun visitor(tree: List<Statement>, visitStrategy: Pair<StatementVisitor, ExpressionVisitor>) {
    for (node in tree) {
        visitProgram(node,visitStrategy)
    }
}
fun visitProgram(stmt: Statement, strats: Pair<StatementVisitor, ExpressionVisitor>) {
    val (sv, ev) = strats
    when (stmt) {
        is FFunction -> visit(stmt, sv::onFn).also { visitProgram(it.block, strats) }
        is Iif -> visit(stmt, sv::onIf).also {
            visitProgram(it.thenBody, strats)
            visitProgram(it.elseBody, strats)
        }
        is Loop -> visit(stmt, sv::onLoop).also {
            visitProgram(it.block, strats)
        }
        is ExpressionStatement -> visit(stmt, sv::onExprStmt).also { visitExpression(it.expr, ev) }
        is Block -> visit(stmt, sv::onBlock).also { it.stmts.forEach { st -> visitProgram(st, strats) } }
        is Val -> visit(stmt, sv::onVal).also { visitExpression(it.expr, ev) }
    }
}

fun visitExpression(expr: Expr, strat: ExpressionVisitor) {
    when(expr) {
        is Number -> visit(expr, strat::onNumber)
        is StringLiteral -> visit(expr, strat::onStr)
        is Binary -> visit(expr, strat::onBinary).also {
            visitExpression(it.left, strat)
            visitExpression(it.right, strat)
        }
        is Unary -> visit(expr, strat::onUnary).also { visitExpression(it.expr,strat) }
        is Bool -> visit(expr, strat::onBool)
        is ArrayLiteral -> visit(expr, strat::onArrLiteral)
        is And -> visit(expr, strat::onAnd).also {
            visitExpression(it.left,strat)
            visitExpression(it.right, strat)
        }
        is Or -> visit(expr, strat::onOr).also {
            visitExpression(it.left,strat)
            visitExpression(it.right, strat)
        }
        is Variable -> visit(expr, strat::onVariable)
    }
}