package jvm

import nodes.Expr

interface Callee {

    fun call(interpreter: SemanticVisitor, arguments: List<Expr> = emptyList()): Any?

    fun arity(): Int
}