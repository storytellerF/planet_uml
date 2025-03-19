package org.example

import com.strumenta.antlrkotlin.parsers.generated.PlantUMLLexer
import com.strumenta.antlrkotlin.parsers.generated.PlantUMLParser
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import kotlin.uuid.ExperimentalUuidApi

fun main() {
    parsePlantUML(
        """@startuml
(*) --> [You] "First Activity"
--> (*)
@enduml""".trim()
    )
}

//
@OptIn(ExperimentalUuidApi::class)
fun parsePlantUML(plantUMLContent: String) {
    val inputStream = CharStreams.fromString(plantUMLContent)
    val lexer = PlantUMLLexer(inputStream)
    val tokens = CommonTokenStream(lexer)
    val parser = PlantUMLParser(tokens)

    val diagramContext = parser.plantuml().diagram()?.activity_diagram() ?: return
    val activityDiagram = ActivityDiagram(diagramContext)
    println(buildString {
        append("digraph ")
        appendScope {
            buildStringInScope {
                activityDiagram.nodeMap.map { (k, v) ->
                    "${k}[shape=${v.shape.name.lowercase().replace("_", "")} style=\"${v.style.joinToString(",") { it.name.lowercase() }}\" fillcolor=${v.fillColor}]"
                }
            } + activityDiagram.transitions
        }
    })
}

fun StringBuilder.appendScope(block: () -> List<String>) {
    append("{\n")
    block().map {
        appendLine("    $it")
    }
    append("}\n")
}

fun buildStringInScope(block: () -> List<String>): List<String> {
    return buildList<String> {
        add("{")
        block().map {
            add("    $it")
        }
        add("}")
    }
}
