import java.lang.Error

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
}

interface ExpressionVisitor : Visitor<Expr> {
    fun onNumber(number: Number)
    fun onStr(stringLiteral: StringLiteral)
    fun onBinary(binary: Binary)
    fun onCmp(cmp: Comparison)
    fun onUnary(unary: Unary)
    fun onBool(bool: Bool)
    fun onVariable(variable: Variable)
    fun onAnd(and: And)
    fun onOr(or: Or)
    fun onArrAccess(arrAccess: ArrAccess)
    fun onArrLiteral(arrayLiteral: ArrayLiteral)
}

fun <T: Node> visit(item : T, cb: (T) -> Unit): T {
    cb(item)
    return item
}

fun visitor(tree: List<Statement>, bw: NumsWriter) {
    for (node in tree) {
        when(node) {
            is FFunction -> visitProgram(node, bw)
            else -> throw Error("Cannot have $node top level!")
        }
    }
}
//inorder traversal
fun visitProgram(stmt: FFunction, bw: NumsWriter) {
    val defaultProgramVisitor = DefaultProgramVisitor(bw, Semantics())
    defaultProgramVisitor.start(stmt)
}