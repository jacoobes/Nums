import jvm.CallableStructure
import nodes.FFunction

class EnvironmentManager {
    val callables = HashMap<String, CallableStructure>()
    var entryPoint: CallableStructure? = null

    fun addCallable(name: String, callable: CallableStructure) {
        callables[name] = callable
    }


}