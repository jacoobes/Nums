package hl

import org.graalvm.nativeimage.c.struct.CField
import org.graalvm.nativeimage.c.struct.CFieldAddress
import org.graalvm.nativeimage.c.struct.CStruct
import org.graalvm.word.PointerBase

/**
 * struct _hl_field_lookup {
    hl_type *t;
    int hashed_name;
    int field_index; // negative or zero : index in methods
};
 */
@CStruct
interface hl_field_lookup : PointerBase {
    @get:CFieldAddress
    @set:CFieldAddress
    var t : hl_type

    @get:CField
    @set:CField
    var hashed_name : Int

    // negative or zero : index in methods
    @get:CField
    @set:CField
    var field_index: Int
}