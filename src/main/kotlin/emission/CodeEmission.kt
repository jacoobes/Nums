package emission

import IRVisitor
import NumsWriter
import jvm.*
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*
import java.nio.file.Path
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
                        println("no op")
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
        Label()
        when(insn.opcode) {
            ICONST_0, ICONST_1 -> mv!!.visitInsn(insn.opcode)
            IADD, IDIV, ISUB, IMUL -> mv!!.visitInsn(insn.opcode)
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
    override fun visit(varInstr: VarInstruction) {
        mv?.visitVarInsn(varInstr.opcode, varInstr.variableIndex) ?: throw Error("method visitor is null")
    }

    override fun visit(chunk: Chunk) {
        println(chunk.instr.contentToString())
        chunk.instr.forEach(::visit)
    }

}