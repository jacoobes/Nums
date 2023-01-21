package hl

import org.graalvm.nativeimage.c.struct.CField
import org.graalvm.nativeimage.c.struct.CFieldAddress
import org.graalvm.nativeimage.c.struct.CStruct
import org.graalvm.nativeimage.c.type.CCharPointer
import org.graalvm.nativeimage.c.type.WordPointer
import org.graalvm.word.PointerBase

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
    @get:CFieldAddress
    @set:CFieldAddress
    var t: hl_type

    @get:CFieldAddress
    @set:CFieldAddress
    var lookup: hl_field_lookup

    @get:CField
    @set:CField
    var raw_data: CCharPointer

    @get:CField
    @set:CField
    // void **values
    var values : WordPointer

    @get:CField
    @set:CField
    var nfields : Int


    @get:CField
    @set:CField
    var raw_size : Int


    @get:CField
    @set:CField
    var nvalues: Int

    @get:CFieldAddress
    @set:CFieldAddress
    var virtuals : vvirtual
}