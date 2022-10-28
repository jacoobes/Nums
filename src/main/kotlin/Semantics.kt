data class Local(val name: String, val depth: Int, val registerVal: Int) {
    fun inSameScope(local:Local) : Boolean {
        return name.compareTo(local.name) == 0 && depth == local.depth
    }
}
class Semantics {
    private val locals = arrayListOf<Local>()
    val registers = HashSet<Int>()
    var scopeDepth = 0
    fun incDepth() = scopeDepth++

    fun decDepth() {
        locals.retainAll { it.depth < scopeDepth }
        scopeDepth--
    }

    fun addLocal(local: String, registerVal: Int) {
        val newLocal = Local(local, scopeDepth, registerVal)
        if(localMatch(newLocal)) throw Error("Already have another variable $local in same scope")
        val idx = hasShadow(newLocal)
        if(idx == -1) locals.add(newLocal) else {

            locals[idx] = newLocal
        }
    }

    private fun hasShadow(local: Local) : Int {
        return locals.indexOfFirst { it.name.compareTo(local.name) == 0 && it.depth < local.depth  }
    }
    fun addRegister(): Int {
        registers.add(registers.size)
        return registers.size - 1
    }

    fun topMostReg() : Int {
        return registers.size - 1
    }
    fun getLocal(variable: Variable): Local {
        return locals.find { it.name == variable.name } ?: throw Error("Could not find a variable $variable")
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