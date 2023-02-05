package emission

import ExpressionVisitor
import NumsWriter
import StatementVisitor
import hl.hl_code
import nodes.*
import types.Type
import types.Types

class HLEmitter(
    private val f: NumsWriter,
    private val hlCode: hl_code
) : ExpressionVisitor<Expr>, StatementVisitor<Unit> {
    private fun writeTypes(t: Type) {
        when (t) {
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
                f.write(10)//HFunction
                f.write(argssize)
                for (arg in t.typs) writeTypes(arg)
                writeTypes(t.ret)
            }

            is Types.Infer -> throw Error("No inferred types yet")
            else -> throw Error("not yet")
        }
    }
    init {
        f.write(hlHeader(hlCode.version)) //HEADER = HLB{VERSION}
        val flags = 0
        f.write(
            if (hlCode.hasDebug) flags or 1 else flags
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
        indexGen(f::write, if (hlCode.version == 4) hlCode.constants.size else 0)
        indexGen(f::write, hlCode.entryPoint)
        hlCode.ints.li.forEach { int -> f.writeInt(int.value) }
        hlCode.floats.li.forEach { fl -> f.writeDouble(fl.value) }
        val len =
            hlCode.strings.li.fold(0) { acc, s -> acc + s.length() + 1 } //unsure why there's a +1, it exists in the haxe compiler
        f.writeInt(len) //writes the total length of all strings
        hlCode.strings.li.forEach {
            f.writeUTF8(it.str)
        }
        hlCode.strings.li.forEach { indexGen(f::write, it.length()) }

        if (hlCode.version >= 5) {
            val len = 0 //todo
            f.writeInt(0)
        }
        if (hlCode.hasDebug) {
            //todo or not do
        }

        for (t in hlCode.types.li) {
            writeTypes(t)
        }
    }

    override fun visit(expr: Expr): Expr {
        TODO("Not yet implemented")
    }

    override fun visit(number: NumsDouble): Expr {
        TODO("Not yet implemented")
    }

    override fun visit(number: NumsInt): Expr {
        TODO("Not yet implemented")
    }

    override fun visit(number: NumsShort): Expr {
        TODO("Not yet implemented")
    }

    override fun visit(number: NumsByte): Expr {
        TODO("Not yet implemented")
    }

    override fun visit(number: NumsFloat): Expr {
        TODO("Not yet implemented")
    }

    override fun visit(stringLiteral: StringLiteral): Expr {
        TODO("Not yet implemented")
    }

    override fun visit(binary: Binary): Expr {
        TODO("Not yet implemented")
    }

    override fun visit(cmp: Comparison): Expr {
        TODO("Not yet implemented")
    }

    override fun visit(unary: Unary): Expr {
        TODO("Not yet implemented")
    }

    override fun visit(bool: Bool): Expr {
        TODO("Not yet implemented")
    }

    override fun visit(textId: TextId): Expr {
        TODO("Not yet implemented")
    }

    override fun visit(and: And): Expr {
        TODO("Not yet implemented")
    }

    override fun visit(or: Or): Expr {
        TODO("Not yet implemented")
    }

    override fun visit(call: Call): Expr {
        TODO("Not yet implemented")
    }

    override fun visit(arrayLiteral: ArrayLiteral): Expr {
        TODO("Not yet implemented")
    }

    override fun visit(path: Path): Expr {
        TODO("Not yet implemented")
    }

    override fun visit(fn: FFunction) {
        val loc = hlCode.funDecls.tbl[fn] ?: throw Error("Unknown function $fn")
        //writing function bytecode
        writeTypes(fn.type)
        indexGen(f::write, loc)
        //should be empty for now
        indexGen(f::write, fn.args.size)

        // visit(fn.block)
    }

    override fun visit(iif: Iif) {
        TODO("Not yet implemented")
    }

    override fun visit(loop: Loop){
        TODO("Not yet implemented")
    }

    override fun visit(expressionStatement: ExpressionStatement) {
        TODO("Not yet implemented")
    }

    override fun visit(block: Block) {
        TODO("Not yet implemented")
    }

    override fun visit(valStmt: Val) {
        TODO("Not yet implemented")
    }

    override fun visit(ret: Return) {
        TODO("Not yet implemented")
    }

    override fun visit(assign: Assign) {
        TODO("Not yet implemented")
    }

    override fun visit(import: Import) {
        TODO("Not yet implemented")
    }

    override fun visit(space: Space) {
        TODO("Not yet implemented")
    }

    override fun visit(dataset: Dataset) {
        TODO("Not yet implemented")
    }

    override fun visit(stmt: Statement) {
        TODO("Not yet implemented")
    }
    override fun visit(traitDeclaration: TraitDeclaration) {
        TODO("Not yet implemented")
    }

}