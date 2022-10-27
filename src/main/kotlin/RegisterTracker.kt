class RegisterTracker {
    private var currentReg = 0
    private val regs = IntArray(256)
    fun incrementReg() {
        currentReg++
    }

    fun decrementReg() {
        currentReg--
    }

    fun getRegString(reg : Int?): String {
        val regStr = "r${reg ?: currentReg}"
        if(reg == null) {
            regs[currentReg] = currentReg
        }
        return regStr
    }
}