package emission

import ExpressionVisitor
import NumsWriter
import Semantics
import StatementVisitor
import visit
import nodes.*

class CodeEmission(
    private val semantics: Semantics = Semantics(),
    private val regMan: RegisterManager = RegisterManager(),
    private val f: NumsWriter,
    ) {
    private val imports = hashSetOf<Expr>()
    private val functions = hashMapOf<String, FFunction>()
    fun start(tree: List<Statement>) {
        for(node in tree) {
            stmtVisitor.visit(node)
        }
    }
    private val stmtVisitor = object : StatementVisitor {
        override fun visit(item: Statement) {
            when (item) {
                is Iif -> visit(item, ::onIf)
                is Loop -> visit(item, ::onLoop)
                is ExpressionStatement -> visit(item, ::onExprStmt)
                is Block -> visit(item,::onBlock)
                is Val -> visit(item, ::onVal)
                is Return -> visit(item, ::onReturn)
                is Skip -> {}
                is Assign -> visit(item, ::onAssign)
                is Space -> {}
                is Import -> visit(item, ::onImport)
                is FFunction -> visit(item, ::onFn)
            }
        }

        override fun onFn(fn: FFunction) {
            if(fn.main) {
                f.writeln("@__entry\n    r0 <- call main\n    exit")
            }
            f.writeln("func ${fn.token.name}")
            fn.args.forEach {
                val reg = regMan.addRegister(it)
                semantics.addLocal(it.name, reg, false)
            }
            visit(fn.block)
            regMan.clearRegisters()
            f.writeln("exit")
            f.writeln("end")
        }

        override fun onIf(iif: Iif) {
            exprVisitor.visit(iif.condition)
            val eReg = regMan.topMostReg()
            val thenLabel = "then.${iif.thenBody.hashCode()}"
            val elseLabel = "else.${iif.elseBody.hashCode()}"
            f.writeln("bb <- ${r(eReg)} $elseLabel $thenLabel", semantics.scopeDepth)
            f.writeln("@$thenLabel", semantics.scopeDepth)
            semantics.incDepth()
            visit(iif.thenBody)
            semantics.decDepth()
            f.writeln("jump @$elseLabel", semantics.scopeDepth)
            semantics.incDepth()
            visit(iif.elseBody)
            semantics.decDepth()
        }

        override fun onLoop(loop: Loop) {
            exprVisitor.visit(loop.condition)
            val curReg = regMan.topMostReg()
            val hash = loop.hashCode()
            f.writeln("bb ${r(curReg)} head.$hash exit.$hash", semantics.scopeDepth)
            f.writeln("@loop.$hash", semantics.scopeDepth)
            visit(loop.block)
            f.writeln("jump loop.$hash", semantics.scopeDepth)
            f.writeln("@exit.$hash", semantics.scopeDepth)
        }

        override fun onExprStmt(expressionStatement: ExpressionStatement) {
            exprVisitor.visit(expressionStatement.expr)
        }

        override fun onBlock(block: Block) {
            semantics.incDepth()
            block.stmts.forEach(::visit)
            semantics.decDepth()
        }

        override fun onVal(valStmt: Val) {
            f.writeln("# val ${valStmt.token.name}", semantics.scopeDepth)
            exprVisitor.visit(valStmt.expr)
            val localReg = regMan.topMostReg()
            semantics.addLocal(valStmt.token.name, localReg, valStmt.isAssignable)
        }

        override fun onReturn(ret: Return) {
            exprVisitor.visit(ret.expr)
            f.writeln("ret ${r(regMan.topMostReg())}",regMan.topMostReg())
        }

        override fun onAssign(assign: Assign) {
            exprVisitor.visit(assign.newVal)
            val tReg = regMan.topMostReg()
            val local = semantics.getLocal(assign.tok)
            if(!local.isAssignable) throw Error("Cannot assign to val")
            f.writeln("${r(local.registerVal)} <- reg ${r(tReg)}", semantics.scopeDepth)
        }

        override fun onImport(import: Import) {
            println(import)
        }
    }

    private val exprVisitor = object : ExpressionVisitor {
        override fun onNumber(number: nodes.Number) {
            val ireg = regMan.addRegister(number)
            f.writeln("${r(ireg)} <- int ${number.value}", semantics.scopeDepth)
        }

        override fun onStr(stringLiteral: StringLiteral) {
            val ireg = regMan.addRegister(stringLiteral)
            f.writeln("${r(ireg)} <- str :${stringLiteral.str}", semantics.scopeDepth)
        }

        override fun onBinary(binary: Binary) {
            visit(binary.left)
            visit(binary.right)
            val iReg = regMan.addRegister(binary)
            f.writeln("${r(iReg)} <- ${binary.op} ${r(iReg - 2)} ${r(iReg - 1)}",semantics.scopeDepth)
        }

        override fun onCmp(cmp: Comparison) {
            visit(cmp.left)
            visit(cmp.right)
            val iReg = regMan.addRegister(cmp)
            val base = { op: String -> "<- call $op ${r(iReg - 2)} ${r(iReg - 1)}" }
            val instr = { str: String, invert:Boolean ->
                    "$str\n" + if(invert) "".padStart(semantics.scopeDepth*2) + "${r(iReg)} <- call not ${r(iReg)}" else ""
            }
            val cmpInstruction = when(cmp.op) {
                ComparisonOps.Eq -> instr(base("eq"), false)
                ComparisonOps.Neq -> instr(base("eq"), true)

                ComparisonOps.Lt -> instr(base("lt"), false)
                ComparisonOps.Gte -> instr(base("lt"), true)

                ComparisonOps.Gt -> instr(base("gt"), false)
                ComparisonOps.Lte -> instr(base("gt"), true)
            }
            f.writeln("${r(iReg)} $cmpInstruction", semantics.scopeDepth)
        }

        override fun onUnary(unary: Unary) {
            visit(unary.expr)
            val iReg = regMan.topMostReg()
            when(unary.op.name) {
                "not" -> {
                    val newReg = regMan.addRegister(unary)
                    f.writeln("${r(newReg)} <- call not ${r(iReg)}", semantics.scopeDepth)
                }
            }
        }

        override fun onBool(bool: Bool) {
            val ireg = regMan.addRegister(bool)
            f.writeln(r(ireg) + " <- int ${bool.bool}", semantics.scopeDepth)
        }

        override fun onVariable(variable: Variable) {
            try {
                //Try to find a local
                //This try catch puts local variables in higher priority before checking if
                //the variable resolves to an import / namespace
                semantics.getLocal(variable)
            } catch (_: java.lang.Exception) {
                //if trying to access import is null, throw Error
                //for now
                if(!imports.contains(variable)) throw Error("Unresolved symbol $variable")
            }
            val ireg = regMan.addRegister(variable)
            val local = semantics.getLocal(variable)
            f.writeln("${r(ireg)} <- reg ${r(local.registerVal)}", semantics.scopeDepth)
        }

        override fun onAnd(and: And) {
            visit(and.left)
            visit(and.right)
            val i = regMan.topMostReg()
            f.writeln("${r(i)} <- band ${r(i)} ${r(i-1)}",semantics.scopeDepth)
        }

        override fun onOr(or: Or) {
            visit(or.left)
            visit(or.right)
            val i = regMan.topMostReg()
            f.writeln("${r(i)} <- bor ${r(i)} ${r(i-1)}",semantics.scopeDepth)
        }

        override fun onCall(call: Call) {
            call.args.forEach(::visit)
            val storedRegs = call.args.map { "r${regMan.registers[it.hashCode()]}" }
            val regStr = storedRegs.joinToString(" ")
            val i = regMan.addRegister(call)
            f.writeln("${r(i)} <- call ${call.callee.name} $regStr", semantics.scopeDepth)
        }

        override fun onArrLiteral(arrayLiteral: ArrayLiteral) {
            throw Error("No array literals yet")
//            arrayLiteral.exprs.forEach(::visit)
//            val loopCode = "set.${arrayLiteral.hashCode()}"
//            val exitCode = "tes.${arrayLiteral.hashCode()}"
//            val areg = semantics.addRegister(arrayLiteral)
//            //len of arr
//            f.writeln("${r(areg)} <- int ${arrayLiteral.exprs.size}", semantics.scopeDepth)
//            f.writeln(
//                "${r(areg+1)} <- arr ${r(areg)}",
//                semantics.scopeDepth
//            )
        }

        override fun onPath(path: Path) {

        }

        override fun visit(item: Expr) {
            when(item) {
                is nodes.Number -> visit(item, ::onNumber)
                is StringLiteral -> visit(item, ::onStr)
                is Binary -> visit(item, ::onBinary)
                is Unary -> visit(item, ::onUnary)
                is Bool -> visit(item, ::onBool)
                is ArrayLiteral -> visit(item, ::onArrLiteral)
                is And -> visit(item, ::onAnd)
                is Or -> visit(item, ::onOr)
                is Variable -> visit(item, ::onVariable)
                is Comparison -> visit(item, ::onCmp)
                is Call -> visit(item, ::onCall)
                is Path -> visit(item, ::onPath)
            }
        }
    }
}

fun r(int: Int): String = "r$int"