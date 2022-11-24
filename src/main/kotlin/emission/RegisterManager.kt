package emission

import nodes.Expr

class RegisterManager {
    val registers = HashMap<Int, Int>()
    var registerCount = 0

    fun addRegister(expr: Expr): Int {
        val added = ++registerCount
        registers[expr.hashCode()] = added
        return added
    }

    fun topMostReg() = registerCount

    fun clearRegisters() {
        registers.clear()
    }
}