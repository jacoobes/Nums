package hl

import org.graalvm.nativeimage.c.struct.CFieldAddress
import org.graalvm.nativeimage.c.struct.CStruct
import org.graalvm.nativeimage.c.type.VoidPointer
import org.graalvm.nativeimage.c.type.WordPointer
import org.graalvm.word.PointerBase

/**
 * typedef struct {
        hl_alloc alloc;
        void **functions_ptrs;
        hl_type **functions_types;
    } hl_module_context;
 */
@CStruct
interface hl_module_context : PointerBase {
    @get:CFieldAddress
    @set:CFieldAddress
    var alloc : hl_alloc

    //void **function_ptrs
    @get:CFieldAddress
    @set:CFieldAddress
    var function_ptrs : WordPointer

    //hl_type **functions_types;
    @get:CFieldAddress
    @set:CFieldAddress
    var functions_types : WordPointer
}