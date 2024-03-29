package emission

import jvm.IR
import jvm.IRFunction
import nodes.FFunction
import nodes.Vis
import org.objectweb.asm.Opcodes


fun makeIRMainFunction(fFunction: FFunction, body: ArrayList<IR>): IRFunction {
    return IRFunction(
        fnAccessor = Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC + Opcodes.ACC_FINAL,
        main = true,
        name = fFunction.name,
        jvmMethodDescriptor = "([Ljava/lang/String;)V",
        body = body
    )
}

fun makeFunction(fFunction: FFunction, body: ArrayList<IR>) : IRFunction {
    return IRFunction(
        fnAccessor =  when(fFunction.vis) {
            Vis.Show -> Opcodes.ACC_PUBLIC
            Vis.Hide -> Opcodes.ACC_PRIVATE
        } + Opcodes.ACC_STATIC + Opcodes.ACC_FINAL,
        main = false,
        name = fFunction.name,
        jvmMethodDescriptor = fFunction.type.toString(),
        body = body
    )
}