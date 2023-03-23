package jvm

import ExpressionVisitor
import Semantics
import StatementVisitor
import nodes.*
import org.objectweb.asm.*
import org.objectweb.asm.Opcodes.*
import types.Context
import types.TypeSolver
import types.Types.*
import java.util.*

//Pass 1: visits and collects data about the tree and type checking that will be used for pass 2 (bytecode generation)
class SemanticVisitor : ExpressionVisitor<Expr>, StatementVisitor<IR?>, Opcodes {
    val typeSolver = TypeSolver(Context())
    val semantics = Semantics()
    var entryPoint: FFunction? = null
    fun start(root: List<Statement>): List<IR?> {
//        val importedNameSpaces = arrayListOf<TextId>()
        val queue = LinkedList(root)
        val tree = ArrayList(root)
        while (queue.isNotEmpty()) {
            when (val node = queue.poll()) {
                is FFunction -> {
                    typeSolver.env[node.name] = node.type
                }
//                is Import -> {
//                    val importedTree = ModuleResolver.dependencyMap[node.file]!!
//                    //for now, get imports working. no need to tree shake for now
//                    if (node.isNamespace) {
//                        importedNameSpaces.add(node.idents[0])
//                    }
//                    tree.addAll(importedTree)
//                    queue.addAll(importedTree)
//                }
//                is TraitDeclaration -> Unit
                else -> Unit
            }
        }
        val ir = tree.map(::visit)
        if (entryPoint == null) {
            throw Error("Could not find a main function")
        }
        return ir
    }

    override fun visit(number: NumsDouble): Expr {
        return number
    }

    override fun visit(number: NumsInt): Expr {
        return number
    }

//    override fun visit(number: NumsShort): Expr {
//        return number
//    }
//
//    override fun visit(number: NumsByte): Expr {
//        return number
//    }

    override fun visit(number: NumsFloat): Expr {
        return number
    }

    override fun visit(stringLiteral: StringLiteral): Expr {
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
        val lhs = visit(and.left)
        val rhs = visit(and.right)
        return and
    }

    override fun visit(or: Or): Expr {
        val lhs = visit(or.left)
        val rhs = visit(or.right)
        return or
    }

    override fun visit(call: Call): Expr {
        return call
    }

    override fun visit(arrayLiteral: ArrayLiteral): Expr {
        return arrayLiteral
    }

    override fun visit(path: NumsPath): Expr {
        return path
    }

    override fun visit(fn: FFunction): IR {
        if (fn.isMain()) {
            if (entryPoint == null) {
                entryPoint = fn
            } else throw Error("Found two functions named main")
        }
        for ((idx, v) in fn.args.withIndex()) {
            typeSolver.env[v] = fn.type.typs[idx]
            semantics.addLocal(v.value, isAssignable = false, v)
        }

        val body: Bytecode = arrayListOf()

        for (stmt in (fn.block as Block).stmts) {
            body.add(stmt.accept(this)!!)
        }
        fn.block.stmts.lastOrNull()?.let {
            if (it is Return && fn.type.ret != TUnit) {
                typeSolver.check(fn.type.ret, (it).expr)
            }
        }
        //val maxStack = 256
        //val localsSize = fn.args.size + fn.block.stmts.filterIsInstance<Val>().size
        semantics.clearLocals() // should clear all locals after block has been finished

        return if(fn.isMain()) {
            makeIRMainFunction(fn, body)
        } else {
            TODO()
        }
    }

    override fun visit(iif: Iif): IR {
        val expr = visit(iif.condition)
        TODO()
    }

    override fun visit(loop: Loop): IR {
        visit(loop.condition)
        loop.block.accept(this)
        TODO()

    }

    override fun visit(expressionStatement: ExpressionStatement): IR {
//        if(expressionStatement.expr !is Call) {
//            throw Error("expression statement is not calling anything")
//        }
        visit(expressionStatement.expr)
        TODO()

    }

    override fun visit(block: Block): IR? {
        semantics.incDepth()
        block.stmts.forEach(::visit)
        semantics.decDepth()
        return null
    }

    override fun visit(valStmt: Val): IR {
        val e = visit(valStmt.expr)
        val typ = if (valStmt.type == Infer) {
            typeSolver.infer(e)
        } else {
            valStmt.type
        }
        typeSolver.check(typ, e)
        typeSolver.env[valStmt.token] = typ //assigns this variable to the type env where its type information can be looked up
        val loc = semantics.addLocal(valStmt.token.value, isAssignable = valStmt.isAssignable, valStmt.expr)

        return when(e) {
            // for now, no jvm opcode optimizations, just getting it working
            is NumsInt -> {
                Chunk(Instruction(BIPUSH, e.value), Instruction(ISTORE, loc.index))
            }
            is NumsDouble -> {
                Chunk(LDC(value = e.value), Instruction(DSTORE, loc.index))
            }
            is Bool -> {
                if(e.bool) {
                    Chunk(Instruction(ICONST_1), Instruction(ISTORE, loc.index))
                } else {
                    Chunk(Instruction(ICONST_0), Instruction(ISTORE, loc.index))
                }
            }
            is TextId -> {
                val referencedLocal = semantics.getLocal(e)
//                println(typeSolver.env[e]) accessing local variable data
//                println(referencedLocal)
                Chunk()
            }
            is ArrayLiteral -> {
                Chunk()
            }
            else -> TODO()
        }
    }

    override fun visit(ret: Return): IR {
        val e = visit(ret.expr)
        TODO()
    }

    override fun visit(assign: Assign): IR {
        val local = semantics.getLocal(assign.tok)
        if (!local.isAssignable) throw Error("Cannot assign to $local")
        val e = visit(assign.newVal)
        TODO()

    }


//    override fun visit(import: Import): IR {
//        TODO()
//
//    }

    override fun visit(space: Space): IR {
        TODO()

    }

//    override fun visit(dataset: Dataset): IR {
//        println(dataset)
//        TODO()
//
//    }

//    override fun visit(traitDeclaration: TraitDeclaration) : IR {
//        TODO()
//
//    }
}