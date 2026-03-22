package org.example

import kotlin.test.Test

class ActivityDiagramTest {
    @Test
    fun testParse() {
        val input = """@startuml
start
:Hello world;
if (condition) then (yes)
  #red:Some activity;
else (no)
  -[#green,dashed]->
  :Other activity;
endif
--
stop
@enduml""".trim()
        println("TRANSITIONS START")
        val diagram = parsePlantUML(input) as ActivityDiagram
        diagram.transitions.forEach { println(it) }
        println("TRANSITIONS END")
        val svg = diagram.toSvg()
        println("SVG OUTPUT START")
        println(svg)
        println("SVG OUTPUT END")
        kotlin.test.assertTrue(svg.contains("#red"), "SVG should contain #red")
        kotlin.test.assertTrue(svg.contains("#green"), "SVG should contain #green")
        kotlin.test.assertTrue(svg.contains("stroke-dasharray=\"5,5\""), "SVG should contain dashed stroke")
    }

    @Test
    fun testConnectors() {
        val input = """@startuml
start
:Step 1;
(A)
(A) #blue
:Step 2;
stop
@enduml""".trim()
        val diagram = parsePlantUML(input) as ActivityDiagram
        val svg = diagram.toSvg()
        println("TEST CONNECTORS SVG:\\n$svg")
        kotlin.test.assertTrue(svg.contains(">A</text>") || svg.contains("fill=\"#000000\" stroke=\"none\""), "SVG should contain connector A")
        kotlin.test.assertTrue(svg.contains("fill=\"#blue\""), "SVG should contain blue connector")
    }

    @Test
    fun testPartition() {
        val input = """@startuml
start
partition "My Group" {
  :Group Action;
}
stop
@enduml""".trim()
        val diagram = parsePlantUML(input) as ActivityDiagram
        val svg = diagram.toSvg()
        kotlin.test.assertTrue(svg.contains("My Group"), "SVG should contain the partition label")
    }

    @Test
    fun testSwimlanes() {
        val input = """@startuml
|Lane 1|
start
:Action 1;
|Lane 2|
:Action 2;
stop
@enduml""".trim()
        val diagram = parsePlantUML(input) as ActivityDiagram
        val svg = diagram.toSvg()
        println("TEST SWIMLANES SVG:\\n$svg")
        kotlin.test.assertTrue(svg.contains("Lane1"), "SVG should contain Lane 1")
        kotlin.test.assertTrue(svg.contains("Lane2"), "SVG should contain Lane 2")
    }

    @Test
    fun testStereotype() {
        val input = """@startuml
start
<<+icon+>> :Action with emoji;
stop
@enduml""".trim()
        val diagram = parsePlantUML(input) as ActivityDiagram
        val svg = diagram.toSvg()
        println("TEST STEREOTYPE SVG:\\n$svg")
        kotlin.test.assertTrue(svg.contains("Actionwithemoji"), "SVG should contain the action label")
        kotlin.test.assertFalse(svg.contains("rx=\"10\" ry=\"10\" fill=\"#f0f0f0\"") && svg.substringAfter("Actionwithemoji").isEmpty(), "Should not draw a typical rectangle box for icon")
    }

    @Test
    fun testUserEmojiSnippet() {
        val input = """@startuml
while (<:cloud_with_rain:>)
  :<:umbrella:>; <<icon>>
endwhile
-<<icon>><:closed_umbrella:>
@enduml""".trim()
        val diagram = parsePlantUML(input) as ActivityDiagram
        val svg = diagram.toSvg()
        println("TEST USER EMOJI SVG:\\n$svg")
    }
}
