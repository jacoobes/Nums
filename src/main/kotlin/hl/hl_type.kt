package hl

import org.graalvm.nativeimage.c.function.CLibrary
import org.graalvm.nativeimage.c.struct.AllowWideningCast
import org.graalvm.nativeimage.c.struct.CField
import org.graalvm.nativeimage.c.struct.CFieldAddress
import org.graalvm.nativeimage.c.struct.CPointerTo
import org.graalvm.nativeimage.c.struct.CStruct
import org.graalvm.nativeimage.c.type.CIntPointer
import org.graalvm.nativeimage.c.type.VoidPointer
import org.graalvm.nativeimage.c.type.WordPointer
import org.graalvm.word.Pointer
import org.graalvm.word.PointerBase
import org.graalvm.word.UnsignedWord

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
@CStruct("hl_type")
interface hl_type : PointerBase {
    @get:CFieldAddress("kind")
    @set:CFieldAddress("kind")
    var kind: HashLink.hl_type_kind

    //anonymous union
    @get:CField
    @set:CField
    //const uchar
    var abs_name : WordPointer

    @get:CFieldAddress("fun")
    @set:CFieldAddress("fun")
    var funn: hl_type_fun

    @get:CFieldAddress
    @set:CFieldAddress
    var obj : hl_type_obj

    @get:CFieldAddress
    @set:CFieldAddress
    var tenum : hl_type_enum

    @get:CFieldAddress
    @set:CFieldAddress
    var virt : hl_type_virtual

    @get:CFieldAddress
    @set:CFieldAddress
    var tparam: hl_type
    //end anonymous union
    @get:CField
    @set:CField
    //unsigned int *mark_bits
    var mark_bits : UnsignedInt
    @get:CFieldAddress
    val vobj_proto : WordPointer

}

@CPointerTo(nameOfCType = "unsigned int")
interface UnsignedInt : UnsignedWord, PointerBase

