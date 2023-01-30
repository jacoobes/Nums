package nodes

import types.Type
import types.Types

interface Statement : Node

data class ExpressionStatement(val expr: Expr) : Statement
data class Assign(val tok : Variable, val newVal: Expr) : Statement
data class Val(val isAssignable: Boolean, val token: Variable, val expr: Expr, val type : Type) : Statement
data class Block(val stmts: List<Statement>) : Statement
data class Return(val expr: Expr) : Statement
data class Iif(val condition: Expr, val thenBody: Statement, val elseBody: Statement) : Statement
data class Loop(val condition: Expr, val block: Statement) : Statement
data class FFunction(val name: Variable, val args: List<Variable>, val block: Statement, val type : Types.TFn) : Statement {
    fun isMain(): Boolean {
        return name == Variable("main")
    }
}
data class Space(val name: Variable, val elements: List<Statement>) : Statement

data class Import(val idents : List<Variable>, val path: String, val isNamespace: Boolean, val file: java.nio.file.Path) : Statement {
    fun uid(): Int {
        return file.hashCode()
    }
}