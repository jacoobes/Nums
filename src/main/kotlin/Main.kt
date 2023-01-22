import hl.HashLink
import kotlinx.cli.*
import java.util.*

fun main(args: Array<String>) = init(args)
fun init(args: Array<String>) {
    System.loadLibrary("libhl")
//    val property = System.getProperty("java.library.path")
//    val parser = StringTokenizer(property, ";")
//    while (parser.hasMoreTokens()) {
//        System.err.println(parser.nextToken())
//    }
    val hl = HashLink()
    //println(HashLink.Static.hl_type_kind._H_FORCE_INT.getCValue())
    //println(hl.hl_alloc_dynobj())
 //  JNAerator.main(args) //-f -direct -mode Directory  hashlink/src/hl.h
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
}