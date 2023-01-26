package types

import nodes.*
import types.Types.*

class TypeError(message: String) : Error(message)


inline fun typeerror(message: String): Nothing {
    throw TypeError(message)
}

data class CtxItem(val name: String, val type: Type)

//https://github.com/atennapel/bidirectional.js/blob/master/ts-mutable/src/context.ts
class Context(val elements: ArrayList<CtxItem> = arrayListOf()) {
    fun clone(): Context {
        return ArrayList(elements).run(::Context)
    }
    fun add(name : String, type: Type) {
        elements.add(CtxItem(name, type))
    }
    fun indexOf(t: CtxItem): Int {
        return elements.indexOfFirst { t.type == it.type && t.name == it.name }
    }

    fun contains(t: CtxItem): Boolean {
        return indexOf(t) != -1
    }

    fun lookup(ty: Type, name: String): CtxItem? {
        return elements.getOrNull(indexOf(CtxItem(name, ty)))
    }

    fun find(name: String): CtxItem? {
        return elements.find { ci -> ci.name == name }
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

                is Bool -> when (t) {
                    !is TBool -> typeerror(message)
                }

                is StringLiteral -> when (t) {
                    !is TTxt -> typeerror(message)
                }

                is Call -> {
                    val getContext = ctx.find(e.callee.name) ?: throw Error("Unknown symbol to call: ${e.callee}")
                    if (getContext.type !is TFn) typeerror("Tried calling a non function")
                    for ((idx, arg) in getContext.type.typs.withIndex()) {
                        check(
                            arg, e.args.getOrNull(idx)
                                ?: typeerror("Arity of call ${e.callee} invalid: Found ${e.args.size}, expected ${getContext.type.typs}")
                        )
                    }

                }

                else -> typeerror(message)
            }
        }

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
            is Call -> {
                val getContext = ctx.find(e.callee.name) ?: throw Error("Unknown symbol to call: ${e.callee}")
                if (getContext.type !is TFn) typeerror("Tried calling a non function")
                getContext.type.ret
            }
            else -> throw TypeError("Could not infer the type of $e")
        }
    }
}

