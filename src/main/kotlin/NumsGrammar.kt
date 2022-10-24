import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.lexer.*
import com.github.h0tk3y.betterParse.parser.Parser


interface Node
interface Expr : Node
data class StringLiteral(val str: String) : Expr
data class Number(val value: Int) : Expr
data class Variable(val name: String) : Expr
data class Unary(val op: Token, val expr: Expr) : Expr
data class Binary(val left : Expr, val right: Expr, val op: Token) : Expr
data class Bool(val bool: Boolean) : Expr

class NumsGrammar: Grammar<List<Expr>>() {
    private val num by regexToken("\\d+")
    private val ttrue by literalToken("T")
    private val ffalse by literalToken("F")
    private val and by literalToken("and")
    private val not by literalToken("not")
    private val word by regexToken("[A-Za-z]+")
    private val ws by regexToken("\\s+", ignore = true)
    private val newline by regexToken("[\r\n]+", ignore = true)
    private val comma by literalToken(",")
    private val stringLit by regexToken("\".*?\"")
    private val assign by literalToken("=")
    private val ident by regexToken("[A-Za-z]+[1-9]*")
    private val vval by literalToken("val")
    private val fn by literalToken("fn")
    private val lparen by literalToken("(")
    private val rparen by literalToken(")")
    private val pipe by regexToken("\\|")
    private val plus by regexToken("\\+")
    private val minus by literalToken("-")
    private val div by literalToken("/")
    private val mod by literalToken("%")
    private val timex by regexToken("\\*")
    private val equal by literalToken("==")
    private val nequal by literalToken("!=")
    private val lessEqual by literalToken("<=")
    private val greaterEqual by literalToken(">=")
    private val lt by literalToken("<")
    private val gt by literalToken(">")
    private val stringLiteral by stringLit use { StringLiteral(text.removeSurrounding("\"", "\"")) }
    private val numParser by num use { Number(text.toInt()) }
    private val varParser by word use { Variable(text) }
    private val truthParser by ttrue asJust Bool(true)
    private val falseParser by ffalse asJust Bool(false)
    private val primitiveExpr : Parser<Expr> by (numParser or varParser or truthParser or falseParser or stringLiteral)
    private val grouped by -lparen and parser(this::expr) and -rparen
    private val unary by (not and parser(this::expr)) map { Unary(it.t1.type, it.t2) }
    private val multiplicationOperator by timex or div or mod
    private val multiplicationOrTerm by leftAssociative(primitiveExpr or grouped or unary, multiplicationOperator) { l, o, r ->
        Binary(l, r, o.type)
    }
    private val sumOperators by plus or minus
    private val expr: Parser<Expr> by multiplicationOrTerm
    override val rootParser by oneOrMore(expr)
}
