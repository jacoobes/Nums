package hl

import org.graalvm.nativeimage.c.struct.CField
import org.graalvm.nativeimage.c.struct.CFieldAddress
import org.graalvm.nativeimage.c.struct.CStruct
import org.graalvm.nativeimage.c.type.VoidPointer
import org.graalvm.nativeimage.c.type.WordPointer
import org.graalvm.word.PointerBase

@CStruct
interface hl_type_fun : PointerBase {
    @get:CFieldAddress
    //hl_type
    val args: WordPointer
    @get:CFieldAddress
    val ret : hl_type
    @get:CFieldAddress
    val parent: hl_type

    @get:CFieldAddress
    val closure_type: closure_type

    @get:CFieldAddress
    val closure: closure
}
@CStruct
interface closure_type : PointerBase {
    @get:CFieldAddress
    val kind : HashLink.hl_type_kind
    @get:CField
    val p : VoidPointer
}
@CStruct
interface closure : PointerBase {
    @get:CFieldAddress
    //hl_type
    val args: WordPointer
    @get:CFieldAddress
    val ret : hl_type
    @get:CField
    val nargs: Int
    @get:CFieldAddress
    val parent: hl_type
}