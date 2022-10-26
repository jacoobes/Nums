import com.github.h0tk3y.betterParse.grammar.tryParseToEnd
import com.github.h0tk3y.betterParse.parser.ErrorResult
import com.github.h0tk3y.betterParse.parser.Parsed
import com.github.h0tk3y.betterParse.utils.Tuple2
import kotlinx.cli.*
import java.io.File

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
    parser.parse(args)
    val fileString = File(input).readText()
    when(val result = NumsGrammar().tryParseToEnd(fileString)) {
        is ErrorResult -> {
            println(result)
        }
        is Parsed -> {
            visitor(result.value, DefaultStatementVisitor to DefaultExprVisitor)
        }
    }
}