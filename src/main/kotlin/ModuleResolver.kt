import com.github.h0tk3y.betterParse.grammar.parseToEnd
import java.io.File

class ModuleResolver(val bw: NumsWriter, val alpha : List<Import>) {
    fun readFile() {
        for(imp in alpha) {
            val nf = File(imp.path)
            if(!nf.exists()) throw Error("File $nf does not exist")
            if(nf.isDirectory) throw Error("No directories allowed")
            if(nf.extension != "nums") throw Error("Only .nums files are allowed")

        }
    }
}