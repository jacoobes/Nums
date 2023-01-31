package nodes

import ExpressionVisitor
import StatementVisitor
import com.github.h0tk3y.betterParse.lexer.Token

sealed interface Expr : Node {
    fun <T> accept(visitor: ExpressionVisitor<T>): T?
}

enum class ComparisonOps {
    Lt,
    Lte,
    Gt,
    Gte,
    Eq,
    Neq;
}

object Skip : Statement {
    override fun <R> accept(visitor: StatementVisitor<R>): R? = null
}

@JvmInline
value class StringLiteral(val str: String) : Expr {
    fun length() = str.length
    override fun <T> accept(visitor: ExpressionVisitor<T>) = visitor.visit(this)
}

@JvmInline
value class NumsInt(val value: Int) : Expr {
    override fun <T> accept(visitor: ExpressionVisitor<T>) = visitor.visit(this)

    override fun toString(): String {
        return value.toString()
    }
}

@JvmInline
value class Variable(val name: String) : Expr {
    override fun <T> accept(visitor: ExpressionVisitor<T>) = visitor.visit(this)
}

@JvmInline
value class NumsDouble(val value: Double) : Expr {
    override fun <T> accept(visitor: ExpressionVisitor<T>) = visitor.visit(this)

    override fun toString(): String {
        return value.toString()
    }
}

@JvmInline
value class NumsFloat(val value: Float) : Expr {
    override fun <T> accept(visitor: ExpressionVisitor<T>) = visitor.visit(this)

    override fun toString(): String {
        return value.toString()
    }
}

@JvmInline
value class NumsByte(val value: UByte) : Expr {
    override fun <T> accept(visitor: ExpressionVisitor<T>) = visitor.visit(this)

    override fun toString(): String {
        return value.toString()
    }
}

@JvmInline
value class NumsShort(val value: UShort) : Expr {
    override fun <T> accept(visitor: ExpressionVisitor<T>) = visitor.visit(this)

    override fun toString(): String {
        return value.toString()
    }
}

@JvmInline
value class Bool(val bool: Boolean) : Expr {
    override fun <T> accept(visitor: ExpressionVisitor<T>) = visitor.visit(this)
}

data class Unary(val op: Token, val expr: Expr) : Expr {
    override fun <T> accept(visitor: ExpressionVisitor<T>) = visitor.visit(this)
}

data class Binary(val left: Expr, val right: Expr, val op: String) : Expr {
    override fun <T> accept(visitor: ExpressionVisitor<T>) = visitor.visit(this)
}

data class Call(val callee: Variable, val args: List<Expr>) : Expr {
    override fun <T> accept(visitor: ExpressionVisitor<T>) = visitor.visit(this)
    override fun toString(): String {
        return callee.name
    }
}

data class Comparison(val left: Expr, val right: Expr, val op: ComparisonOps) : Expr {
    override fun <T> accept(visitor: ExpressionVisitor<T>) = visitor.visit(this)
}

data class And(val left: Expr, val right: Expr) : Expr {
    override fun <T> accept(visitor: ExpressionVisitor<T>) = visitor.visit(this)
}

data class Or(val left: Expr, val right: Expr) : Expr {
    override fun <T> accept(visitor: ExpressionVisitor<T>) = visitor.visit(this)
}

data class ArrayLiteral(val exprs: List<Expr>) : Expr {
    override fun <T> accept(visitor: ExpressionVisitor<T>) = visitor.visit(this)
}

data class Path(var chain: Path?, val tok: Expr) : Expr {
    override fun <T> accept(visitor: ExpressionVisitor<T>) = visitor.visit(this)

    override fun toString(): String {
        return buildString {
            var cur = chain
            append("$tok")
            while (cur != null) {
                append(":")
                append(cur.tok)
                cur = cur.chain
            }
        }
    }
}
