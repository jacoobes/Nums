
import emission.NumsEmitter
import jvm.IR
import jvm.SemanticVisitor
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import nodes.FFunction
import nodes.Statement
import org.graalvm.nativeimage.c.function.CLibrary
import java.nio.file.Path

fun main(args: Array<String>) = init(args)

@CLibrary("libhl", requireStatic = true)
fun init(args: Array<String>) {
    val parser = ArgParser("nums")
    val input by parser.argument(ArgType.String, "Main Entry")
    val out by parser.option(ArgType.String).default("out.class")
    parser.parse(args)
    val inputPath = Path.of(System.getProperty("user.dir"), input)
    val pathToHl = Path.of(System.getProperty("user.dir"), out)
    ModuleResolver.generateFiles(inputPath)
    val programStart = ModuleResolver.dependencyMap[inputPath]
    val entryPoint = programStart!!.visit()
    NumsEmitter(pathToHl).start(entryPoint)
    //NumsEmitter(pathToHl).start()
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

fun List<Statement>.visit() : List<IR?> {
    val sv = SemanticVisitor()
    return sv.start(this)
}