import nodes.*

interface Visitor<T> {
    fun visit(item: T)
}

interface StatementVisitor : Visitor<Statement> {
    fun onFn(fn: FFunction)
    fun onIf(iif: Iif)
    fun onLoop(loop: Loop)
    fun onExprStmt(expressionStatement: ExpressionStatement)
    fun onBlock(block: Block)
    fun onVal(valStmt: Val)
    fun onReturn(ret: Return)
    fun onAssign(assign: Assign)
    fun onImport(import: Import)

    override fun visit(item: Statement) {
        when (item) {
            is Iif -> visit(item, ::onIf)
            is Loop -> visit(item, ::onLoop)
            is ExpressionStatement -> visit(item, ::onExprStmt)
            is Block -> visit(item, ::onBlock)
            is Val -> visit(item, ::onVal)
            is Return -> visit(item, ::onReturn)
            is Assign -> visit(item, ::onAssign)
            is Space -> {}
            is FFunction -> visit(item, ::onFn)
            is Import -> visit(item, ::onImport)
            else -> {}
        }
    }
}

interface ExpressionVisitor : Visitor<Expr> {

    fun onDouble(number: NumsDouble)
    fun onInt(number: NumsInt)
    fun onShort(number: NumsShort)
    fun onUByte(number: NumsByte)
    fun onFloat(number: NumsFloat)

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
    override fun visit(item: Expr) {
        when (item) {
            is NumsInt -> visit(item, ::onInt)
            is NumsFloat -> visit(item, ::onFloat)
            is NumsShort -> visit(item, ::onShort)
            is NumsDouble -> visit(item, ::onDouble)
            is NumsByte -> visit(item, ::onUByte)
            is StringLiteral -> visit(item, ::onStr)
            is Binary -> visit(item, ::onBinary)
            is Unary -> visit(item, ::onUnary)
            is Bool -> visit(item, ::onBool)
            is ArrayLiteral -> visit(item, ::onArrLiteral)
            is And -> visit(item, ::onAnd)
            is Or -> visit(item, ::onOr)
            is Variable -> visit(item, ::onVariable)
            is Comparison -> visit(item, ::onCmp)
            is Call -> visit(item, ::onCall)
            is Path -> visit(item, ::onPath)
            else -> throw Error("visited skip expression")
        }
    }
}

fun <T : Node> visit(item: T, cb: (T) -> Unit): T {
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