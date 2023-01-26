
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import emission.HLEmitter
import hl.SemanticAnalyzer
import hl.hl_code
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
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
    val s = SemanticAnalyzer().apply { start(tree) }

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

//    val parser = ArgParser("nums")
//    val input by parser.option(ArgType.String, shortName = "in", description = "Main Entry").required()
//    val optimization by parser.option(ArgType.Int, shortName = "O", description = "Optimization level").default(0)
//    val out by parser.option(ArgType.String, description = "vasm").required()
//    parser.parse(args)
//    val main = Path.of(input)
//    ModuleResolver.generateFiles(main)
//    //ModuleResolver.createFileImportGraph()
//    val br = NumsWriter(FileWriter(out))
//    val mainTree = ModuleResolver.depMap[main]!!
//    br.use {
//        it.write(MiniVmNative.core())
//        CodeEmission(f=it, curFile = main).start(mainTree)
//    }