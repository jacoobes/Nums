package hl

import org.graalvm.nativeimage.c.CContext
import org.graalvm.nativeimage.c.CContext.Directives
import org.graalvm.nativeimage.c.constant.CConstant
import org.graalvm.nativeimage.c.constant.CEnum
import org.graalvm.nativeimage.c.constant.CEnumConstant
import org.graalvm.nativeimage.c.constant.CEnumValue
import org.graalvm.nativeimage.c.function.CFunction
import org.graalvm.nativeimage.c.function.CFunctionPointer
import org.graalvm.nativeimage.c.function.CMacroInfo
import org.graalvm.nativeimage.c.function.InvokeCFunctionPointer
import org.graalvm.nativeimage.c.struct.AllowWideningCast
import org.graalvm.nativeimage.c.struct.CField
import org.graalvm.nativeimage.c.struct.CFieldAddress
import org.graalvm.nativeimage.c.struct.CStruct
import org.graalvm.nativeimage.c.type.CCharPointer
import org.graalvm.nativeimage.c.type.CIntPointer
import org.graalvm.nativeimage.c.type.VoidPointer
import org.graalvm.nativeimage.c.type.WordPointer
import org.graalvm.word.PointerBase
import org.graalvm.word.UnsignedWord
import java.util.*


@CContext(HashLink.Static.HL::class)
open class HashLink {

    companion object Static {

        class HL : Directives {
            override fun getHeaderFiles(): List<String> {
                return Collections.singletonList("<C:\\Users\\jacob\\OneDrive\\Desktop\\Projects\\Nums\\src\\main\\bin\\hl.h>")
            }

//            override fun getLibraries(): List<String?>? {
//                return Arrays.asList("src/main/bin/libhl.dll")
//            }

        }

        @CEnum
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
            _H_FORCE_INT;

            @CEnumValue
            external fun getCValue(): Int

        }
        @CEnum
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
            external fun getCValue(): Int

        }
        //HL_VERSION
        @JvmStatic
        @CConstant("HL_VERSION")
        //0x010D00
        protected external fun HL_VERSION() : Int
        //8
        @JvmStatic
        @CConstant("HL_WSIZE")
        protected external fun HL_WSIZE() : Int
        // 1
        @JvmStatic
        @CConstant("IS_64")
        protected external fun IS_64() : Int

        /** *native declaration : hashlink\src\hl.h*  */
        //0xAABBCCDD
        @JvmStatic
        @CConstant("hl_invalid_comparison")
        protected external fun hl_invalid_comparison() : Int

        /** *native declaration : hashlink\src\hl.h*  */
        //0
        @JvmStatic
        @CConstant("MEM_KIND_DYNAMIC")
        protected external fun MEM_KIND_DYNAMIC() : Int

        /** *native declaration : hashlink\src\hl.h*  */
        //1
        @JvmStatic
        @CConstant("MEM_KIND_RAW")
        protected external fun MEM_KIND_RAW() : Int

        /** *native declaration : hashlink\src\hl.h*  */
        //2
        @JvmStatic
        @CConstant("MEM_KIND_NOPTR")
        protected external fun MEM_KIND_NOPTR() : Int

        /** *native declaration : hashlink\src\hl.h*  */
        //3
        @JvmStatic
        @CConstant("MEM_KIND_FINALIZER")
        protected external fun MEM_KIND_FINALIZER(): Int

        /** *native declaration : hashlink\src\hl.h*  */
        //128
        @JvmStatic
        @CConstant("MEM_ALIGN_DOUBLE")
        protected external fun MEM_ALIGN_DOUBLE() : Int

        /** *native declaration : hashlink\src\hl.h*  */
        //256
        @JvmStatic
        @CConstant("MEM_ZERO")
        protected external fun MEM_ZERO (): Int

        /** *native declaration : hashlink\src\hl.h*  */
        //"vcsilfdbBDPOATR??X?N?S"
        @JvmStatic
        @CConstant("TYPE_STR")
        protected external fun TYPE_STR() : String

        /** *native declaration : hashlink\src\hl.h*  */
        //"v"
        @JvmStatic
        @CConstant("_VOID")
        protected external fun _VOID() : String

        /** *native declaration : hashlink\src\hl.h*  */
        // "c"
        @JvmStatic
        @CConstant("_I8")
        protected external fun _I8() : String

        /** *native declaration : hashlink\src\hl.h*  */
        // "s"
        @JvmStatic
        @CConstant("_I16")
        protected external fun _I16() : String

        /** *native declaration : hashlink\src\hl.h*  */
        //"i"
        @JvmStatic
        @CConstant("_I32")
        protected external fun _I32() : String

        /** *native declaration : hashlink\src\hl.h*  */
        //l
        @JvmStatic
        @CConstant("_I64")
        protected external fun _I64() : String

        /** *native declaration : hashlink\src\hl.h*  */
        //f
        @JvmStatic
        @CConstant("_F32")
        protected external fun _F32() : String

        /** *native declaration : hashlink\src\hl.h*  */
        //d
        @CConstant("_F64")
        protected external fun _F64() : String

        /** *native declaration : hashlink\src\hl.h*  */
        //b
        @JvmStatic
        @CConstant("_BOOL")
        protected external fun _BOOL() : String

        /** *native declaration : hashlink\src\hl.h*  */
        //B
        @JvmStatic
        @CConstant("_BYTES")
        protected external fun _BYTES () : String

        /** *native declaration : hashlink\src\hl.h*  */
        //D
        @JvmStatic
        @CConstant("_DYN")
        protected external fun _DYN () : String

        /** *native declaration : hashlink\src\hl.h*  */
        //A
        @JvmStatic
        @CConstant("_ARR")
        protected external fun _ARR () : String

        /** *native declaration : hashlink\src\hl.h*  */
        //T
        @JvmStatic
        @CConstant("_TYPE")
        protected external fun _TYPE() : String

        /** *native declaration : hashlink\src\hl.h*  */
        //S
        @JvmStatic
        @CConstant("_STRUCT")
        protected external fun _STRUCT () : String

        /** *native declaration : hashlink\src\hl.h*  */
        //"O"
        @JvmStatic
        @CConstant("_STRING")
        protected external fun _STRING () : String

        /** *native declaration : hashlink\src\hl.h*  */
        //0x100
        @JvmStatic
        @CConstant("HL_EXC_MAX_STACK")
        protected external fun HL_EXC_MAX_STACK () : Int

        /** *native declaration : hashlink\src\hl.h*  */
        //1
        @JvmStatic
        @CConstant("HL_EXC_RETHROW")
        protected external fun HL_EXC_RETHROW () : Int

        /** *native declaration : hashlink\src\hl.h*  */
        //2
        @JvmStatic
        @CConstant("HL_EXC_CATCH_ALL")
        protected external fun HL_EXC_CATCH_ALL  () : Int

        /** *native declaration : hashlink\src\hl.h*  */
        //4
        @JvmStatic
        @CConstant("HL_EXC_IS_THROW")
        protected external fun HL_EXC_IS_THROW  () : Int

        /** *native declaration : hashlink\src\hl.h*  */
        //16
        @JvmStatic
        @CConstant("HL_THREAD_INVISIBLE")
        protected external fun HL_THREAD_INVISIBLE  () : Int

        /** *native declaration : hashlink\src\hl.h*  */
        //32
        @JvmStatic
        @CConstant("HL_THREAD_PROFILER_PAUSED")
        protected external fun HL_THREAD_PROFILER_PAUSED  () : Int

        /** *native declaration : hashlink\src\hl.h*  */
        //16
        @JvmStatic
        @CConstant("HL_TREAD_TRACK_SHIFT")
        protected external fun HL_TREAD_TRACK_SHIFT  () : Int

        /** *native declaration : hashlink\src\hl.h*  */
        //1
        @JvmStatic
        @CConstant("HL_TRACK_ALLOC")
        protected external fun HL_TRACK_ALLOC  () : Int

        /** *native declaration : hashlink\src\hl.h*  */
        //2
        @JvmStatic
        @CConstant("HL_TRACK_CAST")
        protected external fun HL_TRACK_CAST  () : Int

        /** *native declaration : hashlink\src\hl.h*  */
        //4
        @JvmStatic
        @CConstant("HL_TRACK_DYNFIELD")
        protected external fun HL_TRACK_DYNFIELD  () : Int

        /** *native declaration : hashlink\src\hl.h*  */
        //8
        @JvmStatic
        @CConstant("HL_TRACK_DYNCALL")
        protected external fun HL_TRACK_DYNCALL  () : Int

        /** *native declaration : hashlink\src\hl.h*  */
        //(1 or 2 or 4 or 8)
        @JvmStatic
        @CConstant("HL_TRACK_MASK")
        protected external fun HL_TRACK_MASK  () : Int

        /** *native declaration : hashlink\src\hl.h*  */
        //64
        @JvmStatic
        @CConstant("HL_MAX_EXTRA_STACK")
        protected external fun HL_MAX_EXTRA_STACK () : Int
//
//        @get:CFieldAddress
//        lateinit var hlt_void : hl_type
//        @get:CFieldAddress
//        lateinit var hlt_i32 : hl_type
//        @get:CFieldAddress
//        lateinit var hlt_i64: hl_type
//        @get:CFieldAddress
//        lateinit var hlt_f64: hl_type
//        @get:CFieldAddress
//        lateinit var hlt_f32: hl_type
//        @get:CFieldAddress
//        lateinit var hlt_dyn : hl_type
//        @get:CFieldAddress
//        lateinit var hlt_array: hl_type
//        @get:CFieldAddress
//        lateinit var hlt_bytes: hl_type
//
//        @get:CFieldAddress
//        lateinit var hlt_dynobj:hl_type
//        @get:CFieldAddress
//        lateinit var hlt_bool : hl_type
//        @get:CFieldAddress
//        lateinit var hlt_abstract: hl_type
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
    external fun uvszprintf(out: CCharPointer, out_size: Int, fmt: CCharPointer, vararg arglist: List<Any>) : Int

    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    external fun hl_type_size(t: hl_type) : Int

    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    external fun hl_gc_alloc_noptr(size : Int)

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

    /**
     * struct _hl_field_lookup {
        hl_type *t;
        int hashed_name;
        int field_index; // negative or zero : index in methods
    };
     */
    @CStruct("hl_field_lookup")
    interface hl_field_lookup : PointerBase {
        @CFieldAddress
        //@set:CFieldAddress
        fun t(): hl_type

        @CField
        // @set:CField
        fun hashed_name(): Int

        // negative or zero : index in methods
        @CField
        // @set:CField
        fun field_index(): Int
    }

    @CStruct("hl_alloc_block", isIncomplete = true)
    interface hl_alloc_block: PointerBase
    //typedef struct { hl_alloc_block *cur; } hl_alloc;
    @CStruct("hl_alloc")
    interface hl_alloc : PointerBase {
        @CField
        fun cur (): hl_alloc_block
    }

    /**
     * typedef struct {
    const uchar *name;
    int nconstructs;
    hl_enum_construct *constructs;
    void **global_funue;
    } hl_type_enum;
     */
    @CStruct
    interface hl_type_enum : PointerBase {
        @CField
//    @set:CField
        //const uchar
        fun name() : WordPointer

        @CField
//    @set:CField
        fun nconstructs() : Int

        @CFieldAddress
//    @set:CFieldAddress
        fun constructs(): hl_enum_construct

        @CField
//    @set:CField
        //void **
        fun global_value(): WordPointer
    }


    /**
     *
    typedef struct {
    const uchar *name;
    int nparams;
    hl_type **params;
    int size;
    bool hasptr;
    int *offsets;
    } hl_enum_construct;
     */
    @CStruct
    interface hl_enum_construct : PointerBase {
        @CField
//    @set:CField
        //const uchar
        fun name(): WordPointer

        @CField
//    @set:CField
        fun nparams() : Int

        @CFieldAddress
//     @set:CFieldAddress
        //hl_type **params;
        fun params() : WordPointer

        @CField
//     @set:CField
        fun size() : Int

        @CField
        //    @set:CField
        fun hasptr(): Boolean

        @CField
//     @set:CField
        fun offsets(): CIntPointer

    }

    /**
     * typedef struct {
    hl_alloc alloc;
    void **functions_ptrs;
    hl_type **functions_types;
    } hl_module_context;
     */
    @CStruct
    interface hl_module_context : PointerBase {
        @CFieldAddress
        //  @set:CFieldAddress
        fun alloc () : HashLink.hl_alloc

        //void **function_ptrs
        @CFieldAddress
        //   @set:CFieldAddress
        fun functions_ptrs (): WordPointer

        //hl_type **functions_types;
        @CFieldAddress
        //  @set:CFieldAddress
        fun functions_types(): WordPointer
    }

    /**
     * typedef struct {
    void *ptr;
    hl_type *closure;
    int fid;
    } hl_runtime_binding;
     */
    @CStruct
    interface hl_runtime_binding : PointerBase {
        @CField
        //   @set:CField
        fun ptr() : VoidPointer

        @CFieldAddress
        //  @set:CFieldAddress
        fun closure() : hl_type

        @CField
        //  @set:CField
        fun fid(): Int
    }

    /**
     * struct hl_runtime_obj {
    hl_type *t;
    // absolute
    int nfields;
    int nproto;
    int size;
    int nmethods;
    int nbindings;
    bool hasPtr;
    void **methods;
    int *fields_indexes;
    hl_runtime_binding *bindings;
    hl_runtime_obj *parent;
    const uchar *(*toStringFun)( vdynamic *obj );
    int (*compareFun)( vdynamic *a, vdynamic *b );
    vdynamic *(*castFun)( vdynamic *obj, hl_type *t );
    vdynamic *(*getFieldFun)( vdynamic *obj, int hfield );
    // relative
    int nlookup;
    int ninterfaces;
    hl_field_lookup *lookup;
    int *interfaces;
    };
     */
    @CStruct
    interface hl_runtime_obj : PointerBase {
        @CField
        //   @set:CField
        fun nfields() : Int

        @CField
//    @set:CField
        fun nproto(): Int

        @CField
//    @set:CField
        fun size(): Int

        @CField
//    @set:CField
        fun nmethods(): Int

        @CField
//    @set:CField
        fun nbindings() : Int

        @CField
//    @set:CField
        fun hasPtr(): Boolean

        @CField
        //   @set:CField
        // void **methods
        fun methods() : WordPointer

        @CField
        //   @set:CField
        fun fields_indexes() : CIntPointer

        @CFieldAddress
//    @set:CFieldAddress
        fun bindings() : hl_runtime_binding

        @CFieldAddress
//    @set:CFieldAddress
        fun parent() : hl_runtime_obj

        @CFieldAddress
//    @set:CFieldAddress
        // const uchar *(*toStringFun)( vdynamic *obj );
        fun toStringFun (): WordPointer //pointer to a pointer of a function

        @CFieldAddress
//    @set:CFieldAddress
        //int (*compareFun)( vdynamic *a, vdynamic *b );
        fun compareFun() : CFunctionPointer

        @CFieldAddress
//    @set:CFieldAddress
        //  vdynamic *(*castFun)( vdynamic *obj, hl_type *t );
        fun castFun() : WordPointer

        @CFieldAddress
//    @set:CFieldAddress
        //   vdynamic *(*getFieldFun)( vdynamic *obj, int hfield );
        fun getFieldFun() : WordPointer

        @CField
        //   @set:CField
        fun nlookup(): Int

        @CField
//    @set:CField
        fun ninterfaces(): Int

        @CField
//    @set:CField
        fun interfaces(): CIntPointer
    }

    /**
     * struct hl_type {
    hl_type_kind kind;
    union {
    const uchar *abs_name;
    hl_type_fun *fun;
    hl_type_obj *obj;
    hl_type_enum *tenum;
    hl_type_virtual *virt;
    hl_type	*tparam;
    };
    void **vobj_proto;
    unsigned int *mark_bits;
    };
     */
    @CStruct
    interface hl_type : PointerBase {
        @CFieldAddress("kind")
        //    @set:CFieldAddress("kind")
        fun kind(): CIntPointer

        //anonymous union
        @CField
        //    @set:CField
        //const uchar
        fun abs_name (): WordPointer

        @CFieldAddress("fun")
        //   @set:CFieldAddress("fun")
        fun funn(): hl_type_fun

        @CFieldAddress
        //   @set():CFieldAddress
        fun obj (): hl_type_obj

        @CFieldAddress
        //    @set:CFieldAddress
        fun tenum() : HashLink.hl_type_enum

        @CFieldAddress
        //    @set:CFieldAddress
        fun virt() : hl_type_virtual

        @CFieldAddress
//     @set:CFieldAddress
        fun tparam(): hl_type
        //end anonymous union
        @CField
//     @set:CField
        //unsigned int *mark_bits
        fun mark_bits (): WordPointer
        @CFieldAddress
        fun vobj_proto (): WordPointer

    }

    @CStruct
    interface vvirtual : PointerBase {
        @CFieldAddress
//     @set:CFieldAddress
        fun t(): hl_type

        @CFieldAddress
        //    @set:CFieldAddress
        fun value(): vdynamic

        @CFieldAddress
//     @set:CFieldAddress
        fun next(): vvirtual
    }

    @CStruct
    interface hl_type_fun : PointerBase {
        @CFieldAddress
        //hl_type
        fun args(): WordPointer
        @CFieldAddress
        fun ret (): hl_type
        @CFieldAddress
        fun parent(): hl_type

        @CFieldAddress
        fun closure_type(): PointerBase

        @CFieldAddress
        fun closure(): PointerBase
    }
//    @CStruct
//    interface closure_type : PointerBase {
//        @CFieldAddress
//        fun kind() : CIntPointer
//        @CField
//        fun p() : VoidPointer
//    }
//    @CStruct
//    interface closure : PointerBase {
//        @CFieldAddress
//        //hl_type
//        fun args(): WordPointer
//        @CFieldAddress
//        fun ret (): hl_type
//        @CField
//        fun nargs(): Int
//        @CFieldAddress
//        fun parent(): hl_type
//    }

    /**
     * typedef struct {
    int nfields;
    int nproto;
    int nbindings;
    const uchar *name;
    hl_type *super;
    hl_obj_field *fields;
    hl_obj_proto *proto;
    int *bindings;
    void **global_value;
    hl_module_context *m;
    hl_runtime_obj *rt;
    } hl_type_obj;
     */
    @CStruct
    interface hl_type_obj : PointerBase {
        @CField
        //    @set:CField
        fun nfields (): Int
        @CField
//     @set:CField
        fun nproto() : Int
        @CField
//     @set:CField
        fun nbindings() : Int
        @CField
//     @set:CField
        fun name (): WordPointer

        @CFieldAddress("super")
        fun ssuper(): hl_type

        @CFieldAddress
        //    @set:CFieldAddress
        fun fields (): hl_obj_field

        @CFieldAddress
        //   @set:CFieldAddress
        fun proto (): hl_obj_proto

        @CField
        //    @set:CField
        fun bindings (): CIntPointer

        @CField
        //void**
//     @set:CField
        fun global_value (): WordPointer

        @CFieldAddress
//     @set:CFieldAddress
        fun m (): hl_module_context

        @CFieldAddress
//     @set:CFieldAddress
        fun rt() : hl_runtime_obj

    }
    @CStruct
    interface hl_obj_field : PointerBase {
        @CField
        //    @set:CField
        fun name (): WordPointer

        @CFieldAddress
//     @set:CFieldAddress
        fun t() : hl_type

        @CField
//     @set:CField
        fun hashed_name (): Int
    }

    /**
     * typedef struct {
    const uchar *name;
    int findex;
    int pindex;
    int hashed_name;
    } hl_obj_proto;
     */
    @CStruct
    interface hl_obj_proto : PointerBase {
        @CField
        //    @set:CField
        fun name() : WordPointer

        @CField
//     @set:CField
        fun findex (): Int

        @CField
//     @set:CField
        fun pindex (): Int

        @CField
        //    @set:CField
        fun hashed_name() : Int
    }

    /**
     *
     *   typedef struct {
    hl_obj_field *fields;
    int nfields;
    // runtime
    int dataSize;
    int *indexes;
    hl_field_lookup *lookup;
    } hl_type_virtual;
     */
    @CStruct
    interface hl_type_virtual : PointerBase {
        @CFieldAddress
        //    @set:CFieldAddress
        fun fields (): hl_obj_field

        @CField
//     @set:CField
        fun nfields() : Int

        @CField
//     @set:CField
        fun dataSize(): Int

        @CField
//     @set:CField
        fun indexes (): CIntPointer

        @CFieldAddress
//     @set:CFieldAddress
        fun lookup (): hl_field_lookup
    }

    @CStruct
    interface varray: PointerBase {
        @CFieldAddress
        fun t() : hl_type
        @CFieldAddress
        fun at() : hl_type
        @CField
        fun size() : Int
        @CField
        fun __pad() : Int
    }

    @CStruct("vclosure")
    interface vclosure : PointerBase {
        @CFieldAddress
        //   @set:CFieldAddress
        fun t (): hl_type
        @CField("fun")
//     @set:CField("fun")
        fun getFun(): VoidPointer

        @CField
        //    @set:CField
        fun hasValue() : Int

        @[CField CMacroInfo("ifdef HL_64")]
//     @set:[CField CMacroInfo("ifdef HL_64")]
        fun stackCount (): Int

        @CField
        //   @set:CField
        fun value (): VoidPointer
    }

    @CStruct
    interface vclosure_wrapper : PointerBase {
        @CFieldAddress
        fun cl() : vclosure
        @CFieldAddress
        fun wrappedFun() : vclosure //possible fail?
    }

    /**
     * typedef struct {
    hl_type *t;
    #	ifndef HL_64
    int __pad; // force align on 16 bytes for double
    #	endif
    union {
    bool b;
    unsigned char ui8;
    unsigned short ui16;
    int i;
    float f;
    double d;
    vbyte *bytes;
    void *ptr;
    int64 i64;
    } v;
    } vdynamic;
     */
    @CStruct
    interface vdynamic : PointerBase {
        @CFieldAddress
        //@set:CFieldAddress
        fun t(): hl_type

        // @set: [ CField CMacroInfo("ifndef HL_64 // force align on 16 bytes for double")]
//        @CField("__pad")
//        @CMacroInfo("ifndef HL_64 // force align on 16 bytes for double")
//        fun __pad(): Int

        @CFieldAddress
        // @set:CFieldAddress
        // check v struct
        fun v(): PointerBase

    }

//    /**
//     * union {
//    bool b;
//    unsigned char ui8;
//    unsigned short ui16;
//    int i;
//    float f;
//    double d;
//    vbyte *bytes;
//    void *ptr;
//    int64 i64;
//    } v;
//     */
//    @CStruct
//    interface v : PointerBase {
//        @CField
//        //    @set:CField
//        fun b(): Boolean
//
//        @CField("ui8")
////     @set:CField("ui8")
//        fun i8(): Byte
//
//        @AllowWideningCast
//        @CField
////     @set: [ AllowWideningCast CField ]
//        fun ui8() : UnsignedWord
//
//
//        @CField
//        @AllowWideningCast
////     @set: [ CField AllowWideningCast]
//        fun ui16(): UnsignedWord
//
//        @CField
////     @set:CField
//        fun i(): Int
//
//        @CField
////     @set:CField
//        fun f(): Float
//
//        @CField
////     @set:CField
//        fun d(): Double
//
//        @AllowWideningCast
//        @CField
////     @set: [ AllowWideningCast CField ]
//        fun bytes (): Int // typedef unsigned char vbyte;
//
//        @CField
////     @set:CField
//        fun ptr(): VoidPointer
//
//        @CField
//        //    @set:CField
//        fun int64() : Long
//
//   }

    /**
    typedef struct {
    hl_type *t;
    hl_field_lookup *lookup;
    char *raw_data;
    void **values;
    int nfields;
    int raw_size;
    int nvalues;
    vvirtual *virtuals;
    } vdynobj;
     */
    @CStruct
    interface vdynobj : PointerBase {
        @CFieldAddress
        //   @set:CFieldAddress
        fun t(): hl_type

        @CFieldAddress
        //    @set:CFieldAddress
        fun lookup(): HashLink.hl_field_lookup

        @CField
        //   @set:CField
        fun raw_data(): CCharPointer

        @CField
        //    @set:CField
        // void **values
        fun values(): WordPointer

        @CField
        //    @set:CField
        fun nfields(): Int


        @CField
        //    @set:CField
        fun raw_size (): Int


        @CField
        //    @set:CField
        fun nvalues(): Int

        @CFieldAddress
        //    @set:CFieldAddress
        fun virtuals (): vvirtual
    }

    @CStruct
    interface venum: PointerBase {
        @CFieldAddress
        //   @set:CFieldAddress
        fun t(): hl_type

        @CField
        //  @set:CField
        fun index(): Int
    }
}
