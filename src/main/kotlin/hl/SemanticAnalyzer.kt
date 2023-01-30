package hl

import ExpressionVisitor
import Semantics
import StatementVisitor
import nodes.*
import types.Context
import types.Type
import types.TypeSolver
import types.Types.*

class SemanticAnalyzer {
    val stringTable = TableLookup<StringLiteral>()
    val intTable = TableLookup<NumsInt>()
    val floatTable = TableLookup<NumsDouble>()
    val functionTable = TableLookup<FFunction>()
    val typesTable = TableLookup<Type>()
    val typeSolver = TypeSolver(Context())
    val semantics = Semantics()
    var entryPoint = -1
    fun start(li: List<Statement>) {
        /**HType, TDyn ??*/
        arrayOf(TUnit, TU8, TU16, TI32, TI64, TF32, TF64, TBool).forEach(typesTable::add)
        li.forEach(stmts::visit)
        if (entryPoint == -1) {
            throw Error("Could not find a main function")
        }
    }

    private val stmts = object : StatementVisitor {
        override fun onFn(fn: FFunction) {
            stringTable.add(StringLiteral(fn.name.name))
            functionTable.add(fn)
            typesTable.add(fn.type)
           // typeSolver.ctx.add(fn.name.name, fnType)
            if (fn.isMain()) {
                if (entryPoint == -1) {
                    entryPoint = functionTable.tbl[fn]!!
                } else {
                    throw Error("Found two functions named main")
                }
            }
            for(v in fn.args) {
                //typeSolver.ctx.add(v.name, type)
                semantics.addLocal(v.name, isAssignable = false)
            }
            fn.block as Block
            for (s in fn.block.stmts) {
                visit(s)
            }
        }

        override fun onIf(iif: Iif) {
            exprVisitor.visit(iif.condition)
        }

        override fun onLoop(loop: Loop) {
            exprVisitor.visit(loop.condition)
            visit(loop.block)
        }

        override fun onExprStmt(expressionStatement: ExpressionStatement) {
            exprVisitor.visit(expressionStatement.expr)
        }

        override fun onBlock(block: Block) {
            block.stmts.forEach(::visit)
        }

        override fun onVal(valStmt: Val) {
            typeSolver.check(valStmt.type, valStmt.expr)

            exprVisitor.visit(valStmt.expr)
        }

        override fun onReturn(ret: Return) {
            exprVisitor.visit(ret.expr)
        }

        override fun onAssign(assign: Assign) {
            exprVisitor.visit(assign.newVal)
        }

        override fun onImport(import: Import) {
            TODO("Not yet implemented")
        }
    }

    private val exprVisitor = object : ExpressionVisitor {
        override fun onDouble(number: NumsDouble) {
            typeSolver.check(TF64, number)
            floatTable.add(number)
        }

        override fun onInt(number: NumsInt) {
            typeSolver.check(TI32, number)
            intTable.add(number)
        }

        override fun onShort(number: NumsShort) {
            typeSolver.check(TU16, number)
        }

        override fun onUByte(number: NumsByte) {
            typeSolver.check(TU8, number)
        }

        override fun onFloat(number: NumsFloat) {
            typeSolver.check(TF32, number)
        }

        override fun onStr(stringLiteral: StringLiteral) {
            typeSolver.check(TTxt, stringLiteral)
            stringTable.add(stringLiteral)
        }

        override fun onBinary(binary: Binary) {
             visit(binary.left)
             visit(binary.right)
        }

        override fun onCmp(cmp: Comparison) {
            visit(cmp.left)
            visit(cmp.right)
        }

        override fun onUnary(unary: Unary) {
            visit(unary.expr)
        }

        override fun onBool(bool: Bool) {
            typeSolver.check(TBool, bool)
        }

        override fun onVariable(variable: Variable) {
//            val ctxitem = typeSolver.ctx.find(variable.name) ?: throw TypeError("Unresolved name :$variable")
            stringTable.add(StringLiteral(variable.name))
        }

        override fun onAnd(and: And) {
            visit(and.left)
            visit(and.right)
        }

        override fun onOr(or: Or) {
            visit(or.left)
            visit(or.right)
        }

        override fun onCall(call: Call) {

        }

        override fun onArrLiteral(arrayLiteral: ArrayLiteral) {
            arrayLiteral.exprs.forEach(::visit)
        }

        override fun onPath(path: Path) {
            stringTable.add(StringLiteral(path.toString()))
        }

    }
}