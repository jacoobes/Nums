import com.github.h0tk3y.betterParse.grammar.tryParseToEnd
import com.github.h0tk3y.betterParse.parser.ErrorResult
import com.github.h0tk3y.betterParse.parser.Parsed
import kotlinx.cli.*
import java.io.File
import java.io.FileWriter
fun main(args: Array<String>) = init(args)
fun init(args: Array<String>) {
    val parser = ArgParser("nums")
    val input by parser.option(ArgType.String, shortName = "i", description = "Main Entry").required()
    val output by parser.option(ArgType.String, shortName = "o", description = "vasm").required()
    parser.parse(args)
    val file = File(input)
    when(val result = NumsGrammar().tryParseToEnd(file.readText())) {
        is ErrorResult -> println(result)
        is Parsed -> {
            val fr = FileWriter(output)
            val br = NumsWriter(fr)
            //ok so the steps to a multi file project
            // need to parse each file first resolving variables and stuff
            // this will ensure the user is only using variables and functions in scope
            // so the module resolve comes last, retrieving the files and bundling into one file for minivm to read


            val mr = ModuleResolver(file)
            mr.depGraph.iterator().forEach { println(it) }
            br.use {
                it.writeln(MiniVmNative.core())
                visitor(result.value, it)
            }
        }
    }
}