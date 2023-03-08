import jvm.Chunk
import jvm.IR
import jvm.IRFunction
import jvm.Instruction
import nodes.*

interface ExpressionVisitor<T> {

    fun visit(e: Expr): T = when (e) {
        is And -> visit(e)
        is ArrayLiteral -> visit(e)
        is Binary -> visit(e)
        is Bool -> visit(e)
        is Call -> visit(e)
        is Comparison -> visit(e)
        is NumsByte -> visit(e)
        is NumsDouble -> visit(e)
        is NumsFloat -> visit(e)
        is NumsInt -> visit(e)
        is NumsShort -> visit(e)
        is Or -> visit(e)
        is NumsPath -> visit(e)
        is StringLiteral -> visit(e)
        is Unary -> visit(e)
        is TextId -> visit(e)
    }

    fun visit(number: NumsDouble): T
    fun visit(number: NumsInt): T
    fun visit(number: NumsShort): T
    fun visit(number: NumsByte): T
    fun visit(number: NumsFloat): T
    fun visit(stringLiteral: StringLiteral): T
    fun visit(binary: Binary): T
    fun visit(cmp: Comparison): T
    fun visit(unary: Unary): T
    fun visit(bool: Bool): T
    fun visit(textId: TextId): T
    fun visit(and: And): T
    fun visit(or: Or): T
    fun visit(call: Call): T
    fun visit(arrayLiteral: ArrayLiteral): T
    fun visit(path: NumsPath): T
}

interface StatementVisitor<T> {
    fun visit(fn: FFunction): T
    fun visit(iif: Iif): T
    fun visit(loop: Loop): T
    fun visit(expressionStatement: ExpressionStatement): T
    fun visit(block: Block): T
    fun visit(valStmt: Val): T
    fun visit(ret: Return): T
    fun visit(assign: Assign): T
    fun visit(import: Import): T
    fun visit(space: Space): T

    fun visit(dataset: Dataset): T
    fun visit(stmt: Statement): T = when (stmt) {
        is Assign -> visit(stmt)
        is Block -> visit(stmt)
        is ExpressionStatement -> visit(stmt)
        is FFunction -> visit(stmt)
        is Iif -> visit(stmt)
        is Import -> visit(stmt)
        is Loop -> visit(stmt)
        is Return -> visit(stmt)
        Skip -> throw Error("Skip found")
        is Space -> visit(stmt)
        is Dataset -> visit(stmt)
        is Val -> visit(stmt)
        is TraitDeclaration -> visit(stmt)
    }

    fun visit(traitDeclaration: TraitDeclaration): T

}

interface IRVisitor<T> {
    fun visit(insn: Instruction): T
    fun visit(irfn: IRFunction) : T

    fun visit(chunk: Chunk) : T
    fun visit(ir: IR) : T = when (ir) {
        is IRFunction -> visit(ir)
        is Instruction -> visit(ir)
        is Chunk -> visit(ir)
    }
}