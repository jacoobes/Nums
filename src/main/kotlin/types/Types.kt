package types

import nodes.TextId


interface Type


sealed class Types {
    object TF64 : Type {
        override fun toString() = "D"
    }
    object TI64 : Type {
        override fun toString() = "J"
    }
    object TI32 : Type {
        override fun toString() = "I"
    }
    object TTxt : Type {
        override fun toString() = "[C"
    }
    object TBool : Type {
        override fun toString() = "Z"
    }
    object TF32 : Type {
        override fun toString() = "F"
    }
    object TUnit : Type {
        override fun toString() = "V"
    }

//    data class TDataSet(val name: String, val elems: List<Type>, val traits : List<Trait> ) : Type {
//        override fun toString(): String {
//            return "(${elems.joinToString(";")}) implements $traits"
//        }
//    }
    @JvmInline
    value class TVarT(val name: String): Type {
        override fun toString(): String {
            return name
        }
    }
    data class TFn(val typs: List<Type>, val ret: Type) : Type {
        override fun toString(): String {
            return "(${typs.joinToString(",")})$ret"
        }
    }
//    data class Trait(val name : TextId, val impls : List<TFn>) : Type
    object Infer : Type
    data class TArr(val typ : Type): Type
}