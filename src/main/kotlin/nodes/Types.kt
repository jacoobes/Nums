package nodes


interface Type


sealed class Types {
    object TF64 : Type
    object TI64 : Type
    object TI32 : Type
    object TU8 : Type
    object TU16 : Type
    object TTxt : Type
    object TBool : Type
    object TF32 : Type
    object TUnit : Type
    @JvmInline
    value class TVarT(val name: String): Type
    data class TFn(val typs: List<Type>, val ret: Type) : Type
    object Infer : Type
    data class TArr(val typ : Type)
}