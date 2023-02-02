package types


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
        override fun toString() = "U16"
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

    data class TDataSet(val name: String, val elems: List<Type> ) : Type {
        override fun toString(): String {
            return "$name(${elems.joinToString(", ")})"
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
    object Infer : Type
    data class TArr(val typ : Type)
}