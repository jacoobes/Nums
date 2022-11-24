package nodes

import com.github.h0tk3y.betterParse.lexer.Token
import kotlin.math.abs

sealed class Expr : Node {
    override fun hashCode(): Int {
        return abs(super.hashCode())
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }
}

enum class ComparisonOps {
    Lt,
    Lte,
    Gt,
    Gte,
    Eq,
    Neq;
}

data class Skip(val n: Nothing? = null) : Statement()
data class StringLiteral(val str: String) : Expr()
data class Number(val value: String) : Expr()
data class Variable(val name: String) : Expr() {
    override fun equals(other: Any?): Boolean {
        return other is Variable && name == other.name
    }
    override fun hashCode(): Int {
        return name.hashCode()
    }
}
data class Unary(val op: Token, val expr: Expr) : Expr()
data class Binary(val left: Expr, val right: Expr, val op: String) : Expr()
data class Call(val callee: Variable, val args: List<Expr>) : Expr()
data class Comparison(val left: Expr, val right: Expr, val op: ComparisonOps) : Expr()
data class And(val left: Expr, val right: Expr) : Expr()
data class Or(val left: Expr, val right: Expr) : Expr()
data class ArrayLiteral(val exprs: List<Expr>) : Expr()
data class Path(val chain: Expr, val tok: Expr) : Expr()
data class Bool(val bool: String) : Expr()
