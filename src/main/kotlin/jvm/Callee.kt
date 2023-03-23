package jvm

interface Callee {

    fun call(interpreter: SemanticVisitor, arguments: List<Any?> = emptyList()): Any?

    fun arity(): Int
}