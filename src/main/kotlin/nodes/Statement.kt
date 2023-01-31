package nodes

import StatementVisitor
import types.Type
import types.Types

interface Statement : Node {
    fun <R> accept(visitor: StatementVisitor<R>) : R?
}

data class ExpressionStatement(val expr: Expr) : Statement {
    override fun <R> accept(visitor: StatementVisitor<R>) = visitor.visit(this)
}

data class Assign(val tok: Variable, val newVal: Expr) : Statement {
    override fun <R> accept(visitor: StatementVisitor<R>) = visitor.visit(this)
}

data class Val(val isAssignable: Boolean, val token: Variable, val expr: Expr, val type: Type) : Statement {
    override fun <R> accept(visitor: StatementVisitor<R>) = visitor.visit(this)
}

data class Block(val stmts: List<Statement>) : Statement {
    override fun <R> accept(visitor: StatementVisitor<R>) = visitor.visit(this)
}

data class Return(val expr: Expr) : Statement {
    override fun <R> accept(visitor: StatementVisitor<R>) = visitor.visit(this)
}

data class Iif(val condition: Expr, val thenBody: Statement, val elseBody: Statement) : Statement {
    override fun <R> accept(visitor: StatementVisitor<R>) = visitor.visit(this)

}

data class Loop(val condition: Expr, val block: Statement) : Statement {
    override fun <R> accept(visitor: StatementVisitor<R>) = visitor.visit(this)
}

data class FFunction(val name: Variable, val args: List<Variable>, val block: Statement, val type: Types.TFn) :
    Statement {
    fun isMain(): Boolean {
        return name == Variable("main")
    }

    override fun <R> accept(visitor: StatementVisitor<R>) = visitor.visit(this)
}

data class Space(val name: Variable, val elements: List<Statement>) : Statement {
    override fun <R> accept(visitor: StatementVisitor<R>) = null
}

data class Import(
    val idents: List<Variable>,
    val path: String,
    val isNamespace: Boolean,
    val file: java.nio.file.Path
) : Statement {
    fun uid(): Int {
        return file.hashCode()
    }

    override fun <R> accept(visitor: StatementVisitor<R>) = visitor.visit(this)
}