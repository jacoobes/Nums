package nodes

import java.io.File
import kotlin.math.abs

sealed class Statement : Node {
    override fun hashCode(): Int {
        return abs(super.hashCode())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }
}

data class ExpressionStatement(val expr: Expr) : Statement()
data class Assign(val tok : Variable, val newVal: Expr) : Statement()
data class Val(val isAssignable: Boolean, val token: Variable, val expr: Expr) : Statement()
data class Block(val stmts: List<Statement>) : Statement()
data class Return(val expr: Expr) : Statement()
data class Iif(val condition: Expr, val thenBody: Statement, val elseBody: Statement) : Statement()
data class Loop(val condition: Expr, val block: Statement) : Statement()
data class FFunction(val main: Boolean, val name: Variable, val args: List<Variable>, val block: Statement) : Statement() {
    override fun equals(other: Any?): Boolean {
        if(other is FFunction) {
            return other.name == name && args.size == other.args.size
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}
data class Space(val name: Variable, val elements: List<Statement>) : Statement()

data class Import(val idents : List<Variable>, val path: String, val isNamespace: Boolean, val file: File) : Statement()