package nodes

import StatementVisitor
import org.w3c.dom.Text
import types.Type
import types.Types

enum class Vis {
    Show,
    Hide
}

sealed interface Statement : Node {
    fun <R> accept(visitor: StatementVisitor<R>) = visitor.visit(this)
}

data class ExpressionStatement(val expr: Expr) : Statement

data class Assign(val tok: TextId, val newVal: Expr) : Statement

data class Val(val isAssignable: Boolean, val token: TextId, val expr: Expr, var type: Type) : Statement

data class Block(val stmts: List<Statement>) : Statement

data class Return(val expr: Expr) : Statement

data class Iif(val condition: Expr, val thenBody: Statement, val elseBody: Statement) : Statement

data class Loop(val condition: Expr, val block: Statement) : Statement

data class FFunction(
    val vis: Vis = Vis.Hide,
    val name: TextId,
    var fullName: String,
    val args: List<TextId>,
    val block: Statement,
    val type: Types.TFn
) : Statement {
    fun isMain(): Boolean {
        return name == TextId("main")
    }
}
//data class Dataset(val vis: Vis = Vis.Show, val name: TextId, val elements: List<TextId>, val type: Types.TDataSet) : Statement
data class Space(val vis: Vis = Vis.Show, val name: TextId, val elements: List<Statement>) : Statement
//data class TraitDeclaration(val vis: Vis = Vis.Show, val traitName: TextId, val body: List<FFunction>) : Statement
//data class Import(
//    val idents: List<TextId>,
//    val path: String,
//    val isNamespace: Boolean,
//    val file: java.nio.file.Path
//) : Statement {
//    fun uid(): Int {
//        return file.hashCode()
//    }
//}
object Skip : Statement