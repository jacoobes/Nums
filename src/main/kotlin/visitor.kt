import nodes.*

interface ExpressionVisitor<T> {

    fun visit(expr: Expr): T
    fun visit(number: NumsDouble) : T
    fun visit(number: NumsInt): T
    fun visit(number: NumsShort): T
    fun visit(number: NumsByte): T
    fun visit(number: NumsFloat): T

    fun visit(stringLiteral: StringLiteral): T
    fun visit(binary: Binary) : T
    fun visit(cmp: Comparison) : T
    fun visit(unary: Unary): T
    fun visit(bool: Bool) : T
    fun visit(textId: TextId) : T
    fun visit(and: And) : T
    fun visit(or: Or) : T
    fun visit(call: Call): T
    fun visit(arrayLiteral: ArrayLiteral): T
    fun visit(path: Path): T
}

interface StatementVisitor<T> {
    fun visit(fn: FFunction) : T
    fun visit(iif: Iif): T
    fun visit(loop: Loop): T
    fun visit(expressionStatement: ExpressionStatement): T
    fun visit(block: Block): T
    fun visit(valStmt: Val): T
    fun visit(ret: Return): T
    fun visit(assign: Assign): T
    fun visit(import: Import): T
    fun visit(space: Space) : T

    fun visit(dataset: Dataset) : T
    fun visit(stmt: Statement): T

}