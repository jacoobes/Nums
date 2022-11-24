import nodes.Expr
import nodes.Variable

data class Local(val name: String, val depth: Int, val registerVal: Int, val isAssignable: Boolean) {
    fun inSameScope(local:Local) : Boolean {
        return name.compareTo(local.name) == 0 && depth == local.depth
    }
}

class Semantics {
    private val locals = arrayListOf<Local>()
    var scopeDepth = 0
    fun incDepth() = scopeDepth++
    //TODO: add proper way to dispose variables semantically and in the actual assembly
    // was thinking get all that were removed and reset register tracker to highest depth?
    fun decDepth() {
        locals.retainAll { it.depth >= scopeDepth }
        scopeDepth--
    }

    fun addLocal(local: String, registerVal: Int, isAssignable: Boolean) {
        val newLocal = Local(local, scopeDepth, registerVal, isAssignable)
        if(localMatch(newLocal)) throw Error("Already have another variable $local in same scope")
        locals.add(newLocal)
    }

    fun getLocal(variable: Variable): Local {
        return locals.find {
            it.name == variable.name && it.depth <= scopeDepth
        } ?: throw Error("Could not find a $variable")
    }

    private fun localMatch(other: Local) : Boolean {
        return locals.any { it.inSameScope(other) }
    }
}