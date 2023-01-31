package hl

import ExpressionVisitor
import Semantics
import StatementVisitor
import nodes.*
import types.Context
import types.Type
import types.TypeSolver
import types.Types.*
import kotlin.math.exp

//Pass 1: visits and collects data about the tree that will be used for pass 2 (bytecode generation)
class SemanticVisitor : ExpressionVisitor<Expr>, StatementVisitor<Unit> {
    val stringTable = TableLookup<StringLiteral>()
    val intTable = TableLookup<NumsInt>()
    val floatTable = TableLookup<NumsDouble>()
    val functionTable = TableLookup<FFunction>()
    val typesTable = TableLookup<Type>()
    val typeSolver = TypeSolver(Context())
    val semantics = Semantics()
    var entryPoint = -1

    fun start(tree: List<Statement>) {
        tree.forEach(::stmt)
    }
    override fun visit(number: NumsDouble): Expr {
        floatTable.add(number)
        return number
    }

    override fun visit(number: NumsInt): Expr {
        intTable.add(number)
        return number
    }

    override fun visit(number: NumsShort): Expr {
        return number
    }

    override fun visit(number: NumsByte): Expr {
        return number
    }

    override fun visit(number: NumsFloat): Expr {
        return number
    }

    override fun visit(stringLiteral: StringLiteral): Expr {
        stringTable.add(stringLiteral)
        return stringLiteral
    }

    override fun visit(binary: Binary): Expr {
        expr(binary.left)
        expr(binary.right)
        return binary
    }

    override fun visit(cmp: Comparison): Expr {
        expr(cmp.left)
        expr(cmp.right)
        return cmp
    }

    override fun visit(unary: Unary): Expr {
        expr(unary.expr)
        return unary
    }

    override fun visit(bool: Bool): Expr {
        return bool
    }

    override fun visit(variable: Variable): Expr {
        return variable
    }

    override fun visit(and: And): Expr {
        expr(and.left)
        expr(and.right)
        return and
    }

    override fun visit(or: Or): Expr {
        expr(or.left)
        expr(or.right)
        return or
    }

    override fun visit(call: Call): Expr? {
        TODO("Not yet implemented")
    }

    override fun visit(arrayLiteral: ArrayLiteral): Expr? {
        TODO("Not yet implemented")
    }

    override fun visit(path: Path): Expr {
        stringTable.add(StringLiteral(path.toString()))
        return path
    }

    override fun visit(fn: FFunction) {
        stringTable.add(StringLiteral(fn.name.name))
        functionTable.add(fn)
        typesTable.add(fn.type)
        if(fn.isMain()) {
            if (entryPoint == -1) {
                entryPoint = functionTable.tbl[fn]!!
            } else throw Error("Found two functions named main")
        }
        for (v in fn.args) {
            semantics.addLocal(v.name, isAssignable = false)
        }
        for(stmt in (fn.block as Block).stmts) {
            stmt.accept(this)
        }
        semantics.clearLocals() // should clear all locals after block has been finished
    }

    override fun visit(iif: Iif) {
        val expr = expr(iif.condition)

    }

    override fun visit(loop: Loop) {
        TODO("Not yet implemented")
    }

    override fun visit(expressionStatement: ExpressionStatement) {
        expr(expressionStatement.expr)
    }

    override fun visit(block: Block) {
        semantics.incDepth()
        block.stmts.forEach(::stmt)
        semantics.decDepth()
    }

    override fun visit(valStmt: Val) {
        val e = expr(valStmt.expr)
        semantics.addLocal(valStmt.token.name, isAssignable = valStmt.isAssignable)
    }

    override fun visit(ret: Return) {
        val e = expr(ret.expr)
    }

    override fun visit(assign: Assign) {
        val e = expr(assign.newVal)
    }


    override fun visit(import: Import) {
        TODO("Not yet implemented")
    }
    private fun stmt(s : Statement) {
        s.accept(this)
    }
    private fun expr(e : Expr) : Expr? {
        return e.accept(this)
    }

}
//class SemanticAnalyzer {
//
//    fun start(li: List<Statement>) {
//        /**HType, TDyn ??*/
//        arrayOf(TUnit, TU8, TU16, TI32, TI64, TF32, TF64, TBool).forEach(typesTable::add)
//        li.forEach(stmts::visit)
//        if (entryPoint == -1) {
//            throw Error("Could not find a main function")
//        }
//    }
//    private fun addFunctionData(fn : FFunction) {
//        stringTable.add(StringLiteral(fn.name.name))
//        functionTable.add(fn)
//        typesTable.add(fn.type)
//    }
//    private val stmts = object : StatementVisitor {
//         fun onFn(fn: FFunction) {
//            addFunctionData(fn)
//            if (fn.isMain()) {
//                if (entryPoint == -1) {
//                    entryPoint = functionTable.tbl[fn]!!
//                } else throw Error("Found two functions named main")
//            }
//            bytecodeGenerator.addReg(fn.type.ret) //adds the return type of function
//            for((idx, v) in fn.args.withIndex()) {
//                bytecodeGenerator.addReg(fn.type.typs[idx]) // adds the type of function to register
//                semantics.addLocal(v.name, isAssignable = false)
//            }
//            fn.block as Block
//            for (s in fn.block.stmts) {
//                visit(s)
//            }
//            bytecodeGenerator.addBytecode(fn)
//            bytecodeGenerator.clearAll()
//            semantics.clearLocals() // should clear all locals after block has been finished
//        }
//
//         fun onIf(iif: Iif) {
//            exprVisitor.visit(iif.condition)
//        }
//
//         fun onLoop(loop: Loop) {
//            exprVisitor.visit(loop.condition)
//            visit(loop.block)
//        }
//
//         fun onExprStmt(expressionStatement: ExpressionStatement) {
//            exprVisitor.visit(expressionStatement.expr)
//        }
//
//         fun onBlock(block: Block) {
//            semantics.incDepth()
//            block.stmts.forEach(::visit)
//            semantics.decDepth()
//        }
//
//         fun onVal(valStmt: Val) {
//            bytecodeGenerator.addReg(valStmt.type) // still need to type check this because it could be the infer type
//            semantics.addLocal(valStmt.token.name, isAssignable = valStmt.isAssignable)
//            exprVisitor.visit(valStmt.expr)
//        }
//
//         fun onReturn(ret: Return) {
//            exprVisitor.visit(ret.expr)
//        }
//
//         fun onAssign(assign: Assign) {
//            exprVisitor.visit(assign.newVal)
//        }
//
//        fun onImport(import: Import) {
//            TODO("Not yet implemented")
//        }
//    }
//
//    private val exprVisitor = object : ExpressionVisitor {
//        override fun onDouble(number: NumsDouble) {
//            typeSolver.check(TF64, number)
//            floatTable.add(number)
//        }
//
//        override fun onInt(number: NumsInt) {
//            typeSolver.check(TI32, number)
//            intTable.add(number)
//            bytecodeGenerator.addOp(OInt(semantics.localSize(), intTable.size))
//        }
//
//        override fun onShort(number: NumsShort) {
//            typeSolver.check(TU16, number)
//        }
//
//        override fun onUByte(number: NumsByte) {
//            typeSolver.check(TU8, number)
//        }
//
//        override fun onFloat(number: NumsFloat) {
//            typeSolver.check(TF32, number)
//        }
//
//        override fun onStr(stringLiteral: StringLiteral) {
//            typeSolver.check(TTxt, stringLiteral)
//            stringTable.add(stringLiteral)
//        }
//
//        override fun onBinary(binary: Binary) {
//             visit(binary.left)
//             visit(binary.right)
//        }
//
//        override fun onCmp(cmp: Comparison) {
//            visit(cmp.left)
//            visit(cmp.right)
//        }
//
//        override fun onUnary(unary: Unary) {
//            visit(unary.expr)
//        }
//
//        override fun onBool(bool: Bool) {
//            typeSolver.check(TBool, bool)
//        }
//
//        override fun onVariable(variable: Variable) {
////            val ctxitem = typeSolver.ctx.find(variable.name) ?: throw TypeError("Unresolved name :$variable")
//            stringTable.add(StringLiteral(variable.name))
//        }
//
//        override fun onAnd(and: And) {
//            visit(and.left)
//            visit(and.right)
//        }
//
//        override fun onOr(or: Or) {
//            visit(or.left)
//            visit(or.right)
//        }
//
//        override fun onCall(call: Call) {
//
//        }
//
//        override fun onArrLiteral(arrayLiteral: ArrayLiteral) {
//            arrayLiteral.exprs.forEach(::visit)
//        }
//
//        override fun onPath(path: Path) {
//            stringTable.add(StringLiteral(path.toString()))
//        }
//
//    }
//}