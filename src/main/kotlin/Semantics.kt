import nodes.TextId

data class Local(val name: String, val depth: Int, val isAssignable: Boolean) {
    fun inSameScope(local:Local) : Boolean {
        return name.compareTo(local.name) == 0 && depth == local.depth
    }
}

//Tracking semantic metadata in a function body
class Semantics {
    val locals = arrayListOf<Local>()
    //The scope depth of function
    // it should return to 0 at the end of a function
    var scopeDepth = 0
    fun incDepth() = scopeDepth++
    //TODO: add proper way to dispose variables semantically and in the actual assembly
    // was thinking get all that were removed and reset register tracker to highest depth?
    fun decDepth() {
        locals.retainAll { it.depth >= scopeDepth }
        scopeDepth--
    }

    fun addLocal(local: String, isAssignable: Boolean) {
        val newLocal = Local(local, scopeDepth, isAssignable)
        if(localMatch(newLocal)) throw Error("Already have another variable $local in same scope")
        locals.add(newLocal)
    }

    fun getLocal(textId: TextId): Local {
        return locals.findLast {
            it.name == textId.value && it.depth <= scopeDepth
        } ?: throw Error("Could not find a $textId")
    }

    private fun localMatch(other: Local) : Boolean {
        return locals.any { it.inSameScope(other) }
    }

    fun localSize() : Int {
        return locals.size
    }

    fun clearLocals() = locals.clear()
}