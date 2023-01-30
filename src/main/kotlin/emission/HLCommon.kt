package emission
import hl.*


fun indexGen(b: (Int) -> Unit, i: Int) {
    if (i < 0) {
        val i = -i
        if (i < 0x2000) {
            b((i shr 8) or 0xA0)
            b(i and 0xFF)
        } else if (i >= 0x20000000) {
            throw IllegalArgumentException("$i too large to write an index")
        } else {
            b((i shr 24) or 0xE0)
            b((i shr 16) and 0xFF)
            b((i shr 8) and 0xFF)
            b(i and 0xFF)
        }
    } else if (i < 0x80) {
        b(i)
    } else if (i < 0x2000) {
        b((i shr 8) or 0x80)
        b(i and 0xFF)
    } else if (i >= 0x20000000) {
        throw IllegalArgumentException("$i too large to write an index")
    } else {
        b((i shr 24) or 0xC0)
        b((i shr 16) and 0xFF)
        b((i shr 8) and 0xFF)
        b(i and 0xFF)
    }
}

fun hlHeader(version: Int) : ByteArray {
    return ("HLB".toByteArray())+ version.toByte()
}

/**
 * Covers most general opcodes that have 1:4 operands
 */
fun writeOps(write: (Int) -> Unit, opCode: Opcode) {
    when(opCode) {
        is NoOperand -> {
            write(opCode.op.cValue)
        }
        is OneOperand -> {
            write(opCode.op.cValue)
        }
        is TwoOperands -> {
            write(opCode.op.cValue)
            write(opCode.p1)
            write(opCode.p2)
        }
        is ThreeOperands -> {
            write(opCode.op.cValue)
            write(opCode.p1)
            write(opCode.p2)
            write(opCode.p3)
        }
        is FourOperands -> {
            write(opCode.op.cValue)
            write(opCode.p1)
            write(opCode.p2)
            write(opCode.p3)
            write(opCode.p4)
        }
    }
}