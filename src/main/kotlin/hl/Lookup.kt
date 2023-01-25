package hl

data class TableLookup<T>(
    val li: ArrayList<T> = arrayListOf(),
    val tbl : HashMap<T, Int> = hashMapOf()
) {
    fun add(t: T) {
        if(!tbl.containsKey(t)) {
            tbl[t] = li.size
            li.add(t)
        }
    }
    val size = li.size
}