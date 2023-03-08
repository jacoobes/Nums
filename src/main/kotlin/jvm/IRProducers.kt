package jvm

import nodes.FFunction
import org.objectweb.asm.Opcodes


fun makeIRMainFunction(fFunction: FFunction, body: ArrayList<IR>): IRFunction {
    val className = fFunction.fullName.substringBefore("/")

    return IRFunction(
        className= className,
        classAccessors = Opcodes.ACC_PUBLIC,
        fnAccessor = Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC + Opcodes.ACC_FINAL,
        main = true,
        name = fFunction.name,
        jvmMethodDescriptor = "([Ljava/lang/String;)V",
        body = body
    )
}