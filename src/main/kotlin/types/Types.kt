package types

import nodes.TextId


interface Type


sealed class Types {
    object TF64 : Type {
        override fun toString() = "f64"
    }
    object TI64 : Type {
        override fun toString() = "i64"
    }
    object TI32 : Type {
        override fun toString() = "i32"
    }
    object TU8 : Type {
        override fun toString() = "u8"
    }
    object TU16 : Type {
        override fun toString() = "u16"
    }
    object TTxt : Type {
        override fun toString() = "string"
    }
    object TBool : Type {
        override fun toString() = "boolean"
    }
    object TF32 : Type {
        override fun toString() = "f32"
    }
    object TUnit : Type {
        override fun toString() = "void"
    }

    data class TDataSet(val name: String, val elems: List<Type>, val traits : List<Trait> ) : Type {
        override fun toString(): String {
            return "$name(${elems.joinToString(", ")}) implements $traits"
        }
    }
    @JvmInline
    value class TVarT(val name: String): Type {
        override fun toString(): String {
            return name
        }
    }
    data class TFn(val typs: List<Type>, val ret: Type) : Type {
        override fun toString(): String {
            return "(${typs.joinToString(",")}) -> $ret"
        }
    }
    data class Trait(val name : TextId, val impls : List<TFn>) : Type
    object Infer : Type
    data class TArr(val typ : Type): Type
}