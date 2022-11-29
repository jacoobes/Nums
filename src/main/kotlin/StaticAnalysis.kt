import nodes.*
import nodes.Number

/**
 * Static analysis is required
 * needs to verify a few things in syntax trees
 * - Can check if variable names are used, unused, resolve import namespaces etc etc
 */
//class StaticAnalysis {
//    private val imports = hashSetOf<String>()
//    private val functions = hashMapOf<String, FFunction>()
//    fun start(tree: List<Statement>, semantics: Semantics) {
//        val exprVisitor = object : ExpressionVisitor {
//            //These methods aren't needed for static analysis
//            //Maybe in the future, I can add folding expressions based on optimization level
//            override fun onNumber(number: Number) {}
//            override fun onStr(stringLiteral: StringLiteral) {}
//            override fun onBinary(binary: Binary) {
//                visit(binary.left)
//                visit(binary.right)
//            }
//            override fun onCmp(cmp: Comparison) {
//                visit(cmp.left)
//                visit(cmp.right)
//            }
//            override fun onUnary(unary: Unary) {
//                visit(unary.expr)
//            }
//            override fun onBool(bool: Bool) {}
//            override fun onVariable(variable: Variable) {
//                try {
//                    //Try to find a local
//                    //This try catch puts local variables in higher priority before checking if
//                    //the variable resolves to an import / namespace
//                    semantics.getLocal(variable)
//                } catch (_: java.lang.Exception) {
//                    //if trying to access import is null, throw Error
//                    if(!imports.contains(variable.name)) throw Error("Unresolved symbol $variable")
//                }
//            }
//
//            override fun onAnd(and: And) {
//                visit(and.left)
//                visit(and.right)
//            }
//            override fun onOr(or: Or) {
//                visit(or.left)
//                visit(or.right)
//            }
//            override fun onCall(call: Call) {
//
//            }
//            override fun onArrLiteral(arrayLiteral: ArrayLiteral) {}
//            override fun onPath(path: Path) {
//                TODO("Not yet implemented")
//            }
//            override fun visit(item: Expr) {
//                when(item) {
//                    is Number -> visit(item, ::onNumber)
//                    is StringLiteral -> visit(item, ::onStr)
//                    is Binary -> visit(item, ::onBinary)
//                    is Unary -> visit(item, ::onUnary)
//                    is Bool -> visit(item, ::onBool)
//                    is ArrayLiteral -> visit(item, ::onArrLiteral)
//                    is And -> visit(item, ::onAnd)
//                    is Or -> visit(item, ::onOr)
//                    is Variable -> visit(item, ::onVariable)
//                    is Comparison -> visit(item, ::onCmp)
//                    is Call -> visit(item, ::onCall)
//                    is Path -> visit(item, ::onPath)
//                }
//            }
//        }
//        val stmtVisitor = object: StatementVisitor {
//            override fun onFn(fn: FFunction) {
//                functions[fn.token.name] = fn
//                fn.args.forEach {
//                    semantics.addLocal(it.name, -1, false)
//                }
//                visit(fn.block)
//            }
//
//            override fun onIf(iif: Iif) {
//                exprVisitor.visit(iif.condition)
//                visit(iif.elseBody)
//                visit(iif.thenBody)
//            }
//
//            override fun onLoop(loop: Loop) {
//                visit(loop.block)
//            }
//
//            override fun onExprStmt(expressionStatement: ExpressionStatement) {
//                exprVisitor.visit(expressionStatement.expr)
//            }
//
//            override fun onBlock(block: Block) {
//                semantics.incDepth()
//                block.stmts.forEach(this::visit)
//                semantics.decDepth()
//            }
//
//            override fun onVal(valStmt: Val) {
//                //REGISTER VAL for static analysis will always be -1
//                semantics.addLocal(valStmt.token.name, -1, valStmt.isAssignable)
//                exprVisitor.visit(valStmt.expr)
//            }
//
//            override fun onReturn(ret: Return) {
//                exprVisitor.visit(ret.expr)
//            }
//
//            override fun onAssign(assign: Assign) {
//                exprVisitor.onVariable(assign.tok)
//                exprVisitor.visit(assign.newVal)
//            }
//
//            override fun visit(item: Statement) {
//                when (item) {
//                    is Iif -> visit(item, ::onIf)
//                    is Loop -> visit(item, ::onLoop)
//                    is ExpressionStatement -> visit(item, ::onExprStmt)
//                    is Block -> visit(item,::onBlock)
//                    is Val -> visit(item, ::onVal)
//                    is Return -> visit(item, ::onReturn)
//                    is Skip -> {}
//                    is Assign -> visit(item, ::onAssign)
//                    else -> {}
//                }
//            }
//        }
//        //hoists all imports toplevel
//        for(node in tree) {
//            if(node is Import) {
//                node.idents.forEach { v -> imports.add(v.name) }
//            }
//        }
//        for(node in tree) {
//            if(node is FFunction) {
//                stmtVisitor.onFn(node)
//            }
//        }
//        //After analyzing the file, clear the file's imports and functions.
//        imports.clear()
//        functions.clear()
//    }
//
//
//}