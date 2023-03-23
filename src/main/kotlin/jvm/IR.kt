package jvm

import nodes.TextId
import org.objectweb.asm.Opcodes


typealias Bytecode = ArrayList<IR>

sealed interface IR


/**
 * IR to represent the transition between source code to jvm.
 */
class Instruction(val opcode: Int, vararg val operands: Int): IR

class Chunk(vararg val instr: IR) : IR

data class LDC(val instr: Int = Opcodes.LDC, val value: Any): IR
class IRFunction(
    val className: String,
    val classAccessors: Int,
    val fnAccessor: Int,
    val main: Boolean,
    val name: TextId,
    val jvmMethodDescriptor: String,
    val body: Bytecode
) : IR



