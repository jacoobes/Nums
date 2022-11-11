
interface Native {
    fun core() : String {
        val lib = listOf(eq(), lt(), gt(), not())
        return lib.joinToString(" ")
    }
    /**
     * A two parameter function, checking if two values are equal.
     * T = 1
     * F = 0
     */
    fun eq() : String
    /**
     * A two parameter function, checking if two values are lt or gte
     * T = 1
     * F = 0
     */
    fun lt() : String

    fun gt() : String

    fun not() : String
}


object MiniVmNative : Native {
    override fun eq() : String {
        return """
        func eq 
        beq r1 r2 fals tru
        @tru
          r0 <- int 1
          ret r0
        exit
        @fals
          r0 <- int 0
          ret r0
        exit  
        end
        
    """.trimIndent()
    }

    override fun lt(): String {
        return """
        func lt 
        blt r1 r2 fals tru
        @tru
          r0 <- int 1
          ret r0
          exit
        @fals
          r0 <- int 0
          ret r0
          exit  
        end
        
    """.trimIndent()
    }

    override fun gt(): String {
        return """
        func gt
            r0 <- int 1
            r3 <- call lt r1 r2
            r4 <- call eq r1 r2
            r5 <- bxor r3 r4
            r5 <- bxor r0 r5            
            ret r6   
        exit  
        end
        
    """.trimIndent()
    }

    override fun not(): String {
        return """
        func not
          r0 <- int 1
          r1 <- bxor r0 r1
          ret r1
          exit
        end
            
        """.trimIndent()
    }

}