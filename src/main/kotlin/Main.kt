
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import emission.NumsEmitter
import jvm.IR
import jvm.SemanticVisitor
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import nodes.FFunction
import nodes.Statement
import org.graalvm.nativeimage.c.function.CLibrary
import java.nio.file.Files
import java.nio.file.Path

fun main(args: Array<String>) = init(args)

fun init(args: Array<String>) {
    val parser = ArgParser("nums")
    val input by parser.argument(ArgType.String, "Main Entry")
    val out by parser.option(ArgType.String).default("out.class")
    parser.parse(args)
    val inputPath = Path.of(System.getProperty("user.dir"), input)
    val pathToHl = Path.of(System.getProperty("user.dir"), out)
    val file = Files.readString(inputPath)
    val entryPoint = NumsGrammar(inputPath).parseToEnd(file).visit()
    NumsEmitter(pathToHl).start(entryPoint)
}

fun List<Statement>.visit() : List<IR?> {
    val sv = SemanticVisitor()
    return sv.start(this)
}