package jvm

import EnvironmentManager
import ExpressionVisitor
import Semantics
import StatementVisitor
import emission.makeFunction
import emission.makeIRMainFunction
import nodes.*
import org.objectweb.asm.*
import org.objectweb.asm.Opcodes.*
import types.Context
import types.TypeSolver
import java.util.*

//Pass 1: visits and collects data about the tree and type checking that will be used for pass 2 (bytecode generation)
class SemanticVisitor : ExpressionVisitor<IR>, StatementVisitor<IR?>, Opcodes {
    val typeSolver = TypeSolver(Context())
    val semantics = Semantics()
    val envManager = EnvironmentManager()
    fun start(root: List<Statement>): List<IR?> {
//        val importedNameSpaces = arrayListOf<TextId>()
        val queue = LinkedList(root)
        val tree = ArrayList(root)
        while (queue.isNotEmpty()) {
            when (val node = queue.poll()) {
                is FFunction -> {
                    if (node.isMain()) {
                        if (envManager.entryPoint == null) {
                            envManager.entryPoint = CallableStructure(node, semantics)
                        } else throw Error("Found two functions named main")
                    }
                    envManager.addCallable(node.name.value, CallableStructure(node, semantics))
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
        if (envManager.entryPoint == null) {
            throw Error("Could not find a main function")
        }
        return ir
    }

    fun visitBlock(block: Block, env: Semantics) {
        visit(block)
    }

    override fun visit(number: NumsDouble): IR {
        return LDC(value = number.value)
    }

    override fun visit(number: NumsInt): IR {
        return LDC(value = number.value)
    }

//    override fun visit(number: NumsShort): Expr {
//        return number
//    }
//
//    override fun visit(number: NumsByte): Expr {
//        return number
//    }

    override fun visit(number: NumsFloat): IR {
        return LDC(value = number.value)
    }

    override fun visit(stringLiteral: StringLiteral): IR {
        TODO()
    }

    override fun visit(binary: Binary): IR {
        val lhs = visit(binary.left)
        val rhs = visit(binary.right)
        return when(binary.op) {
            "add" -> Chunk(lhs, rhs, Instruction(IADD))
            "sub" -> Chunk(lhs, rhs, Instruction(ISUB))
            "mul" -> Chunk(lhs, rhs, Instruction(IDIV))
            "div" -> Chunk(lhs, rhs, Instruction(IDIV))
            else -> throw Error("not implemented yet")
        }
    }

    override fun visit(cmp: Comparison): IR {
        val lhs = visit(cmp.left)
        val rhs = visit(cmp.right)
        return when(cmp.op) {
            ComparisonOps.Lt -> Chunk(lhs,rhs, Instruction(IF_ICMPGE))
            ComparisonOps.Lte -> Chunk(lhs,rhs, Instruction(IF_ICMPGT))
            ComparisonOps.Gt -> Chunk(lhs, rhs, Instruction(IF_ICMPLE))
            ComparisonOps.Gte -> Chunk(lhs, rhs, Instruction(IF_ICMPLT))
            else -> TODO()
        }
    }

    override fun visit(unary: Unary): IR {
        val e = visit(unary.expr)
        TODO()
    }

    override fun visit(bool: Bool): IR {
        return if(bool.bool) {
            Instruction(LCONST_1)
        } else {
            Instruction(LCONST_0)
        }
    }

    override fun visit(textId: TextId): IR {
        // until i get type system working, variables with text id as expressions will be int type only
        val referencedVariable = semantics.getLocal(textId)
        return VarInstruction(ILOAD, referencedVariable.index)
    }

    override fun visit(and: And): IR {
        val lhs = visit(and.left)
        val rhs = visit(and.right)

        TODO()
    }

    override fun visit(or: Or): IR {
        val lhs = visit(or.left)
        val rhs = visit(or.right)
        TODO()
    }

    override fun visit(call: Call): IR {
        TODO()
    }

    override fun visit(arrayLiteral: ArrayLiteral): IR {
        TODO()
    }

    override fun visit(path: NumsPath): IR {
        TODO()
    }

    override fun visit(fn: FFunction): IR? {

        fn.block as Block

        // adding function arguments to the arguments list
        for ((idx, v) in fn.args.withIndex()) {
//            typeSolver.env[v] = fn.type.typs[idx]
            semantics.addLocal(v.value, isAssignable = false, v)
        }

        val body: Bytecode = arrayListOf()

        for (stmt in fn.block.stmts) {
            val ir = stmt.accept(this)
            ir?.let(body::add) ?: println("found null chunk")
        }

        //type checking function signature
//        fn.block.stmts.lastOrNull()?.let {
//            if (it is Return && fn.type.ret != TUnit) {
//                typeSolver.check(fn.type.ret, (it).expr)
//            }
//        }
        //val maxStack = 256
        //val localsSize = fn.args.size + fn.block.stmts.filterIsInstance<Val>().size
        semantics.clearLocals() // should clear all locals after block has been finished
        return if(fn.isMain()) {
            makeIRMainFunction(fn, body)
        } else {
            makeFunction(fn, body)
        }
    }

    override fun visit(iif: Iif): IR {
        val expr = visit(iif.condition)
        TODO()
    }

    override fun visit(loop: Loop): IR {
        val irCondition = visit(loop.condition)
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
        val expressionIR = visit(valStmt.expr)
//        val typ = if (valStmt.type == Infer) {
//            typeSolver.infer(e)
//        } else {
//            valStmt.type
//        }
//        typeSolver.check(typ, e)
//        typeSolver.env[valStmt.token] = typ //assigns this variable to the type env where its type information can be looked up
        val loc = semantics.addLocal(valStmt.token.value, isAssignable = valStmt.isAssignable, valStmt.expr)

        return when(valStmt.expr) {
            // for now, no jvm opcode optimizations, just getting it working
            is NumsInt -> Chunk(expressionIR, VarInstruction(ISTORE, loc.index))
            is NumsDouble -> Chunk(expressionIR, VarInstruction(DSTORE, loc.index))
            is Bool -> Chunk(expressionIR, VarInstruction(ISTORE, loc.index))
            is TextId -> Chunk(expressionIR, VarInstruction(ISTORE, loc.index))
            is ArrayLiteral -> {
                Chunk()
            }
            is Binary -> {
                Chunk(expressionIR, VarInstruction(ISTORE, loc.index))
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