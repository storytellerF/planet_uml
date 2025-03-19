package org.example

import PlantUMLLexer
import PlantUMLParser
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import kotlin.uuid.ExperimentalUuidApi

fun main() {
    println("Hello World!")
    parsePlantUML(
        """@startuml
(*) --> "First Activity"
--> (*)
@enduml""".trim()
    )
}


@OptIn(ExperimentalUuidApi::class)
fun parsePlantUML(plantUMLContent: String) {
    val inputStream = ANTLRInputStream(plantUMLContent)
    val lexer = PlantUMLLexer(inputStream)
    val tokens = CommonTokenStream(lexer)
    val parser = PlantUMLParser(tokens)

    val activityDiagram = ActivityDiagram(parser.plantuml().diagram().activity_diagram())
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
