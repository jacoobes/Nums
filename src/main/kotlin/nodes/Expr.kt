package nodes

import ExpressionVisitor
import com.github.h0tk3y.betterParse.lexer.Token
import types.Types

sealed interface Expr : Node {
    fun <T> accept(visitor: ExpressionVisitor<T>): T
}

enum class ComparisonOps {
    Lt,
    Lte,
    Gt,
    Gte,
    Eq,
    Neq;
}


data class StringLiteral(val str: String, val type: Types.TTxt) : Expr {
    fun length() = str.length
    override fun <T> accept(visitor: ExpressionVisitor<T>) = visitor.visit(this)
}

data class NumsInt(val value: Int, val type: Types.TI32) : Expr {
    override fun <T> accept(visitor: ExpressionVisitor<T>) = visitor.visit(this)

    override fun toString(): String {
        return value.toString()
    }
}

@JvmInline
value class TextId(val value: String) : Expr {
    override fun <T> accept(visitor: ExpressionVisitor<T>) = visitor.visit(this)
    override fun toString(): String {
        return value
    }
}

data class NumsDouble(val value: Double, val type: Types.TF64) : Expr {
    override fun <T> accept(visitor: ExpressionVisitor<T>) = visitor.visit(this)

    override fun toString(): String {
        return value.toString()
    }
}

data class NumsFloat(val value: Float, val type: Types.TF32) : Expr {
    override fun <T> accept(visitor: ExpressionVisitor<T>) = visitor.visit(this)

    override fun toString(): String {
        return value.toString()
    }
}

data class NumsByte(val value: UByte, val type: Types.TU8) : Expr {
    override fun <T> accept(visitor: ExpressionVisitor<T>) = visitor.visit(this)

    override fun toString(): String {
        return value.toString()
    }
}

data class NumsShort(val value: UShort, val type: Types.TU16) : Expr {
    override fun <T> accept(visitor: ExpressionVisitor<T>) = visitor.visit(this)

    override fun toString(): String {
        return value.toString()
    }
}

data class Bool(val bool: Boolean, val type : Types.TBool) : Expr {
    override fun <T> accept(visitor: ExpressionVisitor<T>) = visitor.visit(this)
}

data class Unary(val op: Token, val expr: Expr) : Expr {
    override fun <T> accept(visitor: ExpressionVisitor<T>) = visitor.visit(this)
}

data class Binary(val left: Expr, val right: Expr, val op: String) : Expr {
    override fun <T> accept(visitor: ExpressionVisitor<T>) = visitor.visit(this)
}

data class Call(val callee: TextId, val args: List<Expr>) : Expr {
    override fun <T> accept(visitor: ExpressionVisitor<T>) = visitor.visit(this)
    override fun toString(): String {
        return callee.value
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
