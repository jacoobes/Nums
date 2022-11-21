import com.github.h0tk3y.betterParse.grammar.tryParseToEnd
import com.github.h0tk3y.betterParse.parser.ErrorResult
import com.github.h0tk3y.betterParse.parser.Parsed
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DirectedAcyclicGraph
import java.io.File

class ModuleResolver(root: Pair<File,List<Statement>>) {

    private val filesVisited = HashMap<Int, List<Statement>>()
    val depGraph: DirectedAcyclicGraph<Vertex, DefaultEdge> = DirectedAcyclicGraph(DefaultEdge::class.java)
    private var foundEntry = false
    init {
        root.second.find { it is FFunction && it.main }
            ?.let { depGraph.addVertex(FVertex(it as FFunction)) }
            ?: throw Error("Could not find main entry")
        filesVisited[root.first.hashCode()] = root.second
        generate(root.second)
    }

    interface Vertex
    private inner class FVertex(val f: FFunction) : Vertex {
        override fun equals(other: Any?): Boolean {
            if(other !is FVertex) return false
            return f.token.name === other.f.token.name
        }
        override fun hashCode(): Int {
            return f.token.name.hashCode()
        }
    }
    private inner class IVertex(val i:Import, val id:Int): Vertex {

        val importedNodes = filesVisited[id]!!
        override fun equals(other: Any?): Boolean {
            if (other !is IVertex) return false
            return id == other.id
        }
        override fun toString(): String {
            return i.toString()
        }
        override fun hashCode(): Int {
            return id
        }
    }
    private fun generate(dependencies : List<Statement>) {
        for (node in dependencies) {
            when (node) {
                is FFunction -> {
                    if(foundEntry) throw Error("Found duplicate main entry whilst parsing")

                }
                is Import -> {
                    val nf = File(node.path)
                    if (!nf.exists()) throw Error("File $nf does not exist")
                    if (nf.isDirectory) throw Error("No directories allowed")
                    if (nf.extension != "nums") throw Error("Only .nums files are allowed")

                    when(val res = NumsGrammar().tryParseToEnd(nf.readText())) {
                        is ErrorResult -> println(res)
                        is Parsed -> {

                            if(!filesVisited.contains(nf.hashCode())) {
                                filesVisited[nf.hashCode()] = res.value
                                generate(res.value)
                            }
                            if(node.isNamespace) {
                                TODO()
                            } else {
                                val vtx = IVertex(node, nf.hashCode());
                            }
                            //TODO ensure functions exist when importing
                        }
                    }
                }
                else -> throw Error("Cannot have $node top level")
            }
        }
    }
}