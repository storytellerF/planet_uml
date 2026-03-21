package org.example

import com.strumenta.antlrkotlin.parsers.generated.PlantUMLParser
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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

    private fun addEdge(from: Node, to: Node, label: String? = null) {
        val extra = buildString {
            append("arrowhead=vee")
            label?.takeIf { it.isNotBlank() }?.let {
                append(" label=\"$it\"")
            }
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
            stmt.conditional() != null -> {
                val condCtx = stmt.conditional()!!
                // Condition node
                val condLabel = condCtx.paragraph_text(0)?.text?.trim() ?: ""
                val diamond = createNode(condLabel, NodeShape.DIAMOND, emptyList(), ConstColor.GRAY)
                tails.forEach { addEdge(it, diamond) }

                // The 'then' block
                val thenLabel = if (condCtx.paragraph_text().size > 1) condCtx.paragraph_text(1)?.text?.trim() else null
                var thenTails = listOf(diamond)
                var statementsStartIndex = 0
                val thenStatements = mutableListOf<PlantUMLParser.StatementContext>()
                // Extract statements until ELSE or ENDIF (we can just filter out based on parse tree order but better is using statement lists if ANTLR supports statement(i) grouped)
                // Oh wait, conditional has statement* twice. ANTLR groups them into a single list statement()
                // It's safer to just rely on the count... actually this is tricky in Kotlin ANTLR.
                // A better way is to iterate children.
                var isElse = false
                val thenStmts = mutableListOf<PlantUMLParser.StatementContext>()
                val elseStmts = mutableListOf<PlantUMLParser.StatementContext>()
                for (child in condCtx.children!!) {
                    if (child.text == "else") {
                        isElse = true
                    } else if (child is PlantUMLParser.StatementContext) {
                        if (isElse) elseStmts.add(child) else thenStmts.add(child)
                    }
                }
                
                // process then
                // For the very first node in then block, its incoming edge from diamond needs the 'thenLabel'
                var thenCurrentTails = listOf(diamond)
                val thenFirstStmts = thenStmts.take(1)
                
                if (thenStmts.isEmpty()) {
                    // empty block, but need to attach edge if needed? But if empty, tails is just diamond
                } else {
                    val firstNode = processStatement(thenStmts.first(), emptyList())
                    // Connect diamond to first node of then
                    firstNode.forEach { addEdge(diamond, it, thenLabel ?: "yes") }
                    thenCurrentTails = firstNode
                    for (i in 1 until thenStmts.size) {
                        thenCurrentTails = processStatement(thenStmts[i], thenCurrentTails)
                    }
                }

                // process else
                var elseCurrentTails = listOf(diamond)
                val elseLabel = if (condCtx.ELSE() != null && condCtx.paragraph_text().size > 2) condCtx.paragraph_text(2)?.text?.trim() else null
                
                if (elseStmts.isEmpty()) {
                     // Wait, if there's no else branch, what's the exit? 
                     // The exit is from diamond straight to next node.
                     // We can represent this by returning diamond for the else branch!
                     // But with edge label `elseLabel` or "no". This is tricky since we must add that edge later.
                     // To solve this, we can insert a dummy NO-OP node or just let the caller add edge.
                     // Actually, if we return diamond, the caller will add edge from diamond to NEXT node.
                     // But graphviz needs edge label on that connection!
                     // This means our output tail should probably carry the out-edge label.
                     // To keep it simple, we can insert an invisible intermediate node or just let it fly without label.
                }

                if (elseStmts.isNotEmpty()) {
                    val firstNode = processStatement(elseStmts.first(), emptyList())
                    firstNode.forEach { addEdge(diamond, it, elseLabel ?: "no") }
                    elseCurrentTails = firstNode
                    for (i in 1 until elseStmts.size) {
                        elseCurrentTails = processStatement(elseStmts[i], elseCurrentTails)
                    }
                }

                // If else is missing completely, the "else" flow comes from the diamond directly.
                val branchesOut = mutableListOf<Node>()
                branchesOut.addAll(thenCurrentTails)
                if (elseStmts.isNotEmpty()) branchesOut.addAll(elseCurrentTails) else branchesOut.add(diamond)
                return branchesOut
            }
            stmt.keyword_stmt() != null -> {
                val kw = stmt.keyword_stmt()!!.text.trim()
                return if (kw == "start") {
                    val node = createNode("(*)", NodeShape.CIRCLE, listOf(NodeStyle.FILLED), ConstColor.BLACK)
                    tails.forEach { addEdge(it, node) }
                    listOf(node)
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
                loopTails.forEach { addEdge(it, diamond) }
                
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
                 addEdge(diamond, entry, isLabel)
                 
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
}