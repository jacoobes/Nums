package hl

import org.graalvm.nativeimage.c.struct.CField
import org.graalvm.nativeimage.c.struct.CFieldAddress
import org.graalvm.nativeimage.c.struct.CStruct
import org.graalvm.nativeimage.c.type.CIntPointer
import org.graalvm.nativeimage.c.type.WordPointer
import org.graalvm.word.PointerBase

/**
 * typedef struct {
    const uchar *name;
    int nconstructs;
    hl_enum_construct *constructs;
    void **global_value;
    } hl_type_enum;
 */
@CStruct
interface hl_type_enum : PointerBase {
    @get:CField
    @set:CField
    //const uchar
    var name : WordPointer

    @get:CField
    @set:CField
    var nconstructs : Int

    @get:CFieldAddress
    @set:CFieldAddress
    var constructs: hl_enum_construct

    @get:CField
    @set:CField
    //void **
    var global_value: WordPointer
}


/**
 *
    typedef struct {
        const uchar *name;
        int nparams;
        hl_type **params;
        int size;
        bool hasptr;
        int *offsets;
    } hl_enum_construct;
 */
@CStruct
interface hl_enum_construct : PointerBase {
    @get:CField
    @set:CField
    //const uchar
    var name : WordPointer

    @get:CField
    @set:CField
    var nparams : Int

    @get:CFieldAddress
    @set:CFieldAddress
    //hl_type **params;
    var params : WordPointer

    @get:CField
    @set:CField
    var size : Int

    @get:CField
    @set:CField
    var hasptr: Boolean

    @get:CField
    @set:CField
    var offsets: CIntPointer

}