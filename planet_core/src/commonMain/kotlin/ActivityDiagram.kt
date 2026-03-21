package org.example

import com.strumenta.antlrkotlin.parsers.generated.PlantUMLParser
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

var textToPathProvider: ((text: String, x: Double, y: Double, fontSize: Int) -> String)? = null

enum class NodeShape {
    CIRCLE, DOUBLE_CIRCLE, RECT, DIAMOND
}

enum class NodeStyle {
    FILLED, ROUNDED
}

enum class ConstColor {
    BLACK, GRAY,
}

sealed interface NodeColor {
    data class Const(val value: ConstColor) : NodeColor {
        override fun toString(): String {
            return value.name
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
class Node(
    val label: String,
    val shape: NodeShape,
    val style: List<NodeStyle>,
    val fillColor: NodeColor,
    val identifier: String = "node_" + Uuid.random().toString().replace("-", "")
)

class ActivityDiagram(activityDiagram: PlantUMLParser.Activity_diagramContext) : Diagram {
    val nodeMap = mutableMapOf<String, Node>()
    private val transitionsList = mutableListOf<String>()

    val transitions: List<String>
        get() = transitionsList

    init {
        var currentTails = listOf<Node>()

        activityDiagram.statement().forEach { stmt ->
            currentTails = processStatement(stmt, currentTails)
        }
    }

    private fun addEdge(from: Node, to: Node, label: String? = null, isBackEdge: Boolean = false) {
        val extra = buildString {
            append("arrowhead=vee")
            label?.takeIf { it.isNotBlank() }?.let {
                append(" label=\"$it\"")
            }
            if (isBackEdge) append(" constraint=false")
        }
        transitionsList.add("${from.identifier} -> ${to.identifier} [$extra];")
    }

    private fun processStatement(stmt: PlantUMLParser.StatementContext, tails: List<Node>): List<Node> {
        when {
            stmt.action() != null -> {
                val actionCtx = stmt.action()!!
                val label = actionCtx.paragraph_text()?.text?.trim() ?: ""
                val node = createNode(label, NodeShape.RECT, listOf(NodeStyle.FILLED, NodeStyle.ROUNDED), ConstColor.GRAY)
                tails.forEach { addEdge(it, node) }
                return listOf(node)
            }
            stmt.switch_stmt() != null -> {
                val ctx = stmt.switch_stmt()!!
                val condition = ctx.paragraph_text()?.text?.trim() ?: "switch"
                val diamond = createNode(condition, NodeShape.DIAMOND, emptyList(), ConstColor.GRAY)
                tails.forEach { addEdge(it, diamond) }

                val outTails = mutableListOf<Node>()
                var hasCases = false
                
                for (caseCtx in ctx.case_stmt()) {
                    hasCases = true
                    val caseCond = caseCtx.paragraph_text()?.text?.trim() ?: ""
                    
                    if (caseCtx.statement().isNotEmpty()) {
                        var caseTails = listOf(diamond)
                        val firstNodes = processStatement(caseCtx.statement().first(), emptyList())
                        firstNodes.forEach { addEdge(diamond, it, caseCond) }
                        caseTails = firstNodes
                        for (j in 1 until caseCtx.statement().size) {
                            caseTails = processStatement(caseCtx.statement()[j], caseTails)
                        }
                        outTails.addAll(caseTails)
                    } else {
                        outTails.add(diamond)
                    }
                }
                
                if (!hasCases) {
                    outTails.add(diamond)
                }
                return outTails
            }
            stmt.conditional() != null -> {
                val condCtx = stmt.conditional()!!
                class Branch(var condition: String = "", var label: String? = null, val stmts: MutableList<PlantUMLParser.StatementContext> = mutableListOf())
                val branches = mutableListOf<Branch>()
                var activeBranch = Branch()
                var expectCondition = true
                var expectLabel = false
                var isElse = false

                for (child in condCtx.children!!) {
                    if (child.text == "if" || child.text == "elseif") {
                        if (child.text == "elseif") {
                            branches.add(activeBranch)
                            activeBranch = Branch()
                        }
                        expectCondition = true
                        expectLabel = false
                    } else if (child.text == "then") {
                        expectCondition = false
                        expectLabel = true
                    } else if (child.text == "else") {
                        branches.add(activeBranch)
                        activeBranch = Branch()
                        expectCondition = false
                        expectLabel = true
                        isElse = true
                    } else if (child.text == "endif") {
                        branches.add(activeBranch)
                        break
                    } else if (child is PlantUMLParser.Paragraph_textContext) {
                        if (expectCondition) {
                            activeBranch.condition = child.text.trim()
                            expectCondition = false
                        } else if (expectLabel) {
                            activeBranch.label = child.text.trim()
                            expectLabel = false
                        }
                    } else if (child is PlantUMLParser.StatementContext) {
                        activeBranch.stmts.add(child)
                        expectLabel = false
                    }
                }
                
                var currentDiamondTails = tails
                val branchesOut = mutableListOf<Node>()
                
                for (i in 0 until branches.size) {
                    val branch = branches[i]
                    val isLastBranchAndElse = (i == branches.size - 1) && isElse

                    if (isLastBranchAndElse) {
                        var branchTails = currentDiamondTails
                        if (branch.stmts.isNotEmpty()) {
                            val firstNodes = processStatement(branch.stmts.first(), emptyList())
                            firstNodes.forEach { dest -> 
                                currentDiamondTails.forEach { 
                                    addEdge(it, dest, branch.label ?: "no") 
                                } 
                            }
                            branchTails = firstNodes
                            for (j in 1 until branch.stmts.size) {
                                branchTails = processStatement(branch.stmts[j], branchTails)
                            }
                        }
                        branchesOut.addAll(branchTails)
                    } else {
                        val diamond = createNode(branch.condition, NodeShape.DIAMOND, emptyList(), ConstColor.GRAY)
                        currentDiamondTails.forEach { 
                            val edgeLabel = if (it.shape == NodeShape.DIAMOND) "no" else null
                            addEdge(it, diamond, edgeLabel) 
                        }
                        
                        var branchTails = listOf(diamond)
                        if (branch.stmts.isNotEmpty()) {
                            val firstNodes = processStatement(branch.stmts.first(), emptyList())
                            firstNodes.forEach { addEdge(diamond, it, branch.label ?: "yes") }
                            branchTails = firstNodes
                            for (j in 1 until branch.stmts.size) {
                                branchTails = processStatement(branch.stmts[j], branchTails)
                            }
                        }
                        branchesOut.addAll(branchTails)
                        currentDiamondTails = listOf(diamond)
                    }
                }
                
                if (!isElse && currentDiamondTails.isNotEmpty()) {
                    branchesOut.addAll(currentDiamondTails)
                }
                return branchesOut
            }
            stmt.keyword_stmt() != null -> {
                val kw = stmt.keyword_stmt()!!.text.trim()
                return if (kw == "start") {
                    val node = createNode("(*)", NodeShape.CIRCLE, listOf(NodeStyle.FILLED), ConstColor.BLACK)
                    tails.forEach { addEdge(it, node) }
                    listOf(node)
                } else if (kw == "kill" || kw == "detach") {
                    emptyList()
                } else {
                    val node = createNode("(*)", NodeShape.DOUBLE_CIRCLE, listOf(NodeStyle.FILLED), ConstColor.BLACK)
                    tails.forEach { addEdge(it, node) }
                    emptyList() // Flow stops here
                }
            }
            stmt.legacy_transition() != null -> {
                // Ignore or parse simply for backwards compat
                val node = createNode("legacy", NodeShape.RECT, listOf(NodeStyle.FILLED), ConstColor.GRAY)
                tails.forEach { addEdge(it, node) }
                return listOf(node)
            }
            stmt.loop_while() != null -> {
                val ctx = stmt.loop_while()!!
                val condLabel = ctx.paragraph_text(0)?.text?.trim() ?: ""
                val diamond = createNode(condLabel, NodeShape.DIAMOND, emptyList(), ConstColor.GRAY)
                tails.forEach { addEdge(it, diamond) }

                var loopTails = listOf(diamond)
                val stmts = ctx.statement()
                if (stmts.isNotEmpty()) {
                    val firstNodes = processStatement(stmts.first(), emptyList())
                    // "Yes" edge into the loop
                    val isLabel = if (ctx.IS() != null) ctx.paragraph_text(1)?.text?.trim() else "yes"
                    firstNodes.forEach { addEdge(diamond, it, isLabel) }
                    loopTails = firstNodes
                    for (i in 1 until stmts.size) {
                        loopTails = processStatement(stmts[i], loopTails)
                    }
                }
                
                // Loop back
                loopTails.forEach { addEdge(it, diamond, isBackEdge = true) }
                
                // Exit point is the diamond, with a "no" label (graphviz edge label for the NEXT statement)
                // We'll leave the label off for now to keep mapping simple
                return listOf(diamond)
            }
            stmt.loop_repeat() != null -> {
                 // For repeat, we enter the statements immediately
                 val stmts = stmt.loop_repeat()!!.statement()
                 var loopTails = tails
                 // We need a loop start point. If there are no previous tails, create a dummy or just use first node as entry
                 // It's easier to create an invisible or point node, but let's just create an empty rect for entry
                 val entry = createNode("", NodeShape.CIRCLE, emptyList(), ConstColor.BLACK)
                 tails.forEach { addEdge(it, entry) }
                 
                 var currTails = listOf(entry)
                 for (s in stmts) {
                     currTails = processStatement(s, currTails)
                 }
                 
                 val condLabel = stmt.loop_repeat()!!.paragraph_text(0)?.text?.trim() ?: "repeat?"
                 val diamond = createNode(condLabel, NodeShape.DIAMOND, emptyList(), ConstColor.GRAY)
                 currTails.forEach { addEdge(it, diamond) }
                 
                 // loop back
                 val isLabel = if (stmt.loop_repeat()!!.IS() != null) stmt.loop_repeat()!!.paragraph_text(1)?.text?.trim() else "yes"
                 addEdge(diamond, entry, isLabel, isBackEdge = true)
                 
                 return listOf(diamond) // exit from diamond
            }
            stmt.parallel() != null -> {
                val ctx = stmt.parallel()!!
                val forkStart = createNode("", NodeShape.RECT, listOf(NodeStyle.FILLED), ConstColor.BLACK) // thick bar
                tails.forEach { addEdge(it, forkStart) }
                
                val outTails = mutableListOf<Node>()
                
                val stmts = ctx.statement()
                // ANTLR flattened the statements. We need to split them by FORK_AGAIN which are terminals?
                // Wait, parallel: FORK EOL? statement* (FORK_AGAIN EOL? statement*)* END_FORK EOL?
                // Children contain FORK_AGAIN.
                val branches = mutableListOf<MutableList<PlantUMLParser.StatementContext>>()
                var currentBranch = mutableListOf<PlantUMLParser.StatementContext>()
                for (child in ctx.children!!) {
                    if (child.text == "fork" || child.text == "fork again") {
                        if (currentBranch.isNotEmpty()) branches.add(currentBranch)
                        currentBranch = mutableListOf()
                    } else if (child is PlantUMLParser.StatementContext) {
                        currentBranch.add(child)
                    }
                }
                if (currentBranch.isNotEmpty()) branches.add(currentBranch)
                
                for (branch in branches) {
                    var currTails = listOf(forkStart)
                    for (s in branch) {
                        currTails = processStatement(s, currTails)
                    }
                    outTails.addAll(currTails)
                }
                
                val forkEnd = createNode("", NodeShape.RECT, listOf(NodeStyle.FILLED), ConstColor.BLACK)
                outTails.forEach { addEdge(it, forkEnd) }
                return listOf(forkEnd)
            }
            stmt.note_stmt() != null -> {
                val noteCtx = stmt.note_stmt()!!
                val text = noteCtx.paragraph_text()?.text?.trim() ?: ""
                val node = createNode(text, NodeShape.RECT, emptyList(), ConstColor.GRAY)
                val isFloating = noteCtx.children?.any { it.text == "floating" } == true
                if (!isFloating) {
                    tails.forEach { 
                        // Dotted edge equivalent
                        addEdge(it, node, "note") 
                    }
                }
                return tails 
            }
        }
        return tails
    }

    private fun createNode(
        label: String,
        shape: NodeShape,
        style: List<NodeStyle>,
        fillColor: ConstColor
    ): Node {
        val n = Node(label, shape, style, NodeColor.Const(fillColor))
        nodeMap[n.identifier] = n
        return n
    }

    // A very simple vertical topological layout for SVG generation
    fun toSvg(): String {
        // Find roots (nodes with no incoming edges)
        val incomingCounts = mutableMapOf<String, Int>()
        nodeMap.keys.forEach { incomingCounts[it] = 0 }
        
        val edges = mutableListOf<Edge>()
        transitionsList.forEach { transition ->
            val parts = transition.split(" -> ")
            if (parts.size == 2) {
                val from = parts[0]
                val rightPart = parts[1].split(" ")
                val to = rightPart[0]
                
                var label = ""
                val labelMatch = Regex("label=\"([^\"]+)\"").find(transition)
                if (labelMatch != null) label = labelMatch.groupValues[1]

                val isBackEdge = transition.contains("constraint=false")
                if (!isBackEdge) {
                    incomingCounts[to] = (incomingCounts[to] ?: 0) + 1
                }
                edges.add(Edge(from, to, label, isBackEdge))
            }
        }

        var roots = incomingCounts.filter { it.value == 0 }.keys.toList()
        if (roots.isEmpty() && nodeMap.isNotEmpty()) roots = listOf(nodeMap.keys.first())

        // BFS with layers
        val layers = mutableMapOf<String, Int>()
        val queue = ArrayDeque<Pair<String, Int>>()
        roots.forEach { queue.addLast(it to 0) }
        
        while (queue.isNotEmpty()) {
            val (nodeId, depth) = queue.removeFirst()
            val existingDepth = layers[nodeId] ?: -1
            if (depth > existingDepth) {
                layers[nodeId] = depth
                val outEdges = edges.filter { it.from == nodeId && !it.isBackEdge }
                outEdges.forEach { queue.addLast(it.to to depth + 1) }
            }
        }

        // Layout constants
        val nodeW = 120.0
        val nodeH = 40.0
        val vSpacing = 80.0
        val hSpacing = 160.0
        
        // Group by layer
        val layerNodes = layers.entries.groupBy { it.value }.toSortedMap()
        val coordinates = mutableMapOf<String, Pair<Double, Double>>()
        
        var maxW = 0.0
        var maxH = 0.0

        layerNodes.forEach { (depth, nodesInLayer) ->
            val y = depth * (nodeH + vSpacing) + vSpacing
            val totalW = nodesInLayer.size * nodeW + (nodesInLayer.size - 1) * hSpacing
            var x = (800.0 - totalW) / 2.0 // Center it on a fixed 800px width arbitrarily
            if (x < 40.0) x = 40.0

            nodesInLayer.forEach { entry ->
                coordinates[entry.key] = Pair(x, y)
                if (x + nodeW > maxW) maxW = x + nodeW
                if (y + nodeH > maxH) maxH = y + nodeH
                x += nodeW + hSpacing
            }
        }
        
        maxW += 40.0
        maxH += 40.0

        val svg = buildString {
            appendLine("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"$maxW\" height=\"$maxH\">")
            
            // Draw edges
            appendLine("  <g stroke=\"#333333\" fill=\"none\" stroke-width=\"1.5\">")
            edges.forEach { edge ->
                val p1 = coordinates[edge.from]
                val p2 = coordinates[edge.to]
                if (p1 != null && p2 != null) {
                    val x1 = p1.first + nodeW / 2
                    val y1 = p1.second + nodeH
                    val x2 = p2.first + nodeW / 2
                    val y2 = p2.second

                    // Draw a simple path with a slight curve or straight line vertically
                    appendLine("    <path d=\"M $x1 $y1 C $x1 ${(y1+y2)/2}, $x2 ${(y1+y2)/2}, $x2 $y2\" />")
                    
                    // Edge arrow head
                    appendLine("    <polygon points=\"${x2-4},${y2-6} ${x2+4},${y2-6} $x2,$y2\" fill=\"#333333\" stroke=\"none\"/>")

                    // Edge label
                    if (edge.label.isNotBlank()) {
                        val lx = (x1 + x2) / 2 + 10
                        val ly = (y1 + y2) / 2
                        val provider = textToPathProvider
                        if (provider != null) {
                            val pathD = provider(edge.label, lx, ly.toDouble(), 12)
                            appendLine("    <path d=\"$pathD\" fill=\"#000000\" stroke=\"none\" />")
                        } else {
                            appendLine("    <text x=\"$lx\" y=\"$ly\" dy=\"4\" font-family=\"sans-serif\" font-size=\"12\" fill=\"#000000\">${edge.label}</text>")
                        }
                    }
                }
            }
            appendLine("  </g>")

            // Draw nodes
            appendLine("  <g font-family=\"sans-serif\" font-size=\"14\" text-anchor=\"middle\">")
            nodeMap.forEach { (id, node) ->
                val p = coordinates[id]
                if (p != null) {
                    val cx = p.first + nodeW / 2
                    val cy = p.second + nodeH / 2
                    val fillHex = if (node.fillColor.toString() == "BLACK") "#333333" else "#f0f0f0"
                    val stroke = "#333333"

                    when (node.shape) {
                        NodeShape.CIRCLE -> {
                            appendLine("    <circle cx=\"$cx\" cy=\"$cy\" r=\"15\" fill=\"$fillHex\" stroke=\"$stroke\" stroke-width=\"2\"/>")
                        }
                        NodeShape.DOUBLE_CIRCLE -> {
                            appendLine("    <circle cx=\"$cx\" cy=\"$cy\" r=\"15\" fill=\"none\" stroke=\"$stroke\" stroke-width=\"2\"/>")
                            appendLine("    <circle cx=\"$cx\" cy=\"$cy\" r=\"10\" fill=\"$fillHex\" stroke=\"$stroke\" stroke-width=\"2\"/>")
                        }
                        NodeShape.DIAMOND -> {
                            val dw = 20.0
                            val dh = 20.0
                            appendLine("    <polygon points=\"$cx,${cy-dh} ${cx+dw},$cy $cx,${cy+dh} ${cx-dw},$cy\" fill=\"#f9f9db\" stroke=\"$stroke\" stroke-width=\"1.5\"/>")
                            // Small label next to diamond sometimes
                        }
                        NodeShape.RECT -> {
                            if (node.label.isBlank() && node.fillColor.toString() == "BLACK") {
                                // Fork/join thick bar
                                appendLine("    <rect x=\"${p.first}\" y=\"${cy-3}\" width=\"$nodeW\" height=\"6\" fill=\"$fillHex\" />")
                            } else {
                                val rx = if (node.style.contains(NodeStyle.ROUNDED)) "10" else "0"
                                appendLine("    <rect x=\"${p.first}\" y=\"${p.second}\" width=\"$nodeW\" height=\"$nodeH\" rx=\"$rx\" ry=\"$rx\" fill=\"#fefefa\" stroke=\"$stroke\" stroke-width=\"1.5\"/>")
                                val textStr = node.label.replace("<", "&lt;").replace(">", "&gt;")
                                val provider = textToPathProvider
                                if (provider != null) {
                                    val pathD = provider(node.label, cx, cy, 14)
                                    appendLine("    <path d=\"$pathD\" fill=\"#000000\" stroke=\"none\" />")
                                } else {
                                    appendLine("    <text x=\"$cx\" y=\"$cy\" dy=\"5\" fill=\"#000000\">$textStr</text>")
                                }
                            }
                        }
                    }
                }
            }
            appendLine("  </g>")
            appendLine("</svg>")
        }
        return svg
    }
}

private data class Edge(val from: String, val to: String, val label: String, val isBackEdge: Boolean = false)