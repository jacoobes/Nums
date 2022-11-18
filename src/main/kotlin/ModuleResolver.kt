import com.github.h0tk3y.betterParse.grammar.tryParseToEnd
import com.github.h0tk3y.betterParse.parser.ErrorResult
import com.github.h0tk3y.betterParse.parser.Parsed
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DirectedAcyclicGraph
import java.io.File

class ModuleResolver(root: Pair<File,List<Statement>>) {

    val filesVisited = HashMap<File, List<Statement>>()
    val depGraph: DirectedAcyclicGraph<Vertex, DefaultEdge> = DirectedAcyclicGraph(DefaultEdge::class.java)
    private var foundEntry = false
    init {
        root.second.find { it is FFunction && it.main } ?: throw Error("Could not find main entry")
        filesVisited[root.first] = root.second
        generate(root.second)
    }

    interface Vertex
    data class FVertex(val f: FFunction) : Vertex {
        override fun equals(other: Any?): Boolean {
            if(other !is FVertex) return false
            return f.token.name === other.f.token.name
        }
        override fun hashCode(): Int {
            return f.token.name.hashCode()
        }
    }
    class IVertex(val i : Import): Vertex {
        override fun equals(other: Any?): Boolean {
            if (other !is IVertex) return false
            return i.toString() == other.toString()
        }
        override fun toString(): String {
            return i.toString()
        }
        override fun hashCode(): Int {
            return i.toString().hashCode()
        }
    }
    private fun generate(dependencies : List<Statement>) {
        for (node in dependencies) {
            when (node) {
                is FFunction -> {
                    val vtx = FVertex(node)
                    if(foundEntry) throw Error("Found duplicate main entry whilst parsing")
                    if(!depGraph.containsVertex(vtx)) {
                        if(node.main) foundEntry = true
                        depGraph.addVertex(vtx)
                    }
                }
                is Import -> {
                    val vtx = IVertex(node)
                    if(!depGraph.containsVertex(vtx)) {
                        depGraph.addVertex(vtx)
                    }
                    val nf = File(node.path)
                    if (!nf.exists()) throw Error("File $nf does not exist")
                    if (nf.isDirectory) throw Error("No directories allowed")
                    if (nf.extension != "nums") throw Error("Only .nums files are allowed")
                    when(val res = NumsGrammar().tryParseToEnd(nf.readText())) {
                        is ErrorResult -> println(res)
                        is Parsed -> {
                            if(!filesVisited.contains(nf)) {
                                filesVisited[nf] = res.value
                                generate(res.value)
                            }
                        }
                    }
                }
                else -> throw Error("Cannot have $node top level")
            }
        }
    }
}