package hl

import org.graalvm.nativeimage.c.struct.CFieldAddress
import org.graalvm.nativeimage.c.struct.CStruct
import org.graalvm.word.PointerBase

@CStruct
interface vvirtual : PointerBase {
    @get:CFieldAddress
    @set:CFieldAddress
    var t: hl_type

    @get:CFieldAddress
    @set:CFieldAddress
    var value: vdynamic

    @get:CFieldAddress
    @set:CFieldAddress
    var next: vvirtual
}