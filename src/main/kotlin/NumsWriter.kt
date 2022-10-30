import java.io.BufferedWriter
import java.io.Writer

class NumsWriter(writer: Writer) : BufferedWriter(writer) {
    fun writeln(string: String, depth:Int = 0) {
        write("${"".padEnd(depth * 2)}$string\n")
    }
}