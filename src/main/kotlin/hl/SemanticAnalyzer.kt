package hl

import ExpressionVisitor
import ModuleResolver
import Semantics
import StatementVisitor
import nodes.*
import types.Context
import types.Type
import types.TypeSolver
import types.Types.*
import java.util.LinkedList

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
        val queue = LinkedList(tree)
        while(queue.isNotEmpty()) {
            val node = queue.poll()
            when(node) {
                is FFunction -> {
                    typeSolver.env[node.name] = node.type
                    //typeSolver.ctx.add(ContextItem.FnDecl(id = it.name, type = it.type))
                    stringTable.add(TextId(node.name.value))
                    stringTable.add(TextId(node.fullName))
                    typesTable.add(node.type)
                    functionTable.add(node)
                }
                is Import -> {
                    val tree = ModuleResolver.dependencyMap[node.file]
                    //for now, get imports working. no need to tree shake for now
                    if(node.isNamespace) {

                    } else {

                    }
                }
                else -> Unit
            }
        }
        tree.forEach { node ->

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
        return arrayLiteral
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
        fn.block.stmts.lastOrNull()?.let {
            if (it !is Return && fn.type.ret != TUnit) {
                throw Error("Expected a return at the end of function returning a value")
            }
            typeSolver.check(fn.type.ret, (it as Return).expr)
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
        val typ = if (valStmt.type == Infer) {
            typeSolver.infer(e)
        } else {
            valStmt.type
        }
        typeSolver.check(typ, e)
        typeSolver.env[valStmt.token] = typ //assigns this variable to the type env where its type information can be looked up
        semantics.addLocal(valStmt.token.value, isAssignable = valStmt.isAssignable)
    }

    override fun visit(ret: Return) {
        val e = visit(ret.expr)
    }

    override fun visit(assign: Assign) {
        val local = semantics.getLocal(assign.tok)
        if (!local.isAssignable) throw Error("Cannot assign to $local")
        val e = visit(assign.newVal)
    }


    override fun visit(import: Import) {
       val tree = ModuleResolver.dependencyMap[import.file]
    }

    override fun visit(space: Space) {
    }

    override fun visit(dataset: Dataset) {
        println(dataset)
    }

    override fun visit(traitDeclaration: TraitDeclaration) {

    }
}