package nodes

import com.github.h0tk3y.betterParse.lexer.Token

sealed interface Expr:  Node

enum class ComparisonOps {
    Lt,
    Lte,
    Gt,
    Gte,
    Eq,
    Neq;
}

@JvmInline
value class Skip(val n: Nothing? = null) : Statement
@JvmInline
value class StringLiteral(val str: String) : Expr {
    fun length() = str.length
}
@JvmInline
value class NumsInt(val value: Int): Expr {
    override fun toString(): String {
        return value.toString()
    }
}
@JvmInline
value class Variable(val name: String) : Expr
@JvmInline
value class NumsDouble(val value: Double) : Expr {
    override fun toString(): String {
        return value.toString()
    }
}
@JvmInline
value class Bool(val bool: Boolean) : Expr
data class Unary(val op: Token, val expr: Expr) : Expr
data class Binary(val left: Expr, val right: Expr, val op: String) : Expr
data class Call(val callee: Variable, val args: List<Expr>) : Expr {
    override fun toString(): String {
        return callee.name
    }
}

data class Comparison(val left: Expr, val right: Expr, val op: ComparisonOps) : Expr
data class And(val left: Expr, val right: Expr) : Expr
data class Or(val left: Expr, val right: Expr) : Expr
data class ArrayLiteral(val exprs: List<Expr>) : Expr
data class Path(var chain: Path?, val tok: Expr) : Expr {
    override fun toString(): String {
        return buildString {
            var cur = chain
            append("$tok")
            while(cur != null) {
                append(":")
                append(cur.tok)
                cur = cur.chain
            }
        }
    }
}
