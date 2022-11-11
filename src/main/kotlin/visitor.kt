import com.github.h0tk3y.betterParse.grammar.tryParseToEnd
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
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

}

fun <T: Node> visit(item : T, cb: (T) -> Unit): T {
    cb(item)
    return item
}

fun visitor(tree: List<Statement>, bw: NumsWriter) {
    //for now, have minivm natives
    bw.writeln(MiniVmNative.core())
    for(node in tree) {
        when(node) {
            is FFunction -> visitFns(node, bw)
            is Import -> {
                val numsFile = File(node.path)
                if(!numsFile.exists()) throw Error("File $numsFile does not exist")
                if(numsFile.isDirectory) throw Error("No directories allowed")
                if(numsFile.extension != "nums") throw Error("Only .nums files are allowed")
                val importedGrammar = NumsGrammar().tryParseToEnd(numsFile.readText())
                println(importedGrammar)
            }
            else -> throw Error("Cannot have $node top level!")
        }
    }
}
//inorder traversal
fun visitFns(stmt: FFunction, bw: NumsWriter) {
    val defaultFnVisitor = DefaultFunctionVisitor(stmt, bw, Semantics())
    defaultFnVisitor.start()
}