package hl

import org.graalvm.nativeimage.c.struct.CField
import org.graalvm.nativeimage.c.struct.CFieldAddress
import org.graalvm.nativeimage.c.struct.CStruct
import org.graalvm.word.PointerBase

@CStruct
interface venum: PointerBase {
    @get:CFieldAddress
    @set:CFieldAddress
    var t: hl_type

    @get:CField
    @set:CField
    var index: Int
}