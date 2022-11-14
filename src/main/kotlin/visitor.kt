
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
    fun onCall(call: Call)
    fun onArrLiteral(arrayLiteral: ArrayLiteral)
    fun onGet(get: Get)

}

fun <T: Node> visit(item : T, cb: (T) -> Unit): T {
    cb(item)
    return item
}

fun visitor(tree: NumsNode, bw: NumsWriter) {
    val (fns, imports) = tree
    createImportGraph(imports)
    for(node in fns) {
        when(node) {
            //            is Import -> {
//                val numsFile = File(node.path)
//                if(!numsFile.exists()) throw Error("File $numsFile does not exist")
//                if(numsFile.isDirectory) throw Error("No directories allowed")
//                if(numsFile.extension != "nums") throw Error("Only .nums files are allowed")
//                when(val result = NumsGrammar().tryParseToEnd(numsFile.readText())) {
//                    is Parsed -> {
//                        println(result.value)
//                    }
//                    is ErrorResult -> println(result)
//                }
//            }
            else -> visitFns(node, bw)
        }
    }
}
fun createImportGraph(alpha: List<Import>) {}
//inorder traversal
fun visitFns(stmt: FFunction, bw: NumsWriter) {
    val defaultFnVisitor = DefaultFunctionVisitor(stmt, bw, Semantics())
    defaultFnVisitor.start()
}