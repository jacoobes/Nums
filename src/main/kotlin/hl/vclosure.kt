package hl

import org.graalvm.nativeimage.c.function.CMacroInfo
import org.graalvm.nativeimage.c.struct.CField
import org.graalvm.nativeimage.c.struct.CFieldAddress
import org.graalvm.nativeimage.c.struct.CStruct
import org.graalvm.nativeimage.c.type.VoidPointer
import org.graalvm.word.PointerBase


@CStruct
interface vclosure : PointerBase {
    @get:CFieldAddress
    @set:CFieldAddress
    var t : hl_type
    @get:CField("fun")
    @set:CField("fun")
    var getFun : VoidPointer

    @get:CField
    @set:CField
    var hasValue : Int

    @get:[CField CMacroInfo("ifdef HL_64")]
    @set:[CField CMacroInfo("ifdef HL_64")]
    var stackCount : Int

    @get:CField
    @set:CField
    var value : VoidPointer
}