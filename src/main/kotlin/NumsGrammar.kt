import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.Parser
import nodes.*
import types.Type
import types.Types.*
import kotlin.io.path.absolutePathString


class NumsGrammar : Grammar<List<Statement>>() {

    lateinit var currentFile: java.nio.file.Path

    private val numb by regexToken("(([+-]?\\d*\\.*\\d+[eE])?([+-]?\\d*\\.*\\d+)(f64|f32|u16|u8)?)")
    private val semi by literalToken(";")
    private val space by literalToken("space")
    private val data by literalToken("dataset")
    private val vvar by regexToken("(var|val)\\b")
    private val iif by literalToken("if")
    private val eels by literalToken("else")
    private val fn by regexToken("fn\\b")

    private val i32 by regexToken("int\\b")
    private val i64 by regexToken("i64\\b")
    private val u8 by regexToken("u8\\b")
    private val u16 by regexToken("u16\\b")
    private val txt by regexToken("str\\b")
    private val boo by regexToken("bool\\b")
    private val f32 by regexToken("f32\\b")
    private val f64 by regexToken("f64\\b")

    private val ttrue by regexToken("T\\b")
    private val ffalse by regexToken("F\\b")

    private val and by literalToken("and")
    private val or by literalToken("or")
    private val rreturn by literalToken("return")
    private val loop by literalToken("loop")
    private val not by literalToken("not")

    private val word by regexToken("[A-Za-z]+\\w*")

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

    private val _ws by regexToken("\\s+", ignore = true)
    private val _newline by regexToken("[\r\n]+", ignore = true)
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

    private val typesMap = mapOf(
        i32 to TI32,
        i64 to TI64,
        u16 to TU16,
        u8 to TU8,
        boo to TBool,
        txt to TTxt,
        f32 to TF32,
        f64 to TF64,
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

    private val parsedNum by numb map { mat ->
        val endsWith = { s: String -> mat.text.endsWith(s) }
        val getNumber = { s: String -> mat.text.substringBefore(s) }
        when {
            endsWith("f64") -> NumsDouble(getNumber("f64").toDouble(), TF64)
            endsWith("f32") -> NumsFloat(getNumber("f32").toFloat(), TF32)
            endsWith("u8") -> NumsByte(getNumber("u8").toUByte(), TU8)
            endsWith("u16") -> NumsShort(getNumber("u16").toUShort(), TU16)
            else -> try {
                NumsInt(Integer.valueOf(mat.text), TI32)
            } catch (_: Throwable) {
                NumsDouble(mat.text.toDouble(), TF64)
            }
        }
    }
    private val truthParser by ttrue asJust Bool(false, TBool)
    private val falseParser by ffalse asJust Bool(true, TBool)


    private val stringLiteral by stringLit use { StringLiteral(text.removeSurrounding("\"", "\""), TTxt) }
    private val types by (i32 or i64 or u8 or u16 or txt or boo or f32 or f64 or word) map {
        val typ = typesMap[it.type] ?: TVarT(it.text)
        typ
    }
    private val varParser by word use { TextId(text) }

    //will support local functions in future
    private val fnCall by varParser * -lparen * separatedTerms(parser(::expr), comma, acceptZero = true) * -rparen use {
        Call(t1, t2)
    }
    private val arrayLit by (-lcurly and separatedTerms(
        acceptZero = true,
        separator = comma,
        term = parser(this::expr)
    ) and -rcurly) map { ArrayLiteral(it) }
    private val grouped by -lparen and parser(this::expr) and -rparen
    private val getter by rightAssociative(fnCall or varParser, colon) { l, _, r ->
        if (r !is Path) {
            Path(Path(null, r), l)
        } else {
            Path(r, l)
        }
    }
    private val unary by (not and parser(this::expr)) map { Unary(it.t1.type, it.t2) }
    private val primitiveExpr: Parser<Expr> by (
            parsedNum or
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
    private val multiplicationOrTerm by leftAssociative(primitiveExpr, multiplicationOperator) { l, o, r ->
        Binary(
            l,
            r,
            binOpToKind[o.type]!!
        )
    }
    private val sumOperators by plus or minus
    private val summationOrMul by leftAssociative(multiplicationOrTerm, sumOperators) { l, o, r ->
        Binary(
            l,
            r,
            binOpToKind[o.type]!!
        )
    }
    private val compareOps by lt or gt or greaterEqual or lessEqual
    private val comparisonOrSummation by (summationOrMul * optional(compareOps * summationOrMul)).map { (left, tail) ->
        tail?.let { (op, r) -> Comparison(left, r, compareToKind[op.type]!!) } ?: left
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
    private val assignStmt: Parser<Assign> by (varParser * -assign * parser(::expr) * -semi).map { (t1, t2) ->
        Assign(
            t1,
            t2
        )
    }
    private val valStmt by vvar * varParser * optional(-colon * types) and -assign * exprStatement use {

        Val(t1.text == "var", t2, t4.expr, t3 ?: Infer)
    }
    private val iifStmt by (-iif * expr * -lcurly * optional(parser(::statements)) * -rcurly
            * zeroOrMore(-eels * -iif * expr * -lcurly * optional(parser(this::statements)) * -rcurly) *
            (optional(-eels * -lcurly * optional(parser(this::statements)) * -rcurly)).map { it ?: Skip }
            ).use {
            Iif(t1, t2 ?: Skip, t3.foldRight(t4) { (elifC, elifB), el -> Iif(elifC, elifB ?: Skip, el) })
        }

    private val loopCombine by -loop * expr * parser(this::statements) use { Loop(t1, t2) }
    private val block by -lcurly * zeroOrMore(parser(this::statements)) and -rcurly map { Block(it) }
    private val returnStmt by -rreturn * expr * -semi use { Return(this) }
    private val statements: Parser<Statement> by valStmt or assignStmt or returnStmt or iifStmt or block or exprStatement or loopCombine
    private val fnDecl by (-fn * varParser * -lparen * separatedTerms(
        varParser * types,
        separator = comma,
        acceptZero = true
    ) and -rparen and optional(-colon and types) and -lcurly * zeroOrMore(statements) * -rcurly use {
        val typList = arrayListOf<Type>()
        val argsList = arrayListOf<TextId>()
        t2.forEach {
            typList.add(it.t2)
            argsList.add(it.t1)
        }
        FFunction(
            name = t1,
            fullName = currentFile.parent.absolutePathString() + ":" + t1.value,
            args = argsList,
            block = Block(t4),
            type = TFn(typList, t3 ?: TUnit),
        )
    })
    private val import by (optional(plus) * separatedTerms(
        varParser,
        comma
    )) * -assign * stringLiteral map { (ns, ids, path) ->
        val f = java.nio.file.Path.of(path.str)
        ns?.let {
            if (ids.size == 1) Import(ids, path.str, true, f) else throw Error("A file can only have one namespace")
        } ?: Import(ids, path.str, false, f)
    }
    private val dataSet by -data * varParser * -lparen * separatedTerms(varParser * types, separator = comma) * -rparen use {
        val typList = arrayListOf<Type>()
        val argsList = arrayListOf<TextId>()
        t2.forEach {
            typList.add(it.t2)
            argsList.add(it.t1)
        }
        Dataset(name = t1, elements = argsList, type = TDataSet(t1.value, typList))
    }
    private val spaceBlock by -space * varParser * -lcurly * oneOrMore(parser(::topLevel)) * -rcurly map { (n, stmts) ->
        Space(n, stmts)
    }
    private val topLevel: Parser<Statement> by dataSet or fnDecl or import or spaceBlock
    override val rootParser by oneOrMore(topLevel)
}
