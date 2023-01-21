package hl

import org.graalvm.nativeimage.c.struct.CField
import org.graalvm.nativeimage.c.struct.CFieldAddress
import org.graalvm.nativeimage.c.struct.CPointerTo
import org.graalvm.nativeimage.c.struct.CStruct
import org.graalvm.nativeimage.c.type.WordPointer
import org.graalvm.word.PointerBase

@CStruct
interface varray: PointerBase {
    @CFieldAddress
    fun t() : hl_type
    @CFieldAddress
    fun at() : hl_type
    @CField
    fun size() : Int
    @CField
    fun __pad() : Int
}