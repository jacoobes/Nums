import emission.CodeEmission
import emission.MiniVmNative
import kotlinx.cli.*
import java.io.File
import java.io.FileWriter

fun main(args: Array<String>) = init(args)
fun init(args: Array<String>) {
    val parser = ArgParser("nums")
    val input by parser.option(ArgType.String, shortName = "in", description = "Main Entry").required()
    val optimization by parser.option(ArgType.Int, shortName = "O", description = "Optimization level").default(0)
    val out by parser.option(ArgType.String, description = "vasm").required()
    parser.parse(args)
    ModuleResolver.generateFiles(File(input))
    ModuleResolver.fileImportGraph()
    val br = NumsWriter(FileWriter(out))
    br.use {
        it.write(MiniVmNative.core())
        for((file, tree) in ModuleResolver.depMap) {
            CodeEmission(f=it, curFile = file).start(tree)
        }
    }
}