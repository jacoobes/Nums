package types

import nodes.*
import types.Types.*
import kotlin.math.max

class TypeError(message: String) : Error(message)


fun typeerror(message: String): Nothing {
    throw TypeError(message)
}

sealed class ContextItem {
    interface CtxItem

//  @JvmInline
//  value class TypeDecl(val id: Variable) : CtxItem
    data class VarDecl(val id: TextId, val type : Type) : CtxItem

    data class FnDecl(val id: TextId, val type: TFn) : CtxItem

    data class ExistentialDeclaration(val id: Int, val type: Type?): CtxItem
    @JvmInline
    value class Marker(val id: Int) : CtxItem
}

//https://github.com/atennapel/bidirectional.js/blob/master/ts-mutable/src/context.ts
class Context(private val elements: ArrayList<ContextItem.CtxItem> = arrayListOf()) {
    private var existentials: Int = 0
    fun clone(): Context {
        return ArrayList(elements).run(::Context)
    }

    fun add(item: ContextItem.CtxItem) {
        // make sure we track the largest id in our environment
        when (item) {
            is ContextItem.ExistentialDeclaration -> {
                existentials = max(item.id + 1, existentials)
            }
        }
        elements.add(item)
    }

    fun any(pred: (ContextItem.CtxItem) -> Boolean): Boolean {
        return elements.any(pred)
    }

    fun contains(t: ContextItem.CtxItem): Boolean {
        return any { t == it }
    }

    fun lookupExistential(id: Int): Type? {
        return elements
            .asSequence()
            .filterIsInstance<ContextItem.ExistentialDeclaration>()
            .first { id == it.id }
            .type
    }

    fun lookupVar(name: TextId): Type? {
        return elements
            .asSequence()
            .filterIsInstance<ContextItem.VarDecl>()
            .firstOrNull { name == it.id }
            ?.type
    }

    fun lookupFn(name: TextId): ContextItem.FnDecl? {
        return elements
            .asSequence()
            .filterIsInstance<ContextItem.FnDecl>()
            .firstOrNull { name == it.id }
    }
}

class TypeSolver(val ctx: Context) {
    val env = hashMapOf<TextId, Type>()
    fun tryInfer(e: Expr) {
        check(infer(e), e)
    }

    // t <- e
    fun check(t: Type, e: Expr) {
        val message = "$e is not an $t"
        if (t is Infer) {
            tryInfer(e)
        } else {
            when (e) {
                //most expressions are trivially checked
                is NumsInt -> when (t) {
                    !is TI32 -> typeerror(message)
                }

                is NumsDouble -> when (t) {
                    !is TF64 -> typeerror(message)
                }

                is NumsShort -> when (t) {
                    !is TU16 -> typeerror(message)
                }

                is NumsByte -> when (t) {
                    !is TU8 -> typeerror(message)
                }

                is NumsFloat -> when (t) {
                    !is TF32 -> typeerror(message)
                }

                is Bool -> when (t) {
                    !is TBool -> typeerror(message)
                }

                is StringLiteral -> when (t) {
                    !is TTxt -> typeerror(message)
                }

                is Call -> {
                    val caller = env[e.callee] ?: throw Error("Unknown symbol to call: ${e.callee}")
                    if(caller !is TFn) throw Error("$caller is not a function type ")
                    if (e.args.size != caller.typs.size) typeerror("Arity of call ${e.callee} invalid: Found ${e.args.size}, expected ${caller.typs}")
                    for ((idx, arg) in caller.typs.withIndex()) {
                        check(arg, e.args[idx])
                    }
                    when {
                        t != caller.ret -> typeerror(message)
                    }
                }
                else -> typeerror(message)
            }
        }

    }
    fun isSingleton(e: Expr) : Boolean {
        return e is NumsInt
                || e is NumsDouble
                || e is NumsShort
                || e is NumsFloat
                || e is Bool
                || e is NumsByte
                || e is StringLiteral
    }
    fun isSubtype(t1:Type, t2: Type) : Boolean {
        return when {
            t1 is TF64 && t2 is TF64
            || t1 is TF32 && t2 is TF32
            || t1 is TTxt && t2 is TTxt
            || t1 is TU8 && t2 is TU8
            || t1 is TU16 && t2 is TU16
            || t1 is TUnit && t2 is TUnit -> true
            else -> false
        }
    }
    //all primitive types are wellformed

    //e -> t
    fun infer(e: Expr): Type {
        return when (e) {
            is NumsInt -> TI32
            is NumsDouble -> TF64
            is NumsShort -> TI32
            is NumsFloat -> TF32
            is Bool -> TBool
            is StringLiteral -> TTxt
            is Call -> {
                val getContext = ctx.lookupFn(e.callee) ?: throw Error("Unknown symbol to call: ${e.callee}")
                getContext.type.ret
            }

            else -> throw TypeError("Could not infer the type of $e")
        }
    }
}

