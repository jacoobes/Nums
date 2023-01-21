package hl

import org.graalvm.nativeimage.c.struct.CField
import org.graalvm.nativeimage.c.struct.CFieldAddress
import org.graalvm.nativeimage.c.struct.CStruct

@CStruct
interface vclosure_wrapper {
    @CFieldAddress
    fun cl() : vclosure
    @CFieldAddress
    fun wrappedFun() : vclosure //possible fail?
}