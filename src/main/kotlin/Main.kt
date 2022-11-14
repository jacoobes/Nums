import com.github.h0tk3y.betterParse.grammar.tryParseToEnd
import com.github.h0tk3y.betterParse.parser.ErrorResult
import com.github.h0tk3y.betterParse.parser.Parsed
import kotlinx.cli.*
import java.io.File
import java.io.FileWriter
typealias NumsNode = Pair<List<FFunction>, List<Import>>
fun main(args: Array<String>) {
    init(args)
}


fun init(args: Array<String>) {
    val parser = ArgParser("nums")
    val input by parser.option(ArgType.String, shortName = "i", description = "Main File").required()
    val output by parser.option(ArgType.String, shortName = "o", description = "Output vasm").required()
    parser.parse(args)
    val fileString = File(input).readText()
    when(val result = NumsGrammar().tryParseToEnd(fileString)) {
        is ErrorResult -> println(result)
        is Parsed -> {
            val fr = FileWriter(output)
            val br = NumsWriter(fr)
            br.use {
                it.writeln(MiniVmNative.core())
                visitor(result.value, it)
            }
        }
    }
}