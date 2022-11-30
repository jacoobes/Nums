import com.github.h0tk3y.betterParse.grammar.parseToEnd
import nodes.*
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DirectedAcyclicGraph
import java.io.File
import java.util.LinkedList

class ModuleResolver(root: File) {

    init {
        root.sanityCheck()
        val filesGenerated = generateFiles(root)
        println(filesGenerated)
    }
    companion object {
        var depGraph: HashMap<File, List<Statement>> = hashMapOf()
        fun File.sanityCheck() {
            if(!exists()) throw Error("File $this does not exist")
            if(isDirectory) throw Error("No directories allowed")
            if(extension != "nums") throw Error("Only .nums files are allowed")
            if(!canRead()) throw Error("Cannot write to this file")
        }
        fun generateFiles(root: File) {
            val queue = LinkedList<File>()
            val grammar = NumsGrammar()
            queue.add(root)
            while(queue.isNotEmpty()) {
                val current = queue.poll()
                val parsed = grammar.parseToEnd(current.readText())
                depGraph[current] = parsed
                for(node in parsed) {
                    if(node is Import) {
                        if(!depGraph.contains(node.file)) {
                            node.file.sanityCheck()
                            queue.add(node.file)
                            depGraph[node.file] = parsed
                        }
                    }
                }
            }
        }
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
        override fun toString(): String {
            return f.toString()
        }
    }
    private inner class IVertex(val i: Import, val id:Int): Vertex {
        override fun equals(other: Any?): Boolean {
            if (other !is IVertex) return false
            return id == other.id
        }
        override fun toString(): String {
            return "[import: $i id: $id]"
        }
        override fun hashCode(): Int {
            return id
        }
    }
    private inner class NVertex(val name: Variable, val path: String, val id: Int) : Vertex {
        override fun equals(other: Any?): Boolean {
            if (other !is NVertex) return false
            return id == other.id
        }
        override fun toString(): String {
            return "[import-namespace: $name id: $id path: $path]"
        }
        override fun hashCode(): Int {
            return id
        }
    }
    private inner class SVertex(val space: Space) : Vertex


//    private fun generateGraph(dependencies : List<Statement>) {
//        for (node in dependencies) {
//            when (node) {
//                is FFunction -> {
//                    depGraph.addVertex(FVertex(node))
//                }
//                is Import -> {
//                    val nf = File(node.path)
//                    nf.sanityCheck() //catches anything that may be wrong with the file being imported
//                    val hash = nf.hashCode()
//                    val iNode = IVertex(node, hash)
//                    if(!depGraph.containsVertex(iNode)) {
//                        depGraph.addVertex(iNode)
//                    }
//                    when(val res = NumsGrammar().tryParseToEnd(nf.readText())) {
//                        is ErrorResult -> println(res)
//                        is Parsed -> {
//                            //this makes filesVisited[hash] always not null!
//                            if(!filesVisited.contains(hash)) {
//                                filesVisited[hash] = res.value
//                                generateGraph(res.value)
//                            }
//                            val importVertex = IVertex(node, hash)
//
//                            if(node.isNamespace) {
//                                val nVertex = NVertex(node.idents.first(), id=hash, path=node.path)
//                                depGraph.addVertex(nVertex)
//
//                                filesVisited[hash]!!
//                                    .asSequence()
//                                    .filterIsInstance<FFunction>()
//                                    .forEach {
//                                        val fTex = FVertex(it)
//                                        depGraph.addVertex(fTex)
//                                        depGraph.addEdge(nVertex, fTex)
//                                    }
//                            } else {
//                                val tokens = HashSet(node.idents)
//                                filesVisited[hash]!!
//                                    .asSequence()
//                                    .filterIsInstance<FFunction>()
//                                    .filter { tokens.contains(it.token) }
//                                    .forEach {
//                                        val fTex = FVertex(it)
//                                        depGraph.addVertex(fTex)
//                                        depGraph.addEdge(importVertex, fTex)
//                                    }
//                            }
//                        }
//                    }
//                }
//                is Space -> {
//                    depGraph.addVertex(SVertex(node))
//                }
//                else -> throw Error("Cannot have $node top level")
//            }
//        }
//    }
}