package hl

import COpcode.hl_op
interface Opcode
data class NoOperand(val op: hl_op) : Opcode
data class OneOperand(val op: hl_op, val p1: Int) : Opcode
data class TwoOperands(val op: hl_op, val p1: Int, val p2: Int) : Opcode
data class ThreeOperands(val op: hl_op, val p1: Int, val p2: Int, val p3: Int) : Opcode
data class FourOperands(val op: hl_op, val p1: Int, val p2: Int, val p3: Int, val p4: Int) : Opcode


fun NOp() = NoOperand(hl_op.ONop)
fun ONull(p1: Int) = OneOperand(hl_op.ONull, p1)
fun OMov(p1: Int, p2: Int) = TwoOperands(hl_op.OMov, p1, p2)
fun OInt(p1: Int, p2: Int) = TwoOperands(hl_op.OInt, p1, p2)
fun OFloat(p1: Int, p2: Int) = TwoOperands(hl_op.OFloat, p1, p2)
fun OBool(p1: Int, p2: Int) = TwoOperands(hl_op.OBool, p1, p2)
fun OString(p1: Int, p2: Int) = TwoOperands(hl_op.OString, p1, p2)

