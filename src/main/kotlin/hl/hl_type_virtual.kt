package hl

import org.graalvm.nativeimage.c.struct.CField
import org.graalvm.nativeimage.c.struct.CFieldAddress
import org.graalvm.nativeimage.c.type.CIntPointer
import org.graalvm.word.PointerBase

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
interface hl_type_virtual : PointerBase {
    @get:CFieldAddress
    @set:CFieldAddress
    var fields : hl_obj_field

    @get:CField
    @set:CField
    var nfields : Int

    @get:CField
    @set:CField
    var dataSize: Int

    @get:CField
    @set:CField
    var indexes : CIntPointer

    @get:CFieldAddress
    @set:CFieldAddress
    var lookup : hl_field_lookup
}