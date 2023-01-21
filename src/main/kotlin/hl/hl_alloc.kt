package hl


import org.graalvm.nativeimage.c.function.CLibrary
import org.graalvm.nativeimage.c.struct.CField
import org.graalvm.nativeimage.c.struct.CStruct
import org.graalvm.word.PointerBase
@CStruct("hl_alloc_block", isIncomplete = true)
interface hl_alloc_block: PointerBase
//typedef struct { hl_alloc_block *cur; } hl_alloc;
@CLibrary("hl")
interface hl_alloc : PointerBase {
    @get:CField
    val cur : hl_alloc_block
}