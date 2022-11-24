import nodes.*
import java.io.File

/**
 * Static analysis is required
 * needs to verify a few things in syntax trees
 * - Can check if variable names are used, unused, resolve import namespaces etc etc
 */
class StaticAnalysis {
    fun start(pair: Pair<File,List<Statement>>) {
        for(node in pair.second) {
            stmtVisitor.visit(node)
        }
    }
    private val stmtVisitor = object: StatementVisitor {
        override fun onFn(fn: FFunction) {
            TODO("Not yet implemented")
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
                else -> {}
            }
        }

    }

}