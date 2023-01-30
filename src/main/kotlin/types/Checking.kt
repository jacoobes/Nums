package types

import nodes.*
import types.Types.*
import kotlin.math.max

class TypeError(message: String) : Error(message)


inline fun typeerror(message: String): Nothing {
    throw TypeError(message)
}
sealed class ContextItem {
    interface CtxItem
//    @JvmInline
//    value class TypeDecl(val id: Variable) : CtxItem
    data class VarDecl(val id: Variable, val type : Type) : CtxItem

    data class FnDecl(val id: Variable, val type: TFn) : CtxItem

    data class ExistentialDeclaration(val id: Int, val type: Type?): CtxItem
    @JvmInline
    value class Marker(val id : Int) : CtxItem
}

//https://github.com/atennapel/bidirectional.js/blob/master/ts-mutable/src/context.ts
class Context(private val elements: ArrayList<ContextItem.CtxItem> = arrayListOf()) {
    private var existentials : Int = 0
    fun clone(): Context {
        return ArrayList(elements).run(::Context)
    }
    fun add(item : ContextItem.CtxItem) {
        // make sure we track the largest id in our environment
        when(item) {
            is ContextItem.ExistentialDeclaration -> {
                existentials = max(item.id+1, existentials)
            }
        }
        elements.add(item)
    }

    fun any(pred : (ContextItem.CtxItem) -> Boolean) : Boolean {
        return elements.any(pred)
    }

    fun contains(t: ContextItem.CtxItem): Boolean {
        return any { t == it }
    }

    fun lookupExistential(id : Int): Type? {
        return elements
            .asSequence()
            .filterIsInstance<ContextItem.ExistentialDeclaration>()
            .first { id == it.id }
            .type
    }

    fun lookupVar(name : Variable): Type? {
        return elements
            .asSequence()
            .filterIsInstance<ContextItem.VarDecl>()
            .firstOrNull { name == it.id }
            ?.type
    }

}

class TypeSolver(val ctx: Context) {

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
                is NumsByte -> when(t) {
                    !is TU8 -> typeerror(message)
                }

                is NumsFloat -> when(t) {
                    !is TF32 -> typeerror(message)
                }

                is Bool -> when (t) {
                    !is TBool -> typeerror(message)
                }

                is StringLiteral -> when (t) {
                    !is TTxt -> typeerror(message)
                }

                is Call -> {
                //    val getContext = ctx.find(e.callee.name) ?: throw Error("Unknown symbol to call: ${e.callee}")
                  //  if (getContext.type !is TFn) typeerror("Tried calling a non function")
                    //if(e.args.size != getContext.type.typs.size) typeerror("Arity of call ${e.callee} invalid: Found ${e.args.size}, expected ${getContext.type.typs}")
                    //for ((idx, arg) in getContext.type.typs.withIndex()) {
                //        check(arg, e.args[idx])
                //    }
                }
                else -> typeerror(message)
            }
        }

    }

    //all primitive types are wellformed
    fun isPrimitive(e : Expr): Boolean {
        return e is NumsShort
                || e is NumsFloat
                || e is NumsInt
                || e is NumsDouble
                || e is StringLiteral
                || e is Bool
    }

    //e -> t
    fun infer(e: Expr): Type {
        return when (e) {
            is NumsInt -> TI32
            is NumsDouble -> TF64
            is NumsShort -> TI32
            is NumsFloat -> TF32
            is Bool -> TBool
            is StringLiteral -> TTxt
            //is Call -> {
           //     val getContext = ctx.find(e.callee.name) ?: throw Error("Unknown symbol to call: ${e.callee}")
             //   if (getContext.type !is TFn) typeerror("Tried calling a non function")
            //    getContext.type.ret
           // }
            else -> throw TypeError("Could not infer the type of $e")
        }
    }
}

