data class Local(val name: String, val depth: Int, val registerVal: Int) {
    fun inSameScope(local:Local) : Boolean {
        return name.compareTo(local.name) == 0 && depth == local.depth
    }
}
class Semantics {
    val locals = arrayListOf<Local>()
    val registers = HashSet<Int>()
    var scopeDepth = 0
    fun incDepth() = scopeDepth++
    //TODO: add proper way to dispose variables semantically and in the actual assembly
    // was thinking get all that were removed and reset register tracker to highest depth?
    fun decDepth() {
        val removed = locals.filter { it.depth >= scopeDepth }
        locals.removeAll(removed.toSet())
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
    fun topMostReg() = registers.size - 1
    fun getLocal(variable: Variable): Local {
        return locals.find {
            it.name == variable.name && it.depth <= scopeDepth
        } ?: throw Error("Could not find a $variable")
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