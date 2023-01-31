
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import emission.HLEmitter
import hl.SemanticVisitor
import hl.hl_code
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import nodes.Statement
import org.graalvm.nativeimage.c.function.CLibrary
import java.nio.file.Files
import java.nio.file.Path
import java.util.*


fun showPath() {
    val property = System.getProperty("java.library.path")
    val parser = StringTokenizer(property, ";")
    while (parser.hasMoreTokens()) {
        System.err.println(parser.nextToken())
    }
}

fun main(args: Array<String>) = init(args)

@CLibrary("libhl", requireStatic = true)
fun init(args: Array<String>) {
    val parser = ArgParser("nums")
    val input by parser.argument(ArgType.String, "Main Entry")
    val out by parser.option(ArgType.String).default("out.hl")
    parser.parse(args)
    val strInput = Files.readString(Path.of(System.getProperty("user.dir"), input))
    val pathToHl = Path.of(System.getProperty("user.dir"), out)
    println(pathToHl)
    val numsGrammar = NumsGrammar()
    val tree = numsGrammar.parseToEnd(strInput)
    tree.visit()
//    NumsWriter(pathToHl).use {
//        val code = hl_code(
//            version = 5,
//            hasDebug = false,
//            ints = s.intTable,
//            types = s.typesTable,
//            floats = s.floatTable,
//            funDecls = s.functionTable,
//            strings = s.stringTable,
//            entryPoint = s.entryPoint
//        )
//        val hl = HLEmitter(it, code)
//        hl.start(tree)
    }

fun List<Statement>.visit() {
    SemanticVisitor().start(this)
}