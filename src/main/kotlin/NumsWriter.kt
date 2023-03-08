import org.objectweb.asm.ClassWriter
import java.io.Closeable
import java.io.DataOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.createFile
import kotlin.io.path.deleteIfExists
import kotlin.io.path.isReadable

class NumsWriter(file: Path) : Closeable {
    private val ds : DataOutputStream
    init {
        file.deleteIfExists()
        file.createFile()
        ds = DataOutputStream(Files.newOutputStream(file, StandardOpenOption.APPEND, StandardOpenOption.WRITE).buffered())
        if (!file.isReadable()) {
            throw Error("Cannot read this file")
        }
    }


    fun classWriter(writeScope: (ClassWriter) -> Unit) {
        val classWriter = ClassWriter(0)
        writeScope(classWriter)
        ds.write(classWriter.toByteArray())
    }
    override fun close() {
        ds.flush()
        ds.close()
    }

}