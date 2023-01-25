package emission



fun indexGen(b: (Int) -> Unit, i: Int) {
    if (i < 0) {
        val i = -i
        if (i < 0x2000) {
            b((i shr 8) or 0xA0)
            b(i and 0xFF)
        } else if (i >= 0x20000000) {
            throw IllegalArgumentException("$i too large to write an index")
        } else {
            b((i shr 24) or 0xE0)
            b((i shr 16) and 0xFF)
            b((i shr 8) and 0xFF)
            b(i and 0xFF)
        }
    } else if (i < 0x80) {
        b(i)
    } else if (i < 0x2000) {
        b((i shr 8) or 0x80)
        b(i and 0xFF)
    } else if (i >= 0x20000000) {
        throw IllegalArgumentException("$i too large to write an index")
    } else {
        b((i shr 24) or 0xC0)
        b((i shr 16) and 0xFF)
        b((i shr 8) and 0xFF)
        b(i and 0xFF)
    }
}