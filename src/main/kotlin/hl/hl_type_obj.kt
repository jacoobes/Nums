package hl

import org.graalvm.nativeimage.c.struct.CField
import org.graalvm.nativeimage.c.struct.CFieldAddress
import org.graalvm.nativeimage.c.struct.CStruct
import org.graalvm.nativeimage.c.type.CIntPointer
import org.graalvm.nativeimage.c.type.WordPointer
import org.graalvm.word.PointerBase

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
    @get:CField
    @set:CField
    var nfield : Int
    @get:CField
    @set:CField
    var nproto : Int
    @get:CField
    @set:CField
    var nbindings : Int
    @get:CField
    @set:CField
    var name : WordPointer

    @get:CFieldAddress("super")
    val ssuper: hl_type

    @get:CFieldAddress
    @set:CFieldAddress
    var fields : hl_obj_field

    @get:CFieldAddress
    @set:CFieldAddress
    var proto : hl_obj_proto

    @get:CField
    @set:CField
    var bindings : CIntPointer

    @get:CField
    //void**
    @set:CField
    var global_value : WordPointer

    @get:CFieldAddress
    @set:CFieldAddress
    var m : hl_module_context

    @get:CFieldAddress
    @set:CFieldAddress
    var rt : hl_runtime_obj

}
@CStruct
interface hl_obj_field : PointerBase {
    @get:CField
    @set:CField
    var name : WordPointer

    @get:CFieldAddress
    @set:CFieldAddress
    var t : hl_type

    @get:CField
    @set:CField
    var hashed_name : Int
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
    @get:CField
    @set:CField
    var name : WordPointer

    @get:CField
    @set:CField
    var findex : Int

    @get:CField
    @set:CField
    var pindex : Int

    @get:CField
    @set:CField
    var hashed_name : Int
}