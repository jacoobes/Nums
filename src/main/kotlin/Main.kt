import kotlinx.cli.*
import java.io.File
fun main(args: Array<String>) = init(args)
fun init(args: Array<String>) {
    val parser = ArgParser("nums")
    val input by parser.option(ArgType.String, shortName = "in", description = "Main Entry").required()
    val optimization by parser.option(ArgType.Int, shortName = "O", description = "Optimization level").default(0)
    val out by parser.option(ArgType.String, description = "vasm").required()
    parser.parse(args)
    val entry = File(input)
    val files = ModuleResolver.generateFiles(entry)
    val fileStaticAnalysis = StaticAnalysis()
    for((_, tree) in files) {
        fileStaticAnalysis.start(tree, Semantics())
    }
//    when(val result = NumsGrammar().tryParseToEnd(entry.readText())) {
//        is ErrorResult -> println(result)
//        is Parsed -> {
//            val fr = FileWriter(out)
//            val br = NumsWriter(fr)
//            //ok so the steps to a multi file project
//            // need to parse each file first resolving variables and stuff
//            // this will ensure the user is only using variables and functions in scope
//            // so the module resolve comes last, retrieving the files and bundling into one file for minivm to read
//
//            val mr = ModuleResolver(entry)
//            //mr.depGraph.iterator().forEach { println(it) }
//            br.use {
//                it.writeln(MiniVmNative.core())
//                visitor(result.value, it)
//            }
//        }
//    }
}