import nodes.Expr
import nodes.TextId

data class Local(val name: String, val depth: Int, val index: Int, val isAssignable: Boolean, val expr: Expr) {
    fun inSameScope(local:Local) : Boolean {
        return name.compareTo(local.name) == 0 && depth == local.depth
    }
}

//Tracking semantic metadata in a function body
class Semantics {
    val locals = arrayListOf<Local>()
    //The scope depth of function
    // it should return to 0 at the end of a function
    private var scopeDepth = 0
    private var localIndex = 0
    fun incDepth() = scopeDepth++
    //TODO: add proper way to dispose variables semantically and in the actual assembly
    // was thinking get all that were removed and reset register tracker to highest depth?
    fun decDepth() {
        scopeDepth--
        locals.retainAll { it.depth >= scopeDepth }
    }

    fun addLocal(local: String, isAssignable: Boolean, expr: Expr): Local {
        val newLocal = Local(local, scopeDepth, localIndex++, isAssignable, expr)
        if(localMatch(newLocal)) throw Error("Already have another variable $local in same scope")
        locals.add(newLocal)
        return newLocal
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

    fun clearLocals() {
        locals.clear()
        localIndex = 0
    }
}