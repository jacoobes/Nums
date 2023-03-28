package emission

import IRVisitor
import NumsWriter
import jvm.*
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type
import java.nio.file.Path
import kotlin.io.path.name
import kotlin.io.path.nameWithoutExtension

class NumsEmitter(
    val path: Path
) : IRVisitor<Unit> {
    private val numsWriter = NumsWriter(path)
    private var mv : MethodVisitor? = null

    fun start(ir: List<IR?>) {
        numsWriter.use {
            numsWriter.classWriter {
                it.visit(
                    JAVA_VERSION,
                    ACC_PUBLIC,
                    path.nameWithoutExtension,
                   null,
                   "java/lang/Object",
                   null
                )
                for(chunk in ir) {
                    if(chunk ==null) {
                        println("null chunk")
                    } else {
                        if(chunk is IRFunction) {
                            mv = it.visitMethod(chunk.fnAccessor, chunk.name.value, chunk.jvmMethodDescriptor, null, null)
                        }
                        visit(chunk)
                    }
                }
                it.visitEnd()
            }
        }
    }
    override fun visit(insn: Instruction) {
        if(mv == null) {
            throw Error("null method visitor")
        }
        when(insn.opcode) {
            BIPUSH -> mv!!.visitIntInsn(insn.opcode, insn.operands[0])
            ICONST_0, ICONST_1 -> mv!!.visitInsn(insn.opcode)
            ISTORE -> mv!!.visitVarInsn(insn.opcode, insn.operands[0])
        }
    }

    override fun visit(irfn: IRFunction) {
        irfn.body.forEach { insn ->
            visit(insn)
        }
        mv?.visitEnd() ?: throw Error("method visitor is null")
        mv?.visitMaxs(256, 2)
    }

    override fun visit(ldc: LDC) {
        if(mv !== null) {
            mv!!.visitLdcInsn(ldc.value)
        }
    }

    override fun visit(chunk: Chunk) {
        println("visited chunk")
        chunk.instr.forEach(::visit)
    }

}