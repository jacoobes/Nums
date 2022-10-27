import com.github.h0tk3y.betterParse.grammar.tryParseToEnd
import com.github.h0tk3y.betterParse.parser.ErrorResult
import com.github.h0tk3y.betterParse.parser.Parsed
import kotlinx.cli.*
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

fun main(args: Array<String>) {
    init(args)
}


fun init(args: Array<String>) {
    println("""
  o          o                                           
 <|\        <|>                                          
 / \\o      / \                                          
 \o/ v\     \o/   o       o   \o__ __o__ __o       __o__ 
  |   <\     |   <|>     <|>   |     |     |>     />  \  
 / \    \o  / \  < >     < >  / \   / \   / \     \o     
 \o/     v\ \o/   |       |   \o/   \o/   \o/      v\    
  |       <\ |    o       o    |     |     |        <\   
 / \        < \   <\__ __/>   / \   / \   / \  _\o__</   
                                              
                                              
    """)
    val parser = ArgParser("nums")
    val input by parser.option(ArgType.String, shortName = "i", description = "Main File").required()
    val output by parser.option(ArgType.String, shortName = "o", description = "Output QBE SSA").required()
    parser.parse(args)
    val fileString = File(input).readText()
    when(val result = NumsGrammar().tryParseToEnd(fileString)) {
        is ErrorResult -> {
            println(result)
        }
        is Parsed -> {
            val fr = FileWriter(output)
            val br = BufferedWriter(fr)
            br.use {
                visitor(result.value, it)
            }
        }
    }
}