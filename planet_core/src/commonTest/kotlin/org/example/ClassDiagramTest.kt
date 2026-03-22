package org.example

import kotlin.test.Test
import kotlin.test.assertTrue

class ClassDiagramTest {

    @Test
    fun testBasicClassAndRelation() {
        val input = """@startuml
class User {
  id: Int
  name: String
}
interface Auth
User -- Auth : authenticates
@enduml""".trimIndent()
        
        val diagram = parsePlantUML(input) as? ClassDiagram
        assertTrue(diagram != null, "Diagram should be parsed as ClassDiagram")
        
        val svg = diagram.toSvg()
        println("CLASS DIAGRAM SVG:\n${svg}")
        
        assertTrue(svg.contains("User"), "SVG should contain User class")
        assertTrue(svg.contains("Auth"), "SVG should contain Auth interface")
        assertTrue(svg.contains("#B4A7E5"), "SVG should contain interface circle color")
        assertTrue(svg.contains("authenticates"), "SVG should contain relation label")
    }

    @Test
    fun testInheritanceAndComposition() {
        val input = """@startuml
abstract class Person
class Admin
Person <|-- Admin
Building *-- Room
@enduml""".trimIndent()
        
        val diagram = parsePlantUML(input) as? ClassDiagram
        assertTrue(diagram != null, "Diagram should be parsed as ClassDiagram")
        
        val svg = diagram.toSvg()
        println("INHERITANCE SVG:\n${svg}")
        
        assertTrue(svg.contains("Person"), "SVG should contain Person class")
        assertTrue(svg.contains("Admin"), "SVG should contain Admin class")
        assertTrue(svg.contains("Building"), "SVG should contain Building class")
        assertTrue(svg.contains("Room"), "SVG should contain Room class")
        assertTrue(svg.contains("#A9DCDF"), "SVG should contain abstract circle color")
    }

    @Test
    fun testArrowOrientation() {
        val input = """@startuml
class A
class B
A -right-> B
@enduml""".trimIndent()
        
        val diagram = parsePlantUML(input) as? ClassDiagram
        kotlin.test.assertTrue(diagram != null, "Diagram should be parsed")
        val rel = diagram.relations.firstOrNull()
        kotlin.test.assertEquals("-right->", rel?.arrow)
    }
}
