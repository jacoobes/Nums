import java.io.Closeable
import java.io.DataOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.*

class NumsWriter(file: Path) : Closeable {
    private val ds : DataOutputStream
    init {
        file.deleteIfExists()
        file.createFile()
        ds = DataOutputStream(Files.newOutputStream(file, StandardOpenOption.APPEND).buffered())
        if(!file.isReadable()) {
            throw Error("Cannot read this file")
        }
    }
    fun writeInt(i : Int) {
        ds.writeInt(i)
    }
    fun write(byte: Int) {
        ds.writeByte(byte)
    }
    fun writeDouble(d: Double) {
        ds.writeDouble(d)
    }
    fun write(type: HashLink.hl_type_kind) {
        ds.writeByte(type.cValue)
    }
    fun write(byteArray: ByteArray) {
        ds.write(byteArray)
    }

    fun writeUTF8(string: String) {
        ds.writeUTF(string)
    }
    override fun close() {
        ds.flush()
        ds.close()
    }

}