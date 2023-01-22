import org.graalvm.nativeimage.c.CContext;
import org.graalvm.nativeimage.c.constant.CConstant;
import org.graalvm.nativeimage.c.constant.CEnum;
import org.graalvm.nativeimage.c.constant.CEnumLookup;
import org.graalvm.nativeimage.c.constant.CEnumValue;
import org.graalvm.nativeimage.c.function.CFunction;
import org.graalvm.nativeimage.c.function.CFunction.Transition;
import org.graalvm.nativeimage.c.function.CFunctionPointer;
import org.graalvm.nativeimage.c.function.InvokeCFunctionPointer;
import org.graalvm.nativeimage.c.struct.AllowWideningCast;
import org.graalvm.nativeimage.c.struct.CField;
import org.graalvm.nativeimage.c.struct.CFieldAddress;
import org.graalvm.nativeimage.c.struct.CStruct;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CIntPointer;
import org.graalvm.nativeimage.c.type.VoidPointer;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.graalvm.word.PointerBase;
import org.graalvm.word.UnsignedWord;

import java.util.List;

@CContext(HashLink.HL.class)
public class HashLink {

    public static class HL implements CContext.Directives {
        @Override
        public List<String> getHeaderFiles() {
            return List.of(
                    "<C:\\Users\\jacob\\OneDrive\\Desktop\\Projects\\Nums\\include\\hl.h>",
                    "<C:\\Users\\jacob\\OneDrive\\Desktop\\Projects\\Nums\\include\\opcodes.h>"
            );
        }

        @Override
        public List<String> getLibraries() {
            return List.of("C:\\Users\\jacob\\OneDrive\\Desktop\\Projects\\Nums\\hashlink\\x64\\Release\\libhl");
        }
    }

    @CFunction(
            transition = Transition.NO_TRANSITION
    )
    public static native int uvszprintf(CCharPointer var0, int var1, CCharPointer var2, List... var3);

    @CConstant("HL_WSIZE")
    public static native int HL_WSIZE();


    @CConstant("IS_64")
    public static native int IS_64();


    @CConstant("hl_invalid_comparison")
    public static native int hl_invalid_comparison();


    @CConstant("MEM_KIND_DYNAMIC")
    public static native int MEM_KIND_DYNAMIC();


    @CConstant("MEM_KIND_RAW")
    public static native int MEM_KIND_RAW();


    @CConstant("MEM_KIND_NOPTR")
    public static native int MEM_KIND_NOPTR();


    @CConstant("MEM_KIND_FINALIZER")
    public static native int MEM_KIND_FINALIZER();


    @CConstant("MEM_ALIGN_DOUBLE")
    public static native int MEM_ALIGN_DOUBLE();


    @CConstant("MEM_ZERO")
    public static native int MEM_ZERO();


    @CConstant("TYPE_STR")

    public static native String TYPE_STR();


    @CConstant("_VOID")

    public static native String _VOID();


    @CConstant("_I8")

    public static native String _I8();


    @CConstant("_I16")

    public static native String _I16();


    @CConstant("_I32")

    public static native String _I32();


    @CConstant("_I64")

    public static native String _I64();


    @CConstant("_F32")

    public static native String _F32();


    @CConstant("_BOOL")

    protected static native String _BOOL();


    @CConstant("_BYTES")

    protected static native String _BYTES();


    @CConstant("_DYN")

    protected static native String _DYN();


    @CConstant("_ARR")

    protected static native String _ARR();


    @CConstant("_TYPE")

    protected static native String _TYPE();


    @CConstant("_STRUCT")

    protected static native String _STRUCT();


    @CConstant("_STRING")

    protected static native String _STRING();


    @CConstant("HL_EXC_MAX_STACK")
    protected static native int HL_EXC_MAX_STACK();


    @CConstant("HL_EXC_RETHROW")
    protected static native int HL_EXC_RETHROW();


    @CConstant("HL_EXC_CATCH_ALL")
    protected static native int HL_EXC_CATCH_ALL();


    @CConstant("HL_EXC_IS_THROW")
    protected static native int HL_EXC_IS_THROW();


    @CConstant("HL_THREAD_INVISIBLE")
    protected static native int HL_THREAD_INVISIBLE();


    @CConstant("HL_THREAD_PROFILER_PAUSED")
    protected static native int HL_THREAD_PROFILER_PAUSED();


    @CConstant("HL_TREAD_TRACK_SHIFT")
    protected static native int HL_TREAD_TRACK_SHIFT();


    @CConstant("HL_TRACK_ALLOC")
    protected static native int HL_TRACK_ALLOC();


    @CConstant("HL_TRACK_CAST")
    protected static native int HL_TRACK_CAST();


    @CConstant("HL_TRACK_DYNFIELD")
    protected static native int HL_TRACK_DYNFIELD();


    @CConstant("HL_TRACK_DYNCALL")
    protected static native int HL_TRACK_DYNCALL();


    @CConstant("HL_TRACK_MASK")
    protected static native int HL_TRACK_MASK();


    @CConstant("HL_MAX_EXTRA_STACK")
    protected static native int HL_MAX_EXTRA_STACK();


    public interface hl_types_dump extends CFunctionPointer {
        @InvokeCFunctionPointer
        void invoke(VoidPointer var1);
    }

    @CStruct(
            value = "hl_condition",
            isIncomplete = true
    )

    public interface hl_condition extends PointerBase {
    }

    @CStruct(
            value = "hl_tls",
            isIncomplete = true
    )

    public interface hl_tls extends PointerBase {
    }

    @CStruct(
            value = "hl_thread",
            isIncomplete = true
    )

    public interface hl_thread extends PointerBase {
    }

    @CStruct(
            value = "hl_mutex",
            isIncomplete = true
    )

    public interface hl_mutex extends PointerBase {
    }

    @CStruct(
            value = "hl_buffer",
            isIncomplete = true
    )

    public interface hl_buffer extends PointerBase {
    }

    @CStruct(
            value = "hl_semaphore",
            isIncomplete = true
    )

    public interface hl_semaphore extends PointerBase {
    }

    @CStruct("hl_field_lookup")

    public interface hl_field_lookup extends PointerBase {
        @CFieldAddress
        hl_type t();

        @CField
        int hashed_name();

        @CField
        int field_index();
    }

    @CStruct(
            value = "hl_alloc_block",
            isIncomplete = true
    )
    public interface hl_alloc_block extends PointerBase {
    }

    @CStruct("hl_alloc")

    public interface hl_alloc extends PointerBase {
        @CField
        hl_alloc_block cur();
    }

    @CStruct

    public interface hl_type_enum extends PointerBase {
        @CField
        WordPointer name();

        @CField
        int nconstructs();

        @CFieldAddress
        hl_enum_construct constructs();

        @CField
        WordPointer global_value();
    }

    @CStruct("hl_enum_construct")
    public interface hl_enum_construct extends PointerBase {
        @CField
        WordPointer name();

        @CField
        int nparams();

        @CFieldAddress
        WordPointer params();

        @CField
        int size();

        @CField
        boolean hasptr();

        @CField
        CIntPointer offsets();
    }

    @CStruct

    public interface hl_module_context extends PointerBase {
        @CFieldAddress
        hl_alloc alloc();

        @CFieldAddress
        WordPointer functions_ptrs();

        @CFieldAddress
        WordPointer functions_types();
    }

    @CStruct
    public interface hl_runtime_binding extends PointerBase {
        @CField
        VoidPointer ptr();

        @CFieldAddress
        hl_type closure();

        @CField
        int fid();
    }

    @CStruct
    public interface hl_runtime_obj extends PointerBase {
        @CField
        int nfields();

        @CField
        int nproto();

        @CField
        int size();

        @CField
        int nmethods();

        @CField
        int nbindings();

        @CField
        boolean hasPtr();

        @CField
        WordPointer methods();

        @CField
        CIntPointer fields_indexes();

        @CFieldAddress
        hl_runtime_binding bindings();

        @CFieldAddress
        hl_runtime_obj parent();

        @CFieldAddress
        WordPointer toStringFun();

        @CFieldAddress
        CFunctionPointer compareFun();

        @CFieldAddress
        WordPointer castFun();

        @CFieldAddress
        WordPointer getFieldFun();

        @CField
        int nlookup();

        @CField
        int ninterfaces();

        @CField
        CIntPointer interfaces();
    }

    @CStruct
    public interface hl_type extends PointerBase {
        @CFieldAddress("kind")
        CIntPointer kind();

        @CField
        WordPointer abs_name();

        @CFieldAddress("fun")
        hl_type_fun funn();

        @CFieldAddress
        hl_type_obj obj();

        @CFieldAddress
        hl_type_enum tenum();

        @CFieldAddress
        hl_type_virtual virt();

        @CFieldAddress
        hl_type tparam();

        @CField
        WordPointer mark_bits();

        @CFieldAddress
        WordPointer vobj_proto();
    }

    @CStruct
    public interface vvirtual extends PointerBase {
        @CFieldAddress
        hl_type t();

        @CFieldAddress
        vdynamic value();

        @CFieldAddress
        vvirtual next();
    }

    @CStruct
    public interface hl_type_fun extends PointerBase {
        @CFieldAddress
        WordPointer args();

        @CFieldAddress
        hl_type ret();

        @CFieldAddress
        hl_type parent();

        @CFieldAddress
        PointerBase closure_type();

        @CFieldAddress
        PointerBase closure();
    }

    @CStruct

    public interface hl_type_obj extends PointerBase {
        @CField
        int nfields();

        @CField
        int nproto();

        @CField
        int nbindings();

        @CField
        WordPointer name();

        @CFieldAddress("super")
        hl_type ssuper();

        @CFieldAddress
        hl_obj_field fields();

        @CFieldAddress
        hl_obj_proto proto();

        @CField
        CIntPointer bindings();

        @CField
        WordPointer global_value();

        @CFieldAddress
        hl_module_context m();

        @CFieldAddress
        hl_runtime_obj rt();
    }

    @CStruct
    public interface hl_obj_field extends PointerBase {
        @CField
        WordPointer name();

        @CFieldAddress
        hl_type t();

        @CField
        int hashed_name();
    }

    @CStruct

    public interface hl_obj_proto extends PointerBase {
        @CField
        WordPointer name();

        @CField
        int findex();

        @CField
        int pindex();

        @CField
        int hashed_name();
    }

    @CStruct

    public interface hl_type_virtual extends PointerBase {
        @CFieldAddress
        hl_obj_field fields();

        @CField
        int nfields();

        @CField
        int dataSize();

        @CField
        CIntPointer indexes();

        @CFieldAddress
        hl_field_lookup lookup();
    }

    @CStruct

    public interface varray extends PointerBase {
        @CFieldAddress
        hl_type t();

        @CFieldAddress
        hl_type at();

        @CField
        int size();

        @CField
        int __pad();
    }

    @CStruct("vclosure")

    public interface vclosure extends PointerBase {
        @CFieldAddress
        hl_type t();

        @CField("fun")
        VoidPointer getFun();

        @CField
        int hasValue();

        @CField
        int stackCount();

        @CField
        VoidPointer value();
    }

    @CStruct
    public interface vclosure_wrapper extends PointerBase {
        @CFieldAddress
        vclosure cl();

        @CFieldAddress
        vclosure wrappedFun();
    }

    @CStruct

    public interface vdynamic extends PointerBase {
        @CFieldAddress
        hl_type t();

        @CFieldAddress
        v v();
    }

    public interface v extends PointerBase {
        @CField
        boolean b();

        @CField("ui8")
        byte i8();

        @AllowWideningCast
        @CField
        UnsignedWord ui8();

        @CField
        @AllowWideningCast
        UnsignedWord ui16();

        @CField
        int i();

        @CField
        float f();

        @CField
        double d();

        @AllowWideningCast
        @CField
        int bytes();

        @CField
        VoidPointer ptr();

        @CField
        long int64();
    }

    @CStruct
    public interface vdynobj extends PointerBase {
        @CFieldAddress
        hl_type t();

        @CFieldAddress
        hl_field_lookup lookup();

        @CField
        CCharPointer raw_data();

        @CField
        WordPointer values();

        @CField
        int nfields();

        @CField
        int raw_size();

        @CField
        int nvalues();

        @CFieldAddress
        vvirtual virtuals();
    }

    @CStruct
    public interface venum extends PointerBase {
        @CFieldAddress
        hl_type t();

        @CField
        int index();
    }

    @CFunction(
            transition = Transition.NO_TRANSITION
    )
    public static native int hl_type_size(hl_type var1);

    @CFunction(
            transition = Transition.NO_TRANSITION
    )
    public static native void hl_gc_alloc_noptr(int var1);

    @CFunction(
            transition = Transition.NO_TRANSITION
    )
    public static native varray hl_alloc_array(hl_type var1, int var2);

    @CFunction(
            transition = Transition.NO_TRANSITION
    )
    public static native vdynamic hl_alloc_dynamic(hl_type var1);

    @CFunction(
            transition = Transition.NO_TRANSITION
    )
    public static native vdynamic hl_alloc_obj(hl_type var1);

    @CFunction(
            transition = Transition.NO_TRANSITION
    )
    public static native venum hl_alloc_enum(hl_type var1, int var2);

    @CFunction(
            transition = Transition.NO_TRANSITION
    )
    public static native vvirtual hl_alloc_virtual(hl_type var1);

    @CFunction(
            transition = Transition.NO_TRANSITION
    )
    public static native vdynobj hl_alloc_dynobj();

    @CFunction(transition = Transition.TO_NATIVE)
    public static native byte hl_alloc_bytes(int var1);

    @CFunction(
            transition = Transition.NO_TRANSITION
    )
    public static native vclosure hl_alloc_closure_void(hl_type var1, VoidPointer var2);

    @CFunction(
            transition = Transition.NO_TRANSITION
    )
    public static native vclosure hl_alloc_closure_ptr(hl_type var1, VoidPointer var2, VoidPointer var3);

    @CFunction(
            transition = Transition.NO_TRANSITION
    )
    public static native void hl_gc_set_dump_types(hl_types_dump var1);

    @CConstant("HL_VERSION")
    protected static native int HL_VERSION();

    @CFunction
    public static native void hl_global_init();

    @CFunction
    public static native void hl_global_free();

//        @CConstant("HL_WSIZE")
//        protected staticnative int HL_WSIZE();


//        @CConstant("IS_64")
//        protected native int IS_64();
//
//
//        @CConstant("hl_invalid_comparison")
//        protected native int hl_invalid_comparison();
//
//        @CConstant("MEM_KIND_DYNAMIC")
//        protected native int MEM_KIND_DYNAMIC();
//
//
//        @CConstant("MEM_KIND_RAW")
//        protected native int MEM_KIND_RAW();
//
//
//        @CConstant("MEM_KIND_NOPTR")
//        public native int MEM_KIND_NOPTR();
//
//
//        @CConstant("MEM_KIND_FINALIZER")
//        protected native int MEM_KIND_FINALIZER();
//
//
//        @CConstant("MEM_ALIGN_DOUBLE")
//        protected native int MEM_ALIGN_DOUBLE();
//
//
//        @CConstant("MEM_ZERO")
//        protected native int MEM_ZERO();
//
//
//        @CConstant("TYPE_STR")
//        
//        protected native String TYPE_STR();
//
//
//        @CConstant("_VOID")
//        
//        protected native String _VOID();
//
//
//        @CConstant("_I8")
//        
//        protected native String _I8();
//
//
//        @CConstant("_I16")
//        
//        protected native String _I16();
//
//
//        @CConstant("_I32")
//        
//        protected native String _I32();
//
//
//        @CConstant("_I64")
//        
//        protected native String _I64();
//
//
//        @CConstant("_F32")
//        
//        protected native String _F32();
//
//        @CConstant("_F64")
//        
//        protected native String _F64();
//
//
//        @CConstant("_BOOL")
//        
//        protected native String _BOOL();
//
//
//        @CConstant("_BYTES")
//        
//        protected native String _BYTES();
//
//
//        @CConstant("_DYN")
//        
//        protected native String _DYN();
//
//
//        @CConstant("_ARR")
//        
//        protected native String _ARR();
//
//        @CConstant("_TYPE")
//        
//        protected native String _TYPE();
//
//        @CConstant("_STRUCT")
//        
//        protected native String _STRUCT();
//
//        @CConstant("_STRING")
//        
//        protected native String _STRING();
//
//
//        @CConstant("HL_EXC_MAX_STACK")
//        protected native int HL_EXC_MAX_STACK();
//
//
//        @CConstant("HL_EXC_RETHROW")
//        protected native int HL_EXC_RETHROW();
//
//
//        @CConstant("HL_EXC_CATCH_ALL")
//        protected native int HL_EXC_CATCH_ALL();
//
//
//        @CConstant("HL_EXC_IS_THROW")
//        protected native int HL_EXC_IS_THROW();
//
//
//        @CConstant("HL_THREAD_INVISIBLE")
//        protected native int HL_THREAD_INVISIBLE();
//
//
//        @CConstant("HL_THREAD_PROFILER_PAUSED")
//        protected native int HL_THREAD_PROFILER_PAUSED();
//
//
//        @CConstant("HL_TREAD_TRACK_SHIFT")
//        protected native int HL_TREAD_TRACK_SHIFT();
//
//        @CConstant("HL_TRACK_ALLOC")
//        protected native int HL_TRACK_ALLOC();
//
//
//        @CConstant("HL_TRACK_CAST")
//        protected native int HL_TRACK_CAST();
//
//
//        @CConstant("HL_TRACK_DYNFIELD")
//        protected native int HL_TRACK_DYNFIELD();
//
//
//        @CConstant("HL_TRACK_DYNCALL")
//        protected native int HL_TRACK_DYNCALL();
//
//
//        @CConstant("HL_TRACK_MASK")
//        protected native int HL_TRACK_MASK();
//
//
//        @CConstant("HL_MAX_EXTRA_STACK")
//        protected native int HL_MAX_EXTRA_STACK();

    @CFieldAddress("hlt_void")

    public static native hl_type hlt_void();

    @CFieldAddress

    public static native hl_type hlt_i32();

    @CFieldAddress

    public static native hl_type hlt_i64();

    @CFieldAddress

    public static native hl_type hlt_f64();

    @CFieldAddress

    public static native hl_type hlt_f32();

    @CFieldAddress

    public static native hl_type hlt_dyn();

    @CFieldAddress

    public static native hl_type hlt_array();

    @CFieldAddress

    public static native hl_type hlt_bytes();

    @CFieldAddress

    public static native hl_type hlt_dynobj();

    @CFieldAddress

    public static native hl_type hlt_bool();

    @CFieldAddress

    public static native hl_type hlt_abstract();

    @CEnum
    enum hl_type_kind {
        HVOID,
        HUI8,
        HUI16,
        HI32,
        HI64,
        HF32,
        HF64,
        HBOOL,
        HBYTES,
        HDYN,
        HFUN,
        HOBJ,
        HARRAY,
        HTYPE,
        HREF,
        HVIRTUAL,
        HDYNOBJ,
        HABSTRACT,
        HENUM,
        HNULL,
        HMETHOD,
        HSTRUCT,
        HPACKED,
        HLAST,
        _H_FORCE_INT;

        @CEnumValue
        public native int getCValue();

        @CEnumLookup
        public static native hl_type_kind fromCValue(int var1);

    }

    @CEnum
    enum DynOp {
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
        public native int getCValue();

        @CEnumLookup
        public static native DynOp fromCValue(int var1);

    }

}

