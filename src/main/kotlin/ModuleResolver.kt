import com.github.h0tk3y.betterParse.grammar.parseToEnd
import nodes.*
import java.nio.file.Files
import java.nio.file.Files.exists
import java.nio.file.Path
import java.util.LinkedList
import java.util.Stack
import kotlin.io.path.extension
import kotlin.io.path.isDirectory
import kotlin.io.path.isReadable

class ModuleResolver {
    companion object {
        val dependencyMap: HashMap<Path, List<Statement>> = hashMapOf()
        private fun Path.sanityCheck() {
            if(!exists(this)) throw Error("File $this does not exist")
            if(isDirectory()) throw Error("No directories allowed")
            if(extension != "nums") throw Error("Only .nums files are allowed")
            if(!isReadable()) throw Error("Cannot write to this file")
        }

        /**
         * DFS Search of all nodes. What this also does is check each node body. If the node uses hidden nodes, include those nodes
         * into the parsed grammar as well.
         */
        fun generateFiles(root: Path) {
            val queue = Stack<Path>()
            queue.add(root)
            while(queue.isNotEmpty()) {
                val current = queue.pop()
                val grammar = NumsGrammar(current)
                val parsed = grammar.parseToEnd(Files.readString(current))
                dependencyMap[current] = parsed
                for(node in parsed) {
                    if(node is Import) {
                        if(!dependencyMap.contains(node.file)) {
                            node.file.sanityCheck()
                            queue.add(node.file)
                            dependencyMap[node.file] = parsed
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