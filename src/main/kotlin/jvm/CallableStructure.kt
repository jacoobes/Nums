package jvm

import Semantics
import nodes.Expr
import nodes.FFunction

class CallableStructure(
    private val declaration: FFunction,
    private val closure: Semantics,
) : Callee {
    /**
     * function calling;
     * instantiation of arguments
     */
    override fun call(irVisitor: SemanticVisitor, arguments: List<Expr>): IR {
        val functionEnv = Semantics()
        for ((index, args) in declaration.args.withIndex()) {
            arguments[index].let { functionEnv.addLocal(args.value, isAssignable = false, it) }
        }

//        try {
//            interpreter.executeBlock(declaration.body, functionEnv)
//        } catch (returnStmt: Return) {
//
//            if (isInitializer) return closure.getAt(0, "this")
//            return returnStmt.value
//        }
        return Chunk()
    }

    override fun arity(): Int {
        return declaration.args.size
    }

    override fun toString(): String {
        return declaration.fullName
    }

    /**
     * Binds "this" keyword to the closure environment
     * - Closure environment => the block scope inside function / class
     */
//    fun bind(instance: InstanceOf): Callable {
//        val instanceEnv = Env(closure)
//        instanceEnv.define("this", instance)
//        return Callable(declaration, instanceEnv, isInitializer)
//    }

}