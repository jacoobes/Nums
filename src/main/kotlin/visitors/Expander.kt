package visitors

import ExpressionVisitor
import StatementVisitor
import nodes.*

/**
 * Collects all imported nodes to be processed (explicit or implicit)
 * explicit nodes are those that are imported, implicit nodes are (usually) hidden nodes
 */
class Expander : StatementVisitor<Unit>, ExpressionVisitor<Unit> {
    override fun visit(fn: FFunction) {
        TODO("Not yet implemented")
    }

    override fun visit(iif: Iif) {
        TODO("Not yet implemented")
    }

    override fun visit(loop: Loop) {
        TODO("Not yet implemented")
    }

    override fun visit(expressionStatement: ExpressionStatement) {
        TODO("Not yet implemented")
    }

    override fun visit(block: Block) {
        TODO("Not yet implemented")
    }

    override fun visit(valStmt: Val) {
        TODO("Not yet implemented")
    }

    override fun visit(ret: Return) {
        TODO("Not yet implemented")
    }

    override fun visit(assign: Assign) {
        TODO("Not yet implemented")
    }

    override fun visit(space: Space) {
        TODO("Not yet implemented")
    }

//    override fun visit(dataset: Dataset) {
//        TODO("Not yet implemented")
//    }

    override fun visit(stmt: Statement) {
        TODO("Not yet implemented")
    }

//    override fun visit(traitDeclaration: TraitDeclaration) {
//        TODO("Not yet implemented")
//    }

    override fun visit(expr: Expr) {
        TODO("Not yet implemented")
    }

    override fun visit(number: NumsDouble) {
        TODO("Not yet implemented")
    }

    override fun visit(number: NumsInt) {
        TODO("Not yet implemented")
    }

//    override fun visit(number: NumsShort) {
//        TODO("Not yet implemented")
//    }
//
//    override fun visit(number: NumsByte) {
//        TODO("Not yet implemented")
//    }

    override fun visit(number: NumsFloat) {
        TODO("Not yet implemented")
    }

    override fun visit(stringLiteral: StringLiteral) {
        TODO("Not yet implemented")
    }

    override fun visit(binary: Binary) {
        TODO("Not yet implemented")
    }

    override fun visit(cmp: Comparison) {
        TODO("Not yet implemented")
    }

    override fun visit(unary: Unary) {
        TODO("Not yet implemented")
    }

    override fun visit(bool: Bool) {
        TODO("Not yet implemented")
    }

    override fun visit(textId: TextId) {
        TODO("Not yet implemented")
    }

    override fun visit(and: And) {
        TODO("Not yet implemented")
    }

    override fun visit(or: Or) {
        TODO("Not yet implemented")
    }

    override fun visit(call: Call) {
        TODO("Not yet implemented")
    }

    override fun visit(arrayLiteral: ArrayLiteral) {
        TODO("Not yet implemented")
    }

    override fun visit(path: NumsPath) {
        TODO("Not yet implemented")
    }
}