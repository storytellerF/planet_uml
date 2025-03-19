package org.example

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

class ActivityDiagram(val activityDiagram: PlantUMLParser.Activity_diagramContext) : Diagram {
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
        val statements = mutableListOf(
            activityDiagram.activity_top_transition().activity_state(0) to activityDiagram.activity_top_transition()
                .activity_state(1)
        )
        activityDiagram.activity_statement().forEachIndexed { i, it ->
            val activityTransition = it.activity_transition()
            statements.add(
                if (activityTransition.activity_state().size == 1) {
                    null to activityTransition.activity_state(0)
                } else {
                    activityTransition.activity_state(0) to it.activity_transition().activity_state(1)
                }
            )
        }
        transitions = statements.map { (leftState, rightState) ->
            val rightName = rightState.identifier()?.let {
                addCustomNode(it.text, nodeMap).label
            } ?: "END"
            if (leftState != null) {
                val leftName = leftState.identifier()?.let {
                    addCustomNode(it.text, nodeMap).label
                } ?: "START"
                "$leftName -> $rightName"
            } else {
                "-> $rightName"
            }
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