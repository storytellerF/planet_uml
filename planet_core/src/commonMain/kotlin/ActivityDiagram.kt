package org.example

import com.strumenta.antlrkotlin.parsers.generated.PlantUMLParser
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

var textToPathProvider: ((text: String, x: Double, y: Double, fontSize: Int) -> String)? = null

enum class NodeShape {
    CIRCLE, DOUBLE_CIRCLE, RECT, DIAMOND, POINT
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
    data class Hex(val code: String) : NodeColor {
        override fun toString() = code
    }
}

@OptIn(ExperimentalUuidApi::class)
class Node(
    val label: String,
    val shape: NodeShape,
    val style: List<NodeStyle>,
    val fillColor: NodeColor,
    val identifier: String = "node_" + Uuid.random().toString().replace("-", ""),
    val partition: String? = null,
    val swimlane: String? = null
)

class ActivityDiagram(activityDiagram: PlantUMLParser.Activity_diagramContext) : Diagram {
    val nodeMap = mutableMapOf<String, Node>()
    private val transitionsList = mutableListOf<String>()

    private val labels = mutableMapOf<String, Node>()

    private var pendingArrowStyle: ArrowStyle? = null
    private var pendingArrowLabel: String? = null
    
    private var currentPartition: String? = null
    private var currentSwimlane: String? = null

    private data class ArrowStyle(val color: String?, val isDashed: Boolean, val isLine: Boolean)

    private fun parseArrowStyle(ctx: PlantUMLParser.Activity_arrowContext?): ArrowStyle? {
        if (ctx == null) return null
        val arrowStyleCtx = ctx.arrow_style() ?: ctx.activity_arrow_right()?.arrow_style() ?: ctx.activity_arrow_dir()?.arrow_style() ?: ctx.activity_line()?.arrow_style()
        
        val isLine = ctx.activity_line() != null || ctx.text == "--" || ctx.text.startsWith("--[")
        
        var color: String? = null
        var isDashed = false
        
        if (arrowStyleCtx != null) {
            val styleText = arrowStyleCtx.ARROW_STYLE()?.text ?: ""
            val content = styleText.removePrefix("[").removeSuffix("]")
            content.split(",").map { it.trim() }.forEach { part ->
                if (part == "dashed" || part == "dotted") isDashed = true
                else if (part.startsWith("#")) color = part
            }
        }
        return ArrowStyle(color, isDashed, isLine)
    }

    private val unresolvedGotos = mutableListOf<GotoResolution>()
    private var currentLoopBreaks: MutableList<Node>? = null

    private data class GotoResolution(val tails: List<Node>, val labelName: String, val isBackward: Boolean, val style: ArrowStyle?, val label: String?)

    val transitions: List<String>
        get() = transitionsList

    init {
        var currentTails = listOf<Node>()

        activityDiagram.statement().forEach { stmt ->
            currentTails = processStatement(stmt, currentTails)
        }

        unresolvedGotos.forEach { gotoRes ->
            val target = labels[gotoRes.labelName]
            if (target != null) {
                gotoRes.tails.forEach { internalAddEdge(it, target, gotoRes.label, isBackEdge = gotoRes.isBackward, color = gotoRes.style?.color, dashed = gotoRes.style?.isDashed == true, isLine = gotoRes.style?.isLine == true) }
            } else {
                val errNode = createNode("Missing label: ${gotoRes.labelName}", NodeShape.RECT, listOf(NodeStyle.FILLED), ConstColor.GRAY)
                gotoRes.tails.forEach { addEdge(it, errNode) }
            }
        }
    }

    private fun internalAddEdge(from: Node, to: Node, label: String? = null, isBackEdge: Boolean = false, color: String? = null, dashed: Boolean = false, isLine: Boolean = false) {
        val extra = buildString {
            append("arrowhead=" + if (isLine) "none" else "vee")
            label?.takeIf { it.isNotBlank() }?.let {
                append(" label=\"$it\"")
            }
            if (isBackEdge) append(" constraint=false")
            if (color != null) append(" color=\"$color\"")
            if (dashed) append(" style=\"dashed\"")
        }
        transitionsList.add("${from.identifier} -> ${to.identifier} [$extra];")
    }
    
    private fun addEdge(from: Node, to: Node, label: String? = null, isBackEdge: Boolean = false) = internalAddEdge(from, to, label, isBackEdge)

    private fun processStatement(stmt: PlantUMLParser.StatementContext, tails: List<Node>): List<Node> {
        val entryStyle = pendingArrowStyle
        val entryLabel = pendingArrowLabel
        if (stmt.arrow_stmt() == null) {
            pendingArrowStyle = null
            pendingArrowLabel = null
        }
        
        fun addEntryEdge(from: Node, to: Node, explicitLabel: String? = null, isBackEdge: Boolean = false) {
             val finalLabel = explicitLabel ?: entryLabel
             val color = entryStyle?.color
             val isDashed = entryStyle?.isDashed == true
             val isLine = entryStyle?.isLine == true
             internalAddEdge(from, to, finalLabel, isBackEdge, color, isDashed, isLine)
        }

        when {
            stmt.arrow_stmt() != null -> {
                val arrowCtx = stmt.arrow_stmt()!!.activity_arrow()
                pendingArrowStyle = parseArrowStyle(arrowCtx)
                pendingArrowLabel = stmt.arrow_stmt()!!.transition_label()?.paragraph_text()?.text
                return tails
            }
            stmt.action() != null -> {
                val actionCtx = stmt.action()!!
                var label = actionCtx.paragraph_text().text.trim().replace("<:cloud_with_rain:>", "🌧️")
                    .replace("<:umbrella:>", "☔").replace("<:closed_umbrella:>", "🌂")
                val colorSpec = actionCtx.color_spec()
                val color = if (colorSpec != null) {
                    NodeColor.Hex(colorSpec.COLOR_SPEC().text)
                } else NodeColor.Const(ConstColor.GRAY)
                val stereos = actionCtx.stereotype()
                val stereotype = if (stereos.isNotEmpty()) stereos[0].paragraph_text()?.text?.trim() else null
                if (stereotype != null && stereotype != "icon") {
                    label = "<<$stereotype>>\n$label"
                }
                val isIcon = stereotype == "icon"
                val node = createNode(
                    label,
                    if (isIcon) NodeShape.POINT else NodeShape.RECT,
                    if (isIcon) emptyList() else listOf(NodeStyle.FILLED, NodeStyle.ROUNDED),
                    color
                )
                tails.forEach { addEntryEdge(it, node) }
                return listOf(node)
            }
            stmt.short_action_stmt() != null -> {
                val actionCtx = stmt.short_action_stmt()!!
                var label = actionCtx.paragraph_text().text.trim().replace("<:cloud_with_rain:>", "🌧️")
                    .replace("<:umbrella:>", "☔").replace("<:closed_umbrella:>", "🌂")
                val color = NodeColor.Const(ConstColor.GRAY)
                val stereotype = actionCtx.stereotype()?.paragraph_text()?.text?.trim()
                if (stereotype != null && stereotype != "icon") {
                    label = "<<$stereotype>>\n$label"
                }
                val isIcon = stereotype == "icon"
                val node = createNode(
                    label,
                    if (isIcon) NodeShape.POINT else NodeShape.RECT,
                    if (isIcon) emptyList() else listOf(NodeStyle.FILLED, NodeStyle.ROUNDED),
                    color
                )
                tails.forEach { addEntryEdge(it, node) }
                return listOf(node)
            }
            stmt.switch_stmt() != null -> {
                val ctx = stmt.switch_stmt()!!
                val condition = ctx.paragraph_text()?.text?.trim() ?: "switch"
                val diamond = createNode(condition, NodeShape.DIAMOND, emptyList(), ConstColor.GRAY)
                tails.forEach { addEntryEdge(it, diamond) }

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
                            val branchEntry = createNode("", NodeShape.POINT, emptyList(), ConstColor.GRAY)
                            currentDiamondTails.forEach { 
                                if (it in tails) addEntryEdge(it, branchEntry, branch.label ?: "no")
                                else addEdge(it, branchEntry, branch.label ?: "no")
                            }
                            branchTails = listOf(branchEntry)
                            for (j in 0 until branch.stmts.size) {
                                branchTails = processStatement(branch.stmts[j], branchTails)
                            }
                        }
                        branchesOut.addAll(branchTails)
                    } else {
                        val diamond = createNode(branch.condition, NodeShape.DIAMOND, emptyList(), ConstColor.GRAY)
                        currentDiamondTails.forEach { 
                            val edgeLabel = if (it.shape == NodeShape.DIAMOND) "no" else null
                            if (it in tails) addEntryEdge(it, diamond, edgeLabel)
                            else addEdge(it, diamond, edgeLabel)
                        }
                        
                        var branchTails = listOf(diamond)
                        if (branch.stmts.isNotEmpty()) {
                            val branchEntry = createNode("", NodeShape.POINT, emptyList(), ConstColor.GRAY)
                            addEdge(diamond, branchEntry, branch.label ?: "yes")
                            branchTails = listOf(branchEntry)
                            for (j in 0 until branch.stmts.size) {
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
                    tails.forEach { addEntryEdge(it, node) }
                    listOf(node)
                } else if (kw == "kill" || kw == "detach") {
                    emptyList()
                } else {
                    val node = createNode("(*)", NodeShape.DOUBLE_CIRCLE, listOf(NodeStyle.FILLED), ConstColor.BLACK)
                    tails.forEach { addEntryEdge(it, node) }
                    emptyList() // Flow stops here
                }
            }
            stmt.legacy_transition() != null -> {
                // Ignore or parse simply for backwards compat
                val node = createNode("legacy", NodeShape.RECT, listOf(NodeStyle.FILLED), ConstColor.GRAY)
                tails.forEach { addEntryEdge(it, node) }
                return listOf(node)
            }
            stmt.loop_while() != null -> {
                val loopCtx = stmt.loop_while()!!
                val conditionText = loopCtx.paragraph_text(0)?.text?.trim()?.replace("<:cloud_with_rain:>", "🌧️")
                    ?.replace("<:umbrella:>", "☔")?.replace("<:closed_umbrella:>", "🌂") ?: ""
                val conditionNode = createNode(conditionText, NodeShape.DIAMOND, emptyList(), ConstColor.GRAY)
                tails.forEach { addEntryEdge(it, conditionNode) }

                var loopTails = listOf(conditionNode)
                val stmts = loopCtx.statement()
                if (stmts.isNotEmpty()) {
                    val firstNodes = processStatement(stmts.first(), emptyList())
                    // "Yes" edge into the loop
                    val isLabel = if (loopCtx.IS() != null) loopCtx.paragraph_text(1)?.text?.trim() else "yes"
                    firstNodes.forEach { addEdge(conditionNode, it, isLabel) }
                    loopTails = firstNodes
                    for (i in 1 until stmts.size) {
                        loopTails = processStatement(stmts[i], loopTails)
                    }
                }
                
                // Loop back
                loopTails.forEach { addEdge(it, conditionNode, isBackEdge = true) }
                
                // Exit point is the diamond, with a "no" label (graphviz edge label for the NEXT statement)
                // We'll leave the label off for now to keep mapping simple
                return listOf(conditionNode)
            }
            stmt.loop_repeat() != null -> {
                 val stmts = stmt.loop_repeat()!!.statement()
                 val entry = createNode("", NodeShape.CIRCLE, emptyList(), ConstColor.BLACK)
                 tails.forEach { addEntryEdge(it, entry) }
                 
                 val oldBreaks = currentLoopBreaks
                 currentLoopBreaks = mutableListOf()
                 
                 var currTails = listOf(entry)
                 for (s in stmts) {
                     currTails = processStatement(s, currTails)
                 }
                 
                 val condLabel = stmt.loop_repeat()!!.paragraph_text(0)?.text?.trim() ?: "repeat?"
                 val diamond = createNode(condLabel, NodeShape.DIAMOND, emptyList(), ConstColor.GRAY)
                 currTails.forEach { addEdge(it, diamond) }
                 
                 val isLabel = if (stmt.loop_repeat()!!.IS() != null) stmt.loop_repeat()!!.paragraph_text(1)?.text?.trim() else "yes"
                 addEdge(diamond, entry, isLabel, isBackEdge = true)
                 
                 val outTails = mutableListOf<Node>(diamond)
                 outTails.addAll(currentLoopBreaks ?: emptyList())
                 currentLoopBreaks = oldBreaks
                 
                 return outTails
            }
            stmt.parallel() != null -> {
                val ctx = stmt.parallel()!!
                val forkStart = createNode("", NodeShape.RECT, listOf(NodeStyle.FILLED), ConstColor.BLACK) // thick bar
                tails.forEach { addEntryEdge(it, forkStart) }
                
                val outTails = mutableListOf<Node>()
                
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
            stmt.split_stmt() != null -> {
                val ctx = stmt.split_stmt()!!
                val splitStart = createNode("", NodeShape.RECT, listOf(NodeStyle.FILLED), ConstColor.BLACK)
                tails.forEach { addEntryEdge(it, splitStart) }
                
                val outTails = mutableListOf<Node>()
                
                val branches = mutableListOf<MutableList<PlantUMLParser.StatementContext>>()
                var currentBranch = mutableListOf<PlantUMLParser.StatementContext>()
                for (child in ctx.children!!) {
                    if (child.text == "split" || child.text == "split again") {
                        if (currentBranch.isNotEmpty()) branches.add(currentBranch)
                        currentBranch = mutableListOf()
                    } else if (child is PlantUMLParser.StatementContext) {
                        currentBranch.add(child)
                    }
                }
                if (currentBranch.isNotEmpty()) branches.add(currentBranch)
                
                for (branch in branches) {
                    var currTails = listOf(splitStart)
                    for (s in branch) {
                        currTails = processStatement(s, currTails)
                    }
                    outTails.addAll(currTails)
                }
                
                val splitEnd = createNode("", NodeShape.RECT, listOf(NodeStyle.FILLED), ConstColor.BLACK)
                outTails.forEach { addEdge(it, splitEnd) }
                return listOf(splitEnd)
            }
            stmt.break_stmt() != null -> {
                currentLoopBreaks?.addAll(tails)
                return emptyList()
            }
            stmt.goto_stmt() != null -> {
                val labelName = stmt.goto_stmt()!!.identifier()?.text ?: ""
                val isBackward = labels.containsKey(labelName)
                unresolvedGotos.add(GotoResolution(tails, labelName, isBackward, entryStyle, entryLabel))
                return emptyList()
            }
            stmt.label_stmt() != null -> {
                val labelName = stmt.label_stmt()!!.identifier()?.text ?: ""
                val node = createNode("", NodeShape.CIRCLE, emptyList(), ConstColor.BLACK)
                tails.forEach { addEntryEdge(it, node) }
                labels[labelName] = node
                return listOf(node)
            }
            stmt.note_stmt() != null -> {
                val noteCtx = stmt.note_stmt()!!
                val text = noteCtx.paragraph_text()?.text?.trim() ?: ""
                val node = createNode(text, NodeShape.RECT, emptyList(), ConstColor.GRAY)
                val isFloating = noteCtx.children?.any { it.text == "floating" } == true
                if (!isFloating) {
                    tails.forEach { 
                        // Dotted edge equivalent
                        addEntryEdge(it, node, "note") 
                    }
                }
                return tails 
            }
            stmt.partition_stmt() != null -> {
                val ctx = stmt.partition_stmt()!!
                val name = ctx.paragraph_text()?.text?.trim() ?: "group"
                val oldPartition = currentPartition
                currentPartition = name
                var currTails = tails
                for (s in ctx.statement()) {
                    currTails = processStatement(s, currTails)
                }
                currentPartition = oldPartition
                return currTails
            }
            stmt.swimlane_stmt() != null -> {
                val lane = stmt.swimlane_stmt()!!.paragraph_text()?.text?.trim() ?: ""
                currentSwimlane = lane
                return tails
            }
            stmt.connector_stmt() != null -> {
                val ctx = stmt.connector_stmt()!!
                val name = ctx.identifier()?.text ?: ""
                val colorSpec = ctx.color_spec()
                val color = if (colorSpec != null) {
                    NodeColor.Hex(colorSpec.COLOR_SPEC().text)
                } else NodeColor.Const(ConstColor.GRAY)
                val nodeId = "connector_$name"
                var node = nodeMap[nodeId]
                if (node == null) {
                    node = Node(name, NodeShape.CIRCLE, emptyList(), color, identifier = nodeId, partition = currentPartition, swimlane = currentSwimlane)
                    nodeMap[nodeId] = node
                } else if (colorSpec != null && node.fillColor is NodeColor.Const) {
                    // Update color if not set
                    val newNode = Node(node.label, node.shape, node.style, color, identifier = nodeId, partition = node.partition, swimlane = node.swimlane)
                    nodeMap[nodeId] = newNode
                    node = newNode
                }
                tails.forEach { 
                    if (it.identifier != node?.identifier) {
                        addEntryEdge(it, node!!) 
                    }
                }
                return listOf(node!!)
            }
        }
        return tails
    }

    private fun createNode(
        label: String,
        shape: NodeShape,
        style: List<NodeStyle>,
        fillColor: ConstColor
    ) = createNode(label, shape, style, NodeColor.Const(fillColor))

    private fun createNode(
        label: String,
        shape: NodeShape,
        style: List<NodeStyle>,
        fillColor: NodeColor
    ): Node {
        val n = Node(label, shape, style, fillColor, partition = currentPartition, swimlane = currentSwimlane)
        nodeMap[n.identifier] = n
        return n
    }

    // A very simple vertical topological layout for SVG generation
    override fun toSvg(): String {
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

                var color: String? = null
                val colorMatch = Regex("color=\"([^\"]+)\"").find(transition)
                if (colorMatch != null) color = colorMatch.groupValues[1]

                val isBackEdge = transition.contains("constraint=false")
                val isDashed = transition.contains("style=\"dashed\"")
                val isLine = transition.contains("arrowhead=none")
                
                if (!isBackEdge) {
                    incomingCounts[to] = (incomingCounts[to] ?: 0) + 1
                }
                edges.add(Edge(from, to, label, isBackEdge, color, if (isDashed) "dashed" else null, !isLine))
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
                if (depth < 1000) {
                    val outEdges = edges.filter { it.from == nodeId && !it.isBackEdge }
                    outEdges.forEach { queue.addLast(it.to to depth + 1) }
                }
            }
        }

        // Layout constants
        val nodeW = 120.0
        val nodeH = 40.0
        val vSpacing = 80.0
        val hSpacing = 160.0
        
        // Group by layer
        val layerNodes = layers.entries.groupBy { it.value }.entries.sortedBy { it.key }
        val coordinates = mutableMapOf<String, Pair<Double, Double>>()
        
        var maxW = 0.0
        var maxH = 0.0

        val swimlanes = nodeMap.values.mapNotNull { it.swimlane }.distinct()
        val hasSwimlanes = swimlanes.isNotEmpty()
        val swimlaneWidth = 300.0 // Arbitrary width per swimlane

        layerNodes.forEach { (depth, nodesInLayer) ->
            val y = depth * (nodeH + vSpacing) + vSpacing + (if(hasSwimlanes) 40.0 else 0.0)
            
            if (hasSwimlanes) {
                // Group nodes in this layer by swimlane
                val nodesBySwimlane = nodesInLayer.groupBy { nodeMap[it.key]?.swimlane ?: "" }
                nodesBySwimlane.forEach { (swimlane, laneNodes) ->
                    val laneIndex = if (swimlane.isEmpty()) 0 else swimlanes.indexOf(swimlane)
                    val baseLaneX = laneIndex * swimlaneWidth
                    val totalW = laneNodes.size * nodeW + (laneNodes.size - 1) * 20.0
                    var x = baseLaneX + (swimlaneWidth - totalW) / 2.0
                    if (x < 40.0) x = 40.0
                    
                    laneNodes.forEach { entry ->
                        coordinates[entry.key] = Pair(x, y)
                        if (x + nodeW > maxW) maxW = x + nodeW
                        if (y + nodeH > maxH) maxH = y + nodeH
                        x += nodeW + 20.0
                    }
                }
            } else {
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
        }
        
        if (hasSwimlanes) {
            val totalSwimlaneW = swimlanes.size * swimlaneWidth
            if (totalSwimlaneW > maxW) maxW = totalSwimlaneW
        }
        
        maxW += 40.0
        maxH += 40.0

        val svg = buildString {
            appendLine("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"$maxW\" height=\"$maxH\">")
            
            // Draw swimlane backgrounds / lines
            if (hasSwimlanes) {
                swimlanes.forEachIndexed { index, lane ->
                    val lx = index * swimlaneWidth
                    if (index > 0) {
                        appendLine("  <line x1=\"$lx\" y1=\"0\" x2=\"$lx\" y2=\"$maxH\" stroke=\"#cccccc\" stroke-width=\"2\" />")
                    }
                    appendLine("  <text x=\"${lx + swimlaneWidth/2}\" y=\"25\" font-family=\"sans-serif\" font-size=\"16\" font-weight=\"bold\" text-anchor=\"middle\" fill=\"#000000\">$lane</text>")
                }
            }

            // Draw partitions
            val partitions = nodeMap.values.mapNotNull { it.partition }.distinct()
            val partitionRects = mutableMapOf<String, String>()
            partitions.forEach { part ->
                val partNodes = nodeMap.values.filter { it.partition == part }
                if (partNodes.isNotEmpty()) {
                    var minX = Double.MAX_VALUE
                    var minY = Double.MAX_VALUE
                    var pMaxX = Double.MIN_VALUE
                    var pMaxY = Double.MIN_VALUE
                    partNodes.forEach { n ->
                        val p = coordinates[n.identifier]
                        if (p != null) {
                            if (p.first < minX) minX = p.first
                            if (p.second < minY) minY = p.second
                            if (p.first + nodeW > pMaxX) pMaxX = p.first + nodeW
                            if (p.second + nodeH > pMaxY) pMaxY = p.second + nodeH
                        }
                    }
                    val pad = 20.0
                    minX -= pad
                    minY -= pad * 2 
                    pMaxX += pad
                    pMaxY += pad
                    
                    if (pMaxX > maxW) maxW = pMaxX + pad
                    
                    val rectSvg = """
                        <rect x="$minX" y="$minY" width="${pMaxX-minX}" height="${pMaxY-minY}" fill="#f9f9f9" stroke="#999999" stroke-width="1.5" stroke-dasharray="8,8" rx="8" />
                        <text x="${minX + 10}" y="${minY + 20}" font-family="sans-serif" font-size="14" fill="#333333" font-weight="bold">$part</text>
                    """.trimIndent()
                    partitionRects[part] = rectSvg
                }
            }
            partitionRects.values.forEach { appendLine("  $it") }
            
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

                    val strokeColor = edge.color ?: "#333333"
                    val styleAttr = if (edge.style == "dashed") " stroke-dasharray=\"5,5\"" else ""

                    // Draw a simple path with a slight curve or straight line vertically
                    appendLine("    <path d=\"M $x1 $y1 C $x1 ${(y1+y2)/2}, $x2 ${(y1+y2)/2}, $x2 $y2\" stroke=\"$strokeColor\"$styleAttr fill=\"none\"/>")
                    
                    // Edge arrow head
                    if (edge.isArrow) {
                        appendLine("    <polygon points=\"${x2-4},${y2-6} ${x2+4},${y2-6} $x2,$y2\" fill=\"$strokeColor\" stroke=\"none\"/>")
                    }

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
                    val fillStr = node.fillColor.toString()
                    val fillHex = if(fillStr.startsWith("#")) fillStr else if (fillStr == "BLACK") "#333333" else "#f0f0f0"
                    val stroke = "#333333"

                    when (node.shape) {
                        NodeShape.CIRCLE -> {
                            appendLine("    <circle cx=\"$cx\" cy=\"$cy\" r=\"15\" fill=\"$fillHex\" stroke=\"$stroke\" stroke-width=\"2\"/>")
                            if (node.label != "(*)") {
                                appendLine("    <text x=\"$cx\" y=\"$cy\" dy=\"5\" fill=\"#000000\">${node.label}</text>")
                            }
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
                        NodeShape.POINT -> {
                            // Support label for POINT if it is an icon action
                            if (node.label.isNotBlank()) {
                                val textStr = node.label.replace("<", "&lt;").replace(">", "&gt;").replace("\n", "&#13;")
                                val lines = textStr.split("&#13;")
                                val provider = textToPathProvider
                                if (provider != null) {
                                    lines.forEachIndexed { i, line -> 
                                        val pathD = provider(line, cx, cy - ((lines.size-1)*6) + (i*14), 14)
                                        appendLine("    <path d=\"$pathD\" fill=\"#000000\" stroke=\"none\" />")
                                    }
                                } else {
                                    lines.forEachIndexed { i, line ->
                                        // Render emoji/icon with a larger font size if it's the only text
                                        val fontSize = if (lines.size == 1 && line.length <= 2) "24" else "14"
                                        appendLine("    <text x=\"$cx\" y=\"${cy - ((lines.size-1)*6) + (i*14)}\" dy=\"5\" font-size=\"$fontSize\" fill=\"#000000\">$line</text>")
                                    }
                                }
                            }
                        }
                        NodeShape.RECT -> {
                            if (node.label.isBlank() && node.fillColor.toString() == "BLACK") {
                                // Fork/join thick bar
                                appendLine("    <rect x=\"${p.first}\" y=\"${cy-3}\" width=\"$nodeW\" height=\"6\" fill=\"$fillHex\" />")
                            } else {
                                val rx = if (node.style.contains(NodeStyle.ROUNDED)) "10" else "0"
                                appendLine("    <rect x=\"${p.first}\" y=\"${p.second}\" width=\"$nodeW\" height=\"$nodeH\" rx=\"$rx\" ry=\"$rx\" fill=\"$fillHex\" stroke=\"$stroke\" stroke-width=\"1.5\"/>")
                                val textStr = node.label.replace("<", "&lt;").replace(">", "&gt;").replace("\n", "&#13;")
                                val lines = textStr.split("&#13;")
                                val provider = textToPathProvider
                                if (provider != null) {
                                    lines.forEachIndexed { i, line -> 
                                        val pathD = provider(line, cx, cy - ((lines.size-1)*6) + (i*14), 14)
                                        appendLine("    <path d=\"$pathD\" fill=\"#000000\" stroke=\"none\" />")
                                    }
                                } else {
                                    lines.forEachIndexed { i, line ->
                                        appendLine("    <text x=\"$cx\" y=\"${cy - ((lines.size-1)*6) + (i*14)}\" dy=\"5\" fill=\"#000000\">$line</text>")
                                    }
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

private data class Edge(val from: String, val to: String, val label: String, val isBackEdge: Boolean = false, val color: String? = null, val style: String? = null, val isArrow: Boolean = true)