package org.example

import com.strumenta.antlrkotlin.parsers.generated.PlantUMLLexer
import com.strumenta.antlrkotlin.parsers.generated.PlantUMLParser
import com.strumenta.antlrkotlin.runtime.BitSet
import org.antlr.v4.kotlinruntime.ANTLRErrorListener
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import org.antlr.v4.kotlinruntime.Parser
import org.antlr.v4.kotlinruntime.RecognitionException
import org.antlr.v4.kotlinruntime.Recognizer
import org.antlr.v4.kotlinruntime.atn.ATNConfigSet
import org.antlr.v4.kotlinruntime.dfa.DFA
import kotlin.uuid.ExperimentalUuidApi

fun main() {
    parsePlantUML(
        """@startuml
start
:Hello world;
if (condition) then (yes)
  :Some activity;
else (no)
  :Other activity;
endif
stop
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

    parser.addErrorListener(object : ANTLRErrorListener {
        override fun reportAmbiguity(
            recognizer: Parser,
            dfa: DFA,
            startIndex: Int,
            stopIndex: Int,
            exact: Boolean,
            ambigAlts: BitSet,
            configs: ATNConfigSet
        ) {
            println("ambiguity $startIndex $stopIndex $exact $ambigAlts")
        }

        override fun reportAttemptingFullContext(
            recognizer: Parser,
            dfa: DFA,
            startIndex: Int,
            stopIndex: Int,
            conflictingAlts: BitSet,
            configs: ATNConfigSet
        ) {
            println("attempting $startIndex $stopIndex $conflictingAlts")
        }

        override fun reportContextSensitivity(
            recognizer: Parser,
            dfa: DFA,
            startIndex: Int,
            stopIndex: Int,
            prediction: Int,
            configs: ATNConfigSet
        ) {
            println("sensitivity $startIndex $stopIndex $prediction")
        }

        override fun syntaxError(
            recognizer: Recognizer<*, *>,
            offendingSymbol: Any?,
            line: Int,
            charPositionInLine: Int,
            msg: String,
            e: RecognitionException?
        ) {
            println("syntax error $offendingSymbol $line $charPositionInLine $msg $e")
        }

    })
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
    println(parser.numberOfSyntaxErrors)
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
