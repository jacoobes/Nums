package emission
import ExpressionVisitor
import NumsWriter
import StatementVisitor
import hl.hl_code
import nodes.*
import types.Type
import types.Types

//Targets the HL vm
class HLEmitter(
    private val f: NumsWriter,
    private val hlCode: hl_code,
) {
    // Convert the OCaml code snippet to Kotlin
    // from -500M to +500M
    //   0[7] = 0-127
    //   10[+/-][5] [8] = -x2000/+x2000
    //   11[+/-][5] [24] = -x20000000/+x20000000
    //
    init {
        f.write(hlHeader(hlCode.version)) //HEADER = HLB{VERSION}
        val flags = 0
        f.write(
            if(hlCode.hasDebug) flags or 1 else flags
        )
        //allocating size of x constants table
        indexGen(f::write, hlCode.ints.size)
        indexGen(f::write, hlCode.floats.size)
        indexGen(f::write, hlCode.strings.size)
        if (hlCode.version >= 5) indexGen(f::write, hlCode.bytes.size)
        indexGen(f::write, hlCode.types.size)
        indexGen(f::write, hlCode.globals.size)
        indexGen(f::write, hlCode.natives.size)
        indexGen(f::write, hlCode.funDecls.size)
        indexGen(f::write, if(hlCode.version == 4) hlCode.constants.size else 0)
        indexGen(f::write, hlCode.entryPoint)
        hlCode.ints.li.forEach { int -> f.writeInt(int.value) }
        hlCode.floats.li.forEach { fl -> f.writeDouble(fl.value) }
        val len = hlCode.strings.li.fold(0) { acc, s -> acc + s.length() + 1 } //unsure why there's a +1, it exists in the haxe compiler
        f.writeInt(len) //writes the total length of all strings
        hlCode.strings.li.forEach {
            f.writeUTF8(it.str)
        }
        hlCode.strings.li.forEach { indexGen(f::write, it.length()) }

        if(hlCode.version >= 5) {
            val len = 0 //todo
            f.writeInt(0)
        }
        if(hlCode.hasDebug) {
            //todo or not do
        }

        for(t in hlCode.types.li) {
            writeTypes(t)
        }
    }

    private fun writeTypes(t : Type) {
        when(t) {
            is Types.TUnit -> f.write(0)
            is Types.TU8 -> f.write(1)
            is Types.TU16 -> f.write(2)
            is Types.TI32 -> f.write(3)
            is Types.TI64 -> f.write(4)
            is Types.TF32 -> f.write(5)
            is Types.TF64 -> f.write(6)
            is Types.TBool -> f.write(7)
            is Types.TFn -> {
                val argssize = t.typs.size
                if (argssize > 255) throw IllegalArgumentException("Too many arguments: $argssize")
                f.write(10)
                f.write(argssize)
                for(arg in t.typs) writeTypes(arg)
                writeTypes(t.ret)
            }
            is Types.Infer -> throw Error("No inferred types yet")
            else -> throw Error("not yet")
        }
    }
    fun start(tree: List<Statement>) {
        for(stmt in tree) {
            stmtVisitor.visit(stmt)
        }
    }
    private val stmtVisitor = object : StatementVisitor {

        override fun onFn(fn: FFunction) {
            val loc = hlCode.funDecls.tbl[fn] ?: throw Error("Unknown function $fn")
            //writing function bytecode
            f.write(HashLink.hl_type_kind.HI32)
            indexGen(f::write, loc)
            //should be empty for now
            indexGen(f::write, fn.args.size)


           // visit(fn.block)

        }

        override fun onIf(iif: Iif) {
            TODO("Not yet implemented")
        }

        override fun onLoop(loop: Loop) {
            TODO("Not yet implemented")
        }

        override fun onExprStmt(expressionStatement: ExpressionStatement) {
            TODO("Not yet implemented")
        }

        override fun onBlock(block: Block) {
            TODO("Not yet implemented")
        }

        override fun onVal(valStmt: Val) {
            TODO("Not yet implemented")
        }

        override fun onReturn(ret: Return) {
            TODO("Not yet implemented")
        }

        override fun onAssign(assign: Assign) {
            TODO("Not yet implemented")
        }

        override fun onImport(import: Import) {
            TODO("Not yet implemented")
        }

    }
    private val exprVisitor = object: ExpressionVisitor {
        override fun onDouble(number: NumsDouble) {
            TODO("Not yet implemented")
        }

        override fun onInt(number: NumsInt) {
            TODO("Not yet implemented")
        }

        override fun onShort(number: NumsShort) {
            TODO("Not yet implemented")
        }

        override fun onUByte(number: NumsByte) {
            TODO("Not yet implemented")
        }

        override fun onFloat(number: NumsFloat) {
            TODO("Not yet implemented")
        }

        override fun onStr(stringLiteral: StringLiteral) {
            TODO("Not yet implemented")
        }

        override fun onBinary(binary: Binary) {
            TODO("Not yet implemented")
        }

        override fun onCmp(cmp: Comparison) {
            TODO("Not yet implemented")
        }

        override fun onUnary(unary: Unary) {
            TODO("Not yet implemented")
        }

        override fun onBool(bool: Bool) {
            TODO("Not yet implemented")
        }

        override fun onVariable(variable: Variable) {
            TODO("Not yet implemented")
        }

        override fun onAnd(and: And) {
            TODO("Not yet implemented")
        }

        override fun onOr(or: Or) {
            TODO("Not yet implemented")
        }

        override fun onCall(call: Call) {
            TODO("Not yet implemented")
        }

        override fun onArrLiteral(arrayLiteral: ArrayLiteral) {
            TODO("Not yet implemented")
        }

        override fun onPath(path: Path) {
            TODO("Not yet implemented")
        }

    }
}
