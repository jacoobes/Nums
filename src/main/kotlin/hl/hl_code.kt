package hl

import com.github.h0tk3y.betterParse.utils.Tuple2
import nodes.*
import types.Type

/**
 * typedef struct {
    int version;
    int nints;
    int nfloats;
    int nstrings;
    int nbytes;
    int ntypes;
    int nglobals;
    int nnatives;
    int nfunctions;
    int nconstants;
    int entrypoint;
    int ndebugfiles;
    bool hasdebug;
    int*		ints;
    double*		floats;
    char**		strings;
    int*		strings_lens;
    char*		bytes;
    int*		bytes_pos;
    char**		debugfiles;
    int*		debugfiles_lens;
    uchar**		ustrings;
    hl_type*	types;
    hl_type**	globals;
    hl_native*	natives;
    hl_function*functions;
    hl_constant*constants;
    hl_alloc	alloc;
    hl_alloc	falloc;
} hl_code;
 */
data class hl_code(
    val version: Int,
    val hasDebug: Boolean,
    val ints: TableLookup<NumsInt>,
    val floats: TableLookup<NumsDouble>,
    val types: TableLookup<Type>,
    val bytes: Array<Byte> = arrayOf(),
    val globals: Array<HashLink.hl_type_kind> = arrayOf(),
    val strings: TableLookup<StringLiteral>,
    val natives: Array<String> = arrayOf(), //(string index * string index * ttype * functable index, will think abt later,
    val funDecls: TableLookup<FFunction>, // function declarations,
    val constants: Array<Tuple2<Int, Int>> = arrayOf(),
    val entryPoint: Int,

    ) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as hl_code

        if (version != other.version) return false
        if (hasDebug != other.hasDebug) return false
        if (ints != other.ints) return false
        if (floats != other.floats) return false
        if (!bytes.contentEquals(other.bytes)) return false
        if (!globals.contentEquals(other.globals)) return false
        if (strings != other.strings) return false
        if (!natives.contentEquals(other.natives)) return false
        if (funDecls != other.funDecls) return false
        if (!constants.contentEquals(other.constants)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = version
        result = 31 * result + hasDebug.hashCode()
        result = 31 * result + ints.hashCode()
        result = 31 * result + floats.hashCode()
        result = 31 * result + bytes.contentHashCode()
        result = 31 * result + globals.contentHashCode()
        result = 31 * result + strings.hashCode()
        result = 31 * result + natives.contentHashCode()
        result = 31 * result + funDecls.hashCode()
        result = 31 * result + constants.contentHashCode()
        return result
    }

}