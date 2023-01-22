
import org.graalvm.nativeimage.c.type.CCharPointer
import java.util.StringTokenizer

fun showPath() {
    val property = System.getProperty("java.library.path")
    val parser = StringTokenizer(property, ";")
    while (parser.hasMoreTokens()) {
        System.err.println(parser.nextToken())
    }
}

fun main(args: Array<String>) = init(args)
fun init(args: Array<String>) {
    System.loadLibrary("libhl") // idk if i need this
    HashLink.hl_global_init()
    println(Opcodes.hl_op.OAdd.cValue)

   // HashLink.hl_alloc_array(HashLink.hlt_array(), 10)
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
    HashLink.hl_global_free()

}