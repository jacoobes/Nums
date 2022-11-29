import nodes.*
interface Visitor<T> {
    fun visit(item: T)
}

interface StatementVisitor: Visitor<Statement> {
    fun onFn(fn : FFunction)
    fun onIf(iif: Iif)
    fun onLoop(loop: Loop)
    fun onExprStmt(expressionStatement: ExpressionStatement)
    fun onBlock(block: Block)
    fun onVal(valStmt: Val)
    fun onReturn(ret: Return)
    fun onAssign(assign:Assign)
    fun onImport(import: Import)

}

interface ExpressionVisitor : Visitor<Expr> {
    fun onNumber(number: nodes.Number)
    fun onStr(stringLiteral: StringLiteral)
    fun onBinary(binary: Binary)
    fun onCmp(cmp: Comparison)
    fun onUnary(unary: Unary)
    fun onBool(bool: Bool)
    fun onVariable(variable: Variable)
    fun onAnd(and: And)
    fun onOr(or: Or)
    fun onCall(call: Call)
    fun onArrLiteral(arrayLiteral: ArrayLiteral)
    fun onPath(path: Path)

}

fun <T: Node> visit(item : T, cb: (T) -> Unit): T {
    cb(item)
    return item
}

fun visitor(tree: List<Statement>, bw: NumsWriter) {
//    for(node in tree) {
//        when(node) {
//            is FFunction -> visitFns(node, bw)
//            else -> {}
//        }
//    }
}
//inorder traversal
//fun visitFns(stmt: FFunction, bw: NumsWriter) {
//    val defaultFnVisitor = DefaultFunctionVisitor(stmt, bw, Semantics())
//    defaultFnVisitor.start()
//}