package hl

import nodes.FFunction
import types.Type

data class BytecodeBlock(val regs: List<Type>, val opcodes: List<Opcode>)

class FunctionBytecodeGenerator {
    val bytecodeBlocks = arrayListOf<Pair<FFunction, BytecodeBlock>>()
    private val opcodes = arrayListOf<Opcode>()
    private val regs = arrayListOf<Type>()
    fun addOp(op : Opcode) {
        opcodes.add(op)
    }

    fun clearAll() {
        regs.clear()
        opcodes.clear()
    }

    fun addReg(type: Type) {
        regs.add(type)
    }

    fun addBytecode(f: FFunction) {
        //takes the accumulated stream of regs and opcodes and clones it. adds this data to the final output
        bytecodeBlocks.add(Pair(f, BytecodeBlock(ArrayList(regs), ArrayList(opcodes))))
    }
}