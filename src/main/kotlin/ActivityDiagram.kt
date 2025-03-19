package org.example

import com.strumenta.antlrkotlin.parsers.generated.PlantUMLParser
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


enum class NodeShape {
    CIRCLE, DOUBLE_CIRCLE, RECT
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

class Node(
    val label: String,
    val shape: NodeShape,
    val style: List<NodeStyle>,
    val fillColor: NodeColor
)

//
data class Transition(
    val left: PlantUMLParser.Activity_stateContext?,
    val right: PlantUMLParser.Activity_stateContext?,
    val label: String?
)

class ActivityDiagram(activityDiagram: PlantUMLParser.Activity_diagramContext) : Diagram {
    val nodeMap = mutableMapOf<String, Node>(
        "START" to Node(
            "(*)", NodeShape.CIRCLE, listOf(NodeStyle.FILLED), fillColor = NodeColor.Const(
                ConstColor.BLACK
            )
        ),
        "END" to Node(
            "(*)",
            NodeShape.DOUBLE_CIRCLE,
            listOf(NodeStyle.FILLED),
            fillColor = NodeColor.Const(ConstColor.BLACK)
        )
    )

    val transitions: List<String>

    init {
        val topTransition = activityDiagram.activity_top_transition()
        val statements = mutableListOf(
            Transition(
                topTransition.activity_state(0), topTransition
                    .activity_state(1), topTransition.transition_label()?.text
            )
        )
        activityDiagram.activity_statement().forEachIndexed { i, it ->
            val activityTransition = it.activity_transition()
            statements.add(
                if (activityTransition.activity_state().size == 1) {
                    Transition(
                        statements[i].right,
                        activityTransition.activity_state(0),
                        activityTransition.transition_label()?.PARAGRAPH()?.text
                    )
                } else {
                    Transition(
                        activityTransition.activity_state(0),
                        it.activity_transition().activity_state(1),
                        activityTransition.transition_label()?.PARAGRAPH()?.text
                    )
                }
            )
        }
        transitions = statements.map { (leftState, rightState, label) ->
            val leftName = leftState?.identifier()?.let {
                addCustomNode(it.text, nodeMap).label
            } ?: "START"
            val rightName = rightState?.identifier()?.let {
                addCustomNode(it.text, nodeMap).label
            } ?: "END"
            val extra = buildString {
                append("arrowhead=vee")
                if (!label.isNullOrBlank()) {
                    append(" label=$label")
                }
            }
            "$leftName -> $rightName [$extra];"
        }


    }
}


@OptIn(ExperimentalUuidApi::class)
private fun addCustomNode(
    originLabel: String,
    m: MutableMap<String, Node>
): Node {
    val key = if (originLabel == "START" || originLabel == "END") {
        Uuid.random().toString()
    } else {
        originLabel
    }

    return m.getOrPut(key) {
        Node(
            originLabel,
            NodeShape.RECT,
            listOf(NodeStyle.FILLED, NodeStyle.ROUNDED),
            fillColor = NodeColor.Const(
                ConstColor.GRAY
            )
        )
    }
}