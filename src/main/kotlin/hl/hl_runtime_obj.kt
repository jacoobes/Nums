package hl

import org.graalvm.nativeimage.c.function.CFunctionPointer
import org.graalvm.nativeimage.c.struct.CField
import org.graalvm.nativeimage.c.struct.CFieldAddress
import org.graalvm.nativeimage.c.struct.CStruct
import org.graalvm.nativeimage.c.type.CIntPointer
import org.graalvm.nativeimage.c.type.WordPointer
import org.graalvm.word.PointerBase

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
    @get:CField
    @set:CField
    var nfields : Int

    @get:CField
    @set:CField
    var nproto: Int

    @get:CField
    @set:CField
    var size : Int

    @get:CField
    @set:CField
    var nmethods: Int

    @get:CField
    @set:CField
    var nbindings : Int

    @get:CField
    @set:CField
    var hasPtr : Boolean

    @get:CField
    @set:CField
    // void **methods
    var methods : WordPointer

    @get:CField
    @set:CField
    var fields_indexes : CIntPointer

    @get:CFieldAddress
    @set:CFieldAddress
    var bindings : hl_runtime_binding

    @get:CFieldAddress
    @set:CFieldAddress
    var parent : hl_runtime_obj

    @get:CFieldAddress
    @set:CFieldAddress
    // const uchar *(*toStringFun)( vdynamic *obj );
    var toStringFun : WordPointer //pointer to a pointer of a function

    @get:CFieldAddress
    @set:CFieldAddress
    //int (*compareFun)( vdynamic *a, vdynamic *b );
    var compareFun : CFunctionPointer

    @get:CFieldAddress
    @set:CFieldAddress
    //  vdynamic *(*castFun)( vdynamic *obj, hl_type *t );
    var castFun : WordPointer

    @get:CFieldAddress
    @set:CFieldAddress
    //   vdynamic *(*getFieldFun)( vdynamic *obj, int hfield );
    var getFieldFun : WordPointer

    @get:CField
    @set:CField
    var nlookup: Int

    @get:CField
    @set:CField
    var ninterfaces: Int

    @get:CField
    @set:CField
    var interfaces: CIntPointer
}