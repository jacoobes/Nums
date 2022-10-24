import com.github.h0tk3y.betterParse.grammar.parseToEnd
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
    val tokenizer = NumsGrammar().parseToEnd(fileString)
    println(tokenizer)
}