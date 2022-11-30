import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.Parser
import nodes.*
import java.io.File


class NumsGrammar : Grammar<List<Statement>>() {
    private val num by regexToken("\\d+")
    private val semi by literalToken(";")
    private val ttrue by literalToken("T")
    private val space by literalToken("space")
    private val ffalse by literalToken("F")
    private val and by literalToken("and")
    private val or by literalToken("or")
    private val rreturn by literalToken("return")
    private val loop by literalToken("loop")
    private val not by literalToken("not")
    private val vvar by literalToken("var")
    private val vval by literalToken("val")
    private val iif by literalToken("if")
    private val eels by literalToken("else")
    private val fn by literalToken("fn")
    private val plus by literalToken("+")
    private val minus by literalToken("-")
    private val div by literalToken("/")
    private val mod by literalToken("%")
    private val colon by literalToken(":")
    private val timex by literalToken("*")
    private val equal by literalToken("==")
    private val nequal by literalToken("!=")
    private val lessEqual by literalToken("<=")
    private val greaterEqual by literalToken(">=")
    private val lt by literalToken("<")
    private val gt by literalToken(">")
    private val word by regexToken("[A-Za-z]+[1-9]*")
    private val ws by regexToken("\\s+", ignore = true)
    private val newline by regexToken("[\r\n]+", ignore = true)

    private val compareToKind = mapOf(
        lt to ComparisonOps.Lt,
        gt to ComparisonOps.Gt,
        greaterEqual to ComparisonOps.Gte,
        lessEqual to ComparisonOps.Lte,
        nequal to ComparisonOps.Neq,
        equal to ComparisonOps.Eq
    )

    private val binOpToKind = mapOf(
        plus to "add",
        minus to "sub",
        div to "div",
        mod to "mod",
        timex to "mul"
    )
    private val multiLineComment by regexToken(":>[^<]*(?:[^<:]*)<:", ignore = true)
    private val singleLineComment by regexToken("~~[^\\n]*\\n", ignore = true)
    private val comma by literalToken(",")
    //regex might need adjusting
    private val stringLit by regexToken("\".*?\"")
    private val assign by literalToken("=")
    private val lparen by literalToken("(")
    private val rparen by literalToken(")")
    private val lcurly by literalToken("{")
    private val rcurly by literalToken("}")
    private val pipe by literalToken("|")
    private val stringLiteral by stringLit use { StringLiteral(text.removeSurrounding("\"", "\"")) }
    //only supports int right now
    private val numParser by num use { Number(text) }
    private val varParser by word use { Variable(text) }
    private val truthParser by ttrue asJust Bool("1")
    private val falseParser by ffalse asJust Bool("0")
    //will support local functions in future
    private val fnCall by varParser * -lparen * separatedTerms(parser(::expr), comma, acceptZero = true) * -rparen use {
        Call(t1,t2)
    }
    private val arrayLit by (-lcurly and separatedTerms(
        acceptZero = true,
        separator = comma,
        term = parser(this::expr)
    ) and -rcurly) map { ArrayLiteral(it) }
    private val grouped by -lparen and parser(this::expr) and -rparen
    private val getter by leftAssociative(fnCall or varParser, colon) { l,_,r -> Path(l, r) }
    private val unary by (not and parser(this::expr)) map { Unary(it.t1.type, it.t2) }
    private val primitiveExpr: Parser<Expr> by (
            numParser or
            fnCall or
            getter or
            varParser or
            truthParser or
            falseParser or
            stringLiteral or
            grouped or
            unary or
            arrayLit
            )
    private val multiplicationOperator by timex or div or mod
    private val multiplicationOrTerm by leftAssociative(primitiveExpr, multiplicationOperator) { l, o, r ->  Binary(l, r, binOpToKind[o.type]!!) }
    private val sumOperators by plus or minus
    private val summationOrMul by leftAssociative(multiplicationOrTerm, sumOperators) { l, o, r ->  Binary(l, r, binOpToKind[o.type]!!) }
    private val compareOps by lt or gt or greaterEqual or lessEqual
    private val comparisonOrSummation by (summationOrMul * optional(compareOps * summationOrMul)).map { (left, tail) ->
        tail?.let { (op, r) -> Comparison(left, r, compareToKind[op.type]!! ) } ?: left
    }
    private val equalityOps by equal or nequal
    private val equality by leftAssociative(comparisonOrSummation, equalityOps) { l, o, r ->
        Comparison(l, r, compareToKind[o.type]!!)
    }
    private val andChain by leftAssociative(equality, and) { l, _, r ->
        And(l, r)
    }
    private val orChain by leftAssociative(andChain, or) { l, _, r ->
        Or(l, r)
    }
    private val expr: Parser<Expr> by orChain

    private val exprStatement by expr and -semi map { ExpressionStatement(it) }
    private val assignStmt : Parser<Assign> by (varParser * -assign * parser(::expr) * -semi).map { (t1, t2) -> Assign(t1, t2) }
    private val valStmt by (vval or vvar) * varParser * -assign * exprStatement use {  Val(t1.text == "var", t2, t3.expr) }
    private val iifStmt by (-iif * expr * -lcurly * optional(parser(::statements)) * -rcurly
            * zeroOrMore(-eels * -iif * expr * -lcurly * optional(parser(this::statements)) * -rcurly) *
            (optional(-eels * -lcurly * optional(parser(this::statements)) * -rcurly )).map { it ?: Skip() }
            ).use {
            Iif(t1, t2 ?: Skip(), t3.foldRight(t4) { (elifC, elifB), el -> Iif(elifC, elifB ?: Skip(), el) })
        }

    private val loopCombine by -loop * expr * parser(this::statements) use { Loop(t1, t2) }
    private val block by -lcurly * zeroOrMore(parser(this::statements)) and -rcurly map { Block(it) }
    private val returnStmt by -rreturn * expr * -semi use { Return(this) }
    private val statements: Parser<Statement> by valStmt or assignStmt or returnStmt or iifStmt or block or exprStatement or loopCombine
    private val fnDecl by (-fn * varParser * -lparen * separatedTerms(
        varParser,
        separator = comma,
        acceptZero = true
    ) and -rparen and -lcurly * zeroOrMore(statements) * -rcurly use {
        FFunction(
            t1.name == "main",
            t1,
            t2,
            Block(t3),
        )
    })
    private val import by  ( optional(plus) * separatedTerms(varParser, comma)) * -assign * stringLiteral map { (ns, ids, path) ->
        val f = File(path.str)
        ns?.let {
            if(ids.size == 1) Import(ids, path.str, true, f) else throw Error("A file can only have one namespace")
        } ?: Import(ids, path.str, false, f)
    }
    private val spaceBlock by -space * varParser * -lcurly *  oneOrMore(parser(::topLevel)) * -rcurly map { (n, stmts) -> Space(n,stmts) }
    private val topLevel : Parser<Statement> by fnDecl or import or spaceBlock
    override val rootParser by oneOrMore(topLevel)
}
