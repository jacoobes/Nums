package hl

import org.graalvm.nativeimage.c.struct.CField
import org.graalvm.nativeimage.c.struct.CFieldAddress
import org.graalvm.nativeimage.c.struct.CStruct
import org.graalvm.nativeimage.c.type.VoidPointer
import org.graalvm.word.PointerBase

/**
 * typedef struct {
    void *ptr;
    hl_type *closure;
    int fid;
    } hl_runtime_binding;
 */
@CStruct
interface hl_runtime_binding : PointerBase {
    @get:CField
    @set:CField
    var ptr : VoidPointer

    @get:CFieldAddress
    @set:CFieldAddress
    var closure : hl_type

    @get:CField
    @set:CField
    var fid: Int
}