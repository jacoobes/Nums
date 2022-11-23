import com.github.h0tk3y.betterParse.grammar.tryParseToEnd
import com.github.h0tk3y.betterParse.parser.ErrorResult
import com.github.h0tk3y.betterParse.parser.Parsed
import kotlinx.cli.*
import java.io.File
import java.io.FileWriter
fun main(args: Array<String>) = init(args)
fun init(args: Array<String>) {
    val parser = ArgParser("nums")
    val input by parser.option(ArgType.String, shortName = "i", description = "Main File").required()
    val output by parser.option(ArgType.String, shortName = "o", description = "Output vasm").required()
    parser.parse(args)
    val file = File(input)
    when(val result = NumsGrammar().tryParseToEnd(file.readText())) {
        is ErrorResult -> println(result)
        is Parsed -> {
            val fr = FileWriter(output)
            val br = NumsWriter(fr)
            val mr = ModuleResolver(file to result.value)
            br.use {
                it.writeln(MiniVmNative.core())
                visitor(result.value, it)
            }
        }
    }
}