data class Local(val name: String, val depth: Int, val registerVal: Int) {
    fun inSameScope(local:Local) : Boolean {
        return name.compareTo(local.name) == 0 && depth == local.depth
    }
}
class Semantics {
    private val locals = arrayListOf<Local>()
    private val registers = HashSet<Int>()
    var scopeDepth = 0
    fun incDepth() = scopeDepth++

    fun decDepth() {
        locals.retainAll { it.depth < scopeDepth }
        scopeDepth--
    }
    fun addLocal(local: String, registerVal: Int) {
        val newLocal = Local(local, scopeDepth, registerVal)
        if(localMatch(newLocal)) throw Error("Already have another variable $local in same scope")
        locals.add(newLocal)
    }
    fun addRegister(): Int {
        registers.add(registers.size)
        return registers.size - 1
    }

    fun topMostReg() : Int {
        return registers.size - 1
    }

    fun removeRegister(el: Int) {
        registers.remove(el)
    }

    fun clearRegisters() {
        registers.clear()
    }
    private fun localMatch(other: Local) : Boolean {
        return locals.any { it.inSameScope(other) }
    }
}