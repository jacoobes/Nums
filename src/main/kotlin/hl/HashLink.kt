package hl

import org.graalvm.nativeimage.c.CContext
import org.graalvm.nativeimage.c.CContext.Directives
import org.graalvm.nativeimage.c.constant.CConstant
import org.graalvm.nativeimage.c.constant.CEnum
import org.graalvm.nativeimage.c.constant.CEnumConstant
import org.graalvm.nativeimage.c.constant.CEnumValue
import org.graalvm.nativeimage.c.function.CFunction
import org.graalvm.nativeimage.c.function.CFunctionPointer
import org.graalvm.nativeimage.c.function.InvokeCFunctionPointer
import org.graalvm.nativeimage.c.struct.AllowWideningCast
import org.graalvm.nativeimage.c.struct.CFieldAddress
import org.graalvm.nativeimage.c.struct.CStruct
import org.graalvm.nativeimage.c.type.CCharPointer
import org.graalvm.nativeimage.c.type.CIntPointer
import org.graalvm.nativeimage.c.type.VoidPointer
import org.graalvm.word.PointerBase
import java.util.*


@CContext(HashLink.Companion.HL::class)
open class HashLink {
    @CEnum("hl_type_kind")
    enum class hl_type_kind {
        HVOID	,
        HUI8	,
        HUI16	,
        HI32	,
        HI64	,
        HF32	,
        HF64	,
        HBOOL	,
        HBYTES	,
        HDYN	,
        HFUN	,
        HOBJ	,
        HARRAY	,
        HTYPE	,
        HREF	,
        HVIRTUAL,
        HDYNOBJ ,
        HABSTRACT,
        HENUM	,
        HNULL	,
        HMETHOD ,
        HSTRUCT	,
        HPACKED ,
        HLAST	,
        @CEnumConstant("_H_FORCE_INT")
        _H_FORCE_INT;

        @CEnumValue
        open external fun getCValue(): Int

        @CEnumValue
        open external fun fromCValue(value : Int): hl_type_kind
    }
    
    @CEnum("DynOp")
    enum class DynOp {
        OpAdd,
        OpSub,
        OpMul,
        OpMod,
        OpDiv,
        OpShl,
        OpShr,
        OpUShr,
        OpAnd,
        OpOr,
        OpXor,
        OpLast;

        @CEnumValue
        open external fun getCValue(): Int

        @CEnumValue
        open external fun fromCValue(value : Int): DynOp
    }
    companion object {

        class HL : Directives {
            override fun getHeaderFiles(): List<String> {
                return Collections.singletonList("<hl.h>")
            }

            override fun getLibraries(): List<String?>? {
                return Arrays.asList("src/main/bin/libhl.dll")
            }

        }

        //HL_VERSION
        @CConstant("HL_VERSION")
        //0x010D00
        protected external fun HL_VERSION() : Int
        //8
        @CConstant("HL_WSIZE")
        protected external fun HL_WSIZE() : Int
        // 1
        @CConstant("IS_64")
        protected external fun IS_64() : Int

        /** *native declaration : hashlink\src\hl.h*  */
        //0xAABBCCDD
        @CConstant("hl_invalid_comparison")
        protected external fun hl_invalid_comparison() : Int

        /** *native declaration : hashlink\src\hl.h*  */
        //0
        @CConstant("MEM_KIND_DYNAMIC")
        protected external fun MEM_KIND_DYNAMIC() : Int

        /** *native declaration : hashlink\src\hl.h*  */
        //1
        @CConstant("MEM_KIND_RAW")
        protected external fun MEM_KIND_RAW() : Int

        /** *native declaration : hashlink\src\hl.h*  */
        //2
        @CConstant("MEM_KIND_NOPTR")
        protected external fun MEM_KIND_NOPTR() : Int

        /** *native declaration : hashlink\src\hl.h*  */
        //3
        @CConstant("MEM_KIND_FINALIZER")
        protected external fun MEM_KIND_FINALIZER(): Int

        /** *native declaration : hashlink\src\hl.h*  */
        //128
        @CConstant("MEM_ALIGN_DOUBLE")
        protected external fun MEM_ALIGN_DOUBLE() : Int

        /** *native declaration : hashlink\src\hl.h*  */
        //256
        @CConstant("MEM_ZERO")
        protected external fun MEM_ZERO (): Int

        /** *native declaration : hashlink\src\hl.h*  */
        //"vcsilfdbBDPOATR??X?N?S"
        @CConstant("TYPE_STR")
        protected external fun TYPE_STR() : String

        /** *native declaration : hashlink\src\hl.h*  */
        //"v"
        @CConstant("_VOID")
        protected external fun _VOID() : String

        /** *native declaration : hashlink\src\hl.h*  */
        // "c"
        @CConstant("_I8")
        protected external fun _I8() : String

        /** *native declaration : hashlink\src\hl.h*  */
        // "s"
        @CConstant("_I16")
        protected external fun _I16() : String

        /** *native declaration : hashlink\src\hl.h*  */
        //"i"
        @CConstant("_I32")
        protected external fun _I32() : String

        /** *native declaration : hashlink\src\hl.h*  */
        //l
        @CConstant("_I64")
        protected external fun _I64() : String

        /** *native declaration : hashlink\src\hl.h*  */
        //f
        @CConstant("_F32")
        protected external fun _F32() : String

        /** *native declaration : hashlink\src\hl.h*  */
        //d
        @CConstant("_F64")
        protected external fun _F64() : String

        /** *native declaration : hashlink\src\hl.h*  */
        //b
        @CConstant("_BOOL")
        protected external fun _BOOL() : String

        /** *native declaration : hashlink\src\hl.h*  */
        //B
        @CConstant("_BYTES")
        protected external fun _BYTES () : String

        /** *native declaration : hashlink\src\hl.h*  */
        //D
        @CConstant("_DYN")
        protected external fun _DYN () : String

        /** *native declaration : hashlink\src\hl.h*  */
        //A
        @CConstant("_ARR")
        protected external fun _ARR () : String

        /** *native declaration : hashlink\src\hl.h*  */
        //T
        @CConstant("_TYPE")
        protected external fun _TYPE() : String

        /** *native declaration : hashlink\src\hl.h*  */
        //S
        @CConstant("_STRUCT")
        protected external fun _STRUCT () : String

        /** *native declaration : hashlink\src\hl.h*  */
        //"O"
        @CConstant("_STRING")
        protected external fun _STRING () : String

        /** *native declaration : hashlink\src\hl.h*  */
        //0x100
        @CConstant("HL_EXC_MAX_STACK")
        protected external fun HL_EXC_MAX_STACK () : Int

        /** *native declaration : hashlink\src\hl.h*  */
        //1
        @CConstant("HL_EXC_RETHROW")
        protected external fun HL_EXC_RETHROW () : Int

        /** *native declaration : hashlink\src\hl.h*  */
        //2
        @CConstant("HL_EXC_CATCH_ALL")
        protected external fun HL_EXC_CATCH_ALL  () : Int

        /** *native declaration : hashlink\src\hl.h*  */
        //4
        @CConstant("HL_EXC_IS_THROW")
        protected external fun HL_EXC_IS_THROW  () : Int

        /** *native declaration : hashlink\src\hl.h*  */
        //16
        @CConstant("HL_THREAD_INVISIBLE")
        protected external fun HL_THREAD_INVISIBLE  () : Int

        /** *native declaration : hashlink\src\hl.h*  */
        //32
        @CConstant("HL_THREAD_PROFILER_PAUSED")
        protected external fun HL_THREAD_PROFILER_PAUSED  () : Int

        /** *native declaration : hashlink\src\hl.h*  */
        //16
        @CConstant("HL_TREAD_TRACK_SHIFT")
        protected external fun HL_TREAD_TRACK_SHIFT  () : Int

        /** *native declaration : hashlink\src\hl.h*  */
        //1
        @CConstant("HL_TRACK_ALLOC")
        protected external fun HL_TRACK_ALLOC  () : Int

        /** *native declaration : hashlink\src\hl.h*  */
        //2
        @CConstant("HL_TRACK_CAST")
        protected external fun HL_TRACK_CAST  () : Int

        /** *native declaration : hashlink\src\hl.h*  */
        //4
        @CConstant("HL_TRACK_DYNFIELD")
        protected external fun HL_TRACK_DYNFIELD  () : Int

        /** *native declaration : hashlink\src\hl.h*  */
        //8
        @CConstant("HL_TRACK_DYNCALL")
        protected external fun HL_TRACK_DYNCALL  () : Int

        /** *native declaration : hashlink\src\hl.h*  */
        //(1 or 2 or 4 or 8)
        @CConstant("HL_TRACK_MASK")
        protected external fun HL_TRACK_MASK  () : Int

        /** *native declaration : hashlink\src\hl.h*  */
        //64
        @CConstant("HL_MAX_EXTRA_STACK")
        protected external fun HL_MAX_EXTRA_STACK () : Int

        @get:CFieldAddress
        lateinit var hlt_void : hl_type
        @get:CFieldAddress
        lateinit var hlt_i32 : hl_type
        @get:CFieldAddress
        lateinit var hlt_i64: hl_type
        @get:CFieldAddress
        lateinit var hlt_f64: hl_type
        @get:CFieldAddress
        lateinit var hlt_f32: hl_type
        @get:CFieldAddress
        lateinit var hlt_dyn : hl_type
        @get:CFieldAddress
        lateinit var hlt_array: hl_type
        @get:CFieldAddress
        lateinit var hlt_bytes: hl_type

        @get:CFieldAddress
        lateinit var hlt_dynobj:hl_type
        @get:CFieldAddress
        lateinit var hlt_bool : hl_type
        @get:CFieldAddress
        lateinit var hlt_abstract: hl_type
    }

    /* Import of a C function pointer type. */
    /**
     *
    * The 'typedef' keyword is used to create a type definition,
    * and precedes the type definition. The type being defined is a function pointer.
    * The name of the function pointer is hl_types_dump, followed by parentheses containing the type definition for
    *the argument that the function pointer takes. In this case, it's a void pointer to a function taking two parameters,
    * a  void pointer and an int. This syntax creates a function pointer type that can be used to point to functions that take
    * a void pointer and an int as arguments, and return void.
    */
    interface hl_types_dump : CFunctionPointer {
        /*
         * Invocation of the function pointer. A call to the function is replaced with an indirect
         * call of the function pointer.
         */
        @InvokeCFunctionPointer
        fun invoke(voidPtr: VoidPointer)
    }
    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    external fun hl_gc_set_dump_types(tdump: hl_types_dump)

    /** Undefined type  */
    @CStruct("hl_condition", isIncomplete = true)
    interface hl_condition : PointerBase

    /** Undefined type  */
    @CStruct("hl_alloc_block", isIncomplete = true)
    interface hl_alloc_block : PointerBase

    /** Undefined type  */
    @CStruct("hl_tls", isIncomplete = true)
    interface hl_tls : PointerBase

    /** Undefined type  */
    @CStruct("hl_thread", isIncomplete = true)
    interface hl_thread : PointerBase

    /** Undefined type  */
    @CStruct("hl_mutex", isIncomplete = true)
    interface hl_mutex : PointerBase

    /** Undefined type  */
    @CStruct("hl_buffer", isIncomplete = true)
    interface hl_buffer : PointerBase

    /** Undefined type  */
    @CStruct("hl_semaphore", isIncomplete = true)
    interface hl_semaphore : PointerBase



    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    external fun uvszprintf(out: CCharPointer, out_size: Int, fmt: CCharPointer, vararg arglist: Any) : Int

//    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
//    external fun hl_type_size(t: Pointer) : Int

//    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
//    external fun hl_gc_alloc_noptr(size : Int)

    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    external fun hl_alloc_array(t : hl_type, size: Int) : varray

    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    external fun hl_alloc_dynamic(t: hl_type): vdynamic

    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    external fun hl_alloc_obj(t: hl_type): vdynamic

    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    external fun hl_alloc_enum(t: hl_type, index: Int): venum

    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    external fun hl_alloc_virtual(t: hl_type): vvirtual

    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    external fun hl_alloc_dynobj(): vdynobj


    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    @AllowWideningCast // typedef unsigned char vbyte
    external fun hl_alloc_bytes(size: Int): CIntPointer

    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    external fun hl_alloc_closure_void(t: hl_type, fvalue: VoidPointer): vclosure

    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    external  fun hl_alloc_closure_ptr( fullt: hl_type,fvalue: VoidPointer, ptr: VoidPointer ): vclosure


}
