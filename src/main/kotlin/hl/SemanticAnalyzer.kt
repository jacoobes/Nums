package hl

import ExpressionVisitor
import Semantics
import StatementVisitor
import nodes.*
import types.Context
import types.ContextItem
import types.Type
import types.TypeSolver
import types.Types.*

//Pass 1: visits and collects data about the tree and type checking that will be used for pass 2 (bytecode generation)
class SemanticVisitor : ExpressionVisitor<Expr>, StatementVisitor<Unit> {
    val stringTable = TableLookup<TextId>()
    val intTable = TableLookup<NumsInt>()
    val floatTable = TableLookup<NumsDouble>()
    val functionTable = TableLookup<FFunction>()
    val typesTable = TableLookup<Type>()
    val typeSolver = TypeSolver(Context())
    val semantics = Semantics()
    var entryPoint = -1
    fun start(tree: List<Statement>) {
        /**HType, TDyn ??*/
        arrayOf(TUnit, TU8, TU16, TI32, TI64, TF32, TF64, TBool).forEach(typesTable::add)
        tree.forEach {
            if(it is FFunction) {
                typeSolver.ctx.add(ContextItem.FnDecl(id = it.name, type = it.type))
                stringTable.add(TextId(it.name.value))
                stringTable.add(TextId(it.fullName))
                typesTable.add(it.type)
                functionTable.add(it)
            }
        }
        tree.forEach(::visit)
        if (entryPoint == -1) {
            throw Error("Could not find a main function")
        }
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
        stringTable.add(TextId(stringLiteral.str))
        return stringLiteral
    }

    override fun visit(binary: Binary): Expr {
        val lhs = visit(binary.left)
        val rhs = visit(binary.right)

        return binary
    }
    override fun visit(cmp: Comparison): Expr {
        val lhs = visit(cmp.left)
        val rhs = visit(cmp.right)

        return cmp
    }

    override fun visit(unary: Unary): Expr {
        val e = visit(unary.expr)
        return unary
    }

    override fun visit(bool: Bool): Expr {
        return bool
    }

    override fun visit(textId: TextId): Expr {
        return textId
    }

    override fun visit(and: And): Expr {
        visit(and.left)
        visit(and.right)
        return and
    }

    override fun visit(or: Or): Expr {
        visit(or.left)
        visit(or.right)
        return or
    }

    override fun visit(call: Call): Expr {
       return call
    }

    override fun visit(arrayLiteral: ArrayLiteral): Expr {
        TODO("Not yet implemented")
    }

    override fun visit(path: Path): Expr {
        stringTable.add(TextId(path.toString()))
        return path
    }

    override fun visit(fn: FFunction) {
        if (fn.isMain()) {
            if (entryPoint == -1) {
                entryPoint = functionTable.tbl[fn]!!
            } else throw Error("Found two functions named main")
        }
        for (v in fn.args) {
            semantics.addLocal(v.value, isAssignable = false)
        }
        for (stmt in (fn.block as Block).stmts) {
            stmt.accept(this)
        }
        semantics.clearLocals() // should clear all locals after block has been finished
    }

    override fun visit(iif: Iif) {
        val expr = visit(iif.condition)
    }

    override fun visit(loop: Loop) {
        visit(loop.condition)
        loop.block.accept(this)
    }

    override fun visit(expressionStatement: ExpressionStatement) {
        visit(expressionStatement.expr)
    }

    override fun visit(block: Block) {
        semantics.incDepth()
        block.stmts.forEach(::visit)
        semantics.decDepth()
    }

    override fun visit(valStmt: Val) {
        val e = visit(valStmt.expr)
        val typ = if(valStmt.type == Infer) {
            typeSolver.infer(e)
        } else {
            valStmt.type
        }
        typeSolver.check(typ, e)
        valStmt.type = typ // mutation
        semantics.addLocal(valStmt.token.value, isAssignable = valStmt.isAssignable)
    }

    override fun visit(ret: Return) {
        val e = visit(ret.expr)
    }

    override fun visit(assign: Assign) {
        val local = semantics.getLocal(assign.tok)
        if(!local.isAssignable) throw Error("Cannot assign to $local")
        val e = visit(assign.newVal)
    }


    override fun visit(import: Import) {
        TODO("Not yet implemented")
    }

    override fun visit(space: Space) {
    }

    override fun visit(dataset: Dataset) {
        println(dataset)
    }
    override fun visit(stmt: Statement) {
        when(stmt) {
            is Assign -> visit(stmt)
            is Block ->  visit(stmt)
            is ExpressionStatement ->  visit(stmt)
            is FFunction ->  visit(stmt)
            is Iif ->  visit(stmt)
            is Import ->  visit(stmt)
            is Loop ->  visit(stmt)
            is Return ->  visit(stmt)
            Skip ->  throw Error("Skip found")
            is Space ->  visit(stmt)
            is Dataset -> visit(stmt)
            is Val -> visit(stmt)
        }
    }

    override fun visit(e: Expr): Expr =
        when(e) {
            is And -> visit(e)
            is ArrayLiteral -> visit(e)
            is Binary -> visit(e)
            is Bool -> visit(e)
            is Call -> visit(e)
            is Comparison -> visit(e)
            is NumsByte -> visit(e)
            is NumsDouble -> visit(e)
            is NumsFloat -> visit(e)
            is NumsInt -> visit(e)
            is NumsShort -> visit(e)
            is Or -> visit(e)
            is Path -> visit(e)
            is StringLiteral -> visit(e)
            is Unary -> visit(e)
            is TextId -> visit(e)
        }

}