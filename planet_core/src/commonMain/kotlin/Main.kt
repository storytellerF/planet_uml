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

//
@OptIn(ExperimentalUuidApi::class)
fun parsePlantUML(plantUMLContent: String): ActivityDiagram? {
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
    val diagramContext = parser.plantuml().diagram()?.activity_diagram() ?: return null
    return ActivityDiagram(diagramContext)
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

fun main() {
    val src = """
        @startuml
        start
        fork
          :Parallel 1;
        fork again
          :Parallel 2;
        end merge
        split
          :Split 1;
        split again
          :Split 2;
        end split
        repeat
          :Looping;
          if (break?) then (yes)
             break
          endif
          :More looping;
        repeat while (more?)
        label my_label
        :After label;
        if (goto label?) then (yes)
          goto my_label
        endif
        stop
        @enduml
    """.trimIndent()
    val diagram = parsePlantUML(src)
    if (diagram != null) {
        println("Transitions:")
        diagram.transitions.forEach { println(it) }
        println("\nSVG:\n${diagram.toSvg()}")
    } else {
        println("Failed to parse diagram")
    }
}
