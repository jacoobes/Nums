import com.github.h0tk3y.betterParse.grammar.parseToEnd
import nodes.*
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DirectedAcyclicGraph
import java.io.File
import java.util.LinkedList

class ModuleResolver {
    interface Vertex
    class FileVertex(val file: File) : Vertex {
        override fun hashCode(): Int {
            return file.hashCode()
        }

        override fun toString(): String {
            return file.name
        }
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as FileVertex

            if (file != other.file) return false
            return true
        }
    }
    class NSVertex(val name: String) : Vertex {
        override fun equals(other: Any?): Boolean {
            if(other !is NSVertex) return false
            return name == other.name
        }
        override fun hashCode(): Int {
            return name.hashCode()
        }

        override fun toString(): String {
            return "[Namespace: $name]"
        }
    }
    class SearchableFnVertex(val name: Variable, val len: Int) : Vertex
    class FnVertex(val uid: Int, val fn: FFunction) : Vertex {
        override fun equals(other: Any?):  Boolean {
            return if(other is FFunction) {
                other.name == fn.name && fn.args.size == other.args.size
            } else {
                if(other is FnVertex) {
                    return uid == other.uid && fn == other.fn
                }
                return fn == other
            }
        }
        override fun toString(): String {
            return fn.toString()
        }

        override fun hashCode(): Int {
            return fn.hashCode()
        }
    }
    class SVertex(val space: Space) : Vertex
    companion object {
        val depMap: HashMap<File, List<Statement>> = hashMapOf()
        val pathGraph = HashMap<File, DirectedAcyclicGraph<Vertex, DefaultEdge>>()
        private fun File.sanityCheck() {
            if(!exists()) throw Error("File $this does not exist")
            if(isDirectory) throw Error("No directories allowed")
            if(extension != "nums") throw Error("Only .nums files are allowed")
            if(!canRead()) throw Error("Cannot write to this file")
        }
        fun createFileImportGraph() {
            for((file, tree) in depMap) {
                val graph = DirectedAcyclicGraph<Vertex, DefaultEdge>(DefaultEdge::class.java)
                val root = FileVertex(file)
                graph.addVertex(root)
                for(node in tree) {
                     when(node) {
                         is FFunction -> {
                             val fnVertex = FnVertex(file.hashCode(),node).also { graph.addVertex(it) }
                             graph.addEdge(root, fnVertex)
                         }
                         is Import -> {
                             // A naive approach of filtering all nodes that aren't imports, it is pretty slow
                            val impTree = depMap[node.file]?.filterNot { it is Import }!!
                            if(node.isNamespace) {
                               val nsVertex = NSVertex(node.idents[0].name)
                               graph.addVertex(nsVertex)
                               graph.addEdge(root, nsVertex)
                               for(imported in impTree) {
                                   when(imported) {
                                       is FFunction -> {
                                           val fnVertex = FnVertex(node.file.hashCode(), imported)
                                           graph.addVertex(fnVertex)
                                           graph.addEdge(nsVertex, fnVertex)
                                       }
                                       is Space -> {}
                                       else -> {}
                                   }
                               }
                            } else {
                                val idents = HashSet(node.idents)
                                for(imported in impTree) {
                                    when(imported) {
                                        is FFunction -> {
                                            if(idents.contains(imported.name)) {
                                                val iFn = FnVertex(node.file.hashCode(), imported)
                                                graph.addVertex(iFn)
                                                graph.addEdge(root, iFn)
                                            }
                                        }
                                        is Space -> {

                                        }
                                        else -> {}
                                    }
                                }
                            }
                         }
                         is Space -> {}
                         else -> {}
                     }
                }
                pathGraph[file] = graph
            }
        }
        fun generateFiles(root: File) {
            val queue = LinkedList<File>()
            val grammar = NumsGrammar()
            queue.add(root)
            while(queue.isNotEmpty()) {
                val current = queue.poll()
                val parsed = grammar.parseToEnd(current.readText())
                depMap[current] = parsed
                for(node in parsed) {
                    if(node is Import) {
                        if(!depMap.contains(node.file)) {
                            node.file.sanityCheck()
                            queue.add(node.file)
                            depMap[node.file] = parsed
                        }
                    }
                }
            }
        }
    }


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