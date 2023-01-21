package hl

import org.graalvm.nativeimage.c.function.CMacroInfo
import org.graalvm.nativeimage.c.struct.AllowWideningCast
import org.graalvm.nativeimage.c.struct.CField
import org.graalvm.nativeimage.c.struct.CFieldAddress
import org.graalvm.nativeimage.c.struct.CStruct
import org.graalvm.nativeimage.c.type.VoidPointer
import org.graalvm.word.PointerBase
import org.graalvm.word.UnsignedWord

/**
 * typedef struct {
    hl_type *t;
    #	ifndef HL_64
    int __pad; // force align on 16 bytes for double
    #	endif
    union {
    bool b;
    unsigned char ui8;
    unsigned short ui16;
    int i;
    float f;
    double d;
    vbyte *bytes;
    void *ptr;
    int64 i64;
    } v;
} vdynamic;
 */
@CStruct
interface vdynamic : PointerBase {
    @get:CFieldAddress
    @set:CFieldAddress
    var t: hl_type

    @set: [ CField CMacroInfo("ifndef HL_64 // force align on 16 bytes for double")]
    @get: [CMacroInfo("ifndef HL_64 // force align on 16 bytes for double") CField]

    var __pad: Int

    @get:CFieldAddress
    @set:CFieldAddress
    var v : v

}

/**
 * union {
    bool b;
    unsigned char ui8;
    unsigned short ui16;
    int i;
    float f;
    double d;
    vbyte *bytes;
    void *ptr;
    int64 i64;
 } v;
 */
@CStruct
interface v : PointerBase {
    @get:CField
    @set:CField
    var b: Boolean

    @get:CField("ui8")
    @set:CField("ui8")
    var i8: Byte

    @get: [ AllowWideningCast CField ]
    @set: [ AllowWideningCast CField ]
    var ui8 : UnsignedWord


    @get: [ CField AllowWideningCast]
    @set: [ CField AllowWideningCast]
    var ui16: UnsignedWord

    @get:CField
    @set:CField
    var i: Int

    @get:CField
    @set:CField
    var f: Float

    @get:CField
    @set:CField
    var d: Double

    @get: [ AllowWideningCast CField ]
    @set: [ AllowWideningCast CField ]
    var bytes : Int // typedef unsigned char vbyte;

    @get:CField
    @set:CField
    var ptr: VoidPointer

    @get:CField
    @set:CField
    var int64 : Long

}