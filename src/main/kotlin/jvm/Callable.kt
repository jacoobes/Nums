package jvm

import Semantics
import nodes.FFunction

class Callable(
    private val declaration: FFunction,
    private val closure: Semantics,
    private val isInitializer: Boolean
) : Callee {

    /**
     * function calling;
     * instantiation of arguments
     */
    override fun call(interpreter: SemanticVisitor, arguments: List<Any?>): Any? {
       // val functionEnv = Env(closure)
//        for ((index, args) in declaration.parameters.withIndex()) {
//            arguments[index]?.let { functionEnv.define(args.lexeme, it) }
//        }
//        try {
//            interpreter.executeBlock(declaration.body, functionEnv)
//        } catch (returnStmt: Return) {
//
//            if (isInitializer) return closure.getAt(0, "this")
//            return returnStmt.value
//        }
//
        return null
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