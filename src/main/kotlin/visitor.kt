import kotlin.reflect.KFunction1

interface Visitor<T> {
    fun visit(item: T)
}

interface StatementVisitor : Visitor<Statement> {
    fun onFn(fn : FFunction)
    fun onIf(iif: Iif)
    fun onLoop(loop: Loop)
    fun onExprStmt(expressionStatement: ExpressionStatement)
    fun onBlock(block: Block)
    fun onVal(valStmt: Val)
}

interface ExpressionVisitor : Visitor<Expr> {
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

fun visitor(tree: List<Statement>,visitStrategy: StatementVisitor) {
    for (node in tree) {
        visitProgram(node,visitStrategy)
    }
}
//inorder traversal
fun visitProgram(stmt: Statement, strats: StatementVisitor) {
    strats.visit(stmt)
}