package org.example

import kotlin.test.Test
import kotlin.test.assertTrue
import java.io.File

class RenderedImageTest {

    @Test
    fun testCompareGeneratedImageAndOfficial() {
        val rootDir = File(System.getProperty("user.dir")).let {
            if (it.name == "planet_core") it.parentFile else it
        }
        val targetsDir = File(rootDir, "targets")
        
        // Read official SVG
        val officialSvgFile = File(targetsDir, "sample.svg")
        assertTrue(officialSvgFile.exists(), "Official SVG file should exist at ${officialSvgFile.absolutePath}")
        val officialSvg = officialSvgFile.readText()

        val input = """@startuml
class A
class B
A -right-> B
@enduml"""
        
        // Generate our SVG
        val diagram = parsePlantUML(input) as? ClassDiagram
        assertTrue(diagram != null, "Diagram should be parsed as ClassDiagram")
        
        val generatedSvg = diagram.toSvg()
        
        // Save our SVG for manual inspection
        val generatedSvgFile = File(targetsDir, "project_generated.svg")
        generatedSvgFile.writeText(generatedSvg)
        
        // Basic comparison
        assertTrue(officialSvg.contains("A"), "Official SVG should contain class A name")
        assertTrue(officialSvg.contains("B"), "Official SVG should contain class B name")
        
        assertTrue(generatedSvg.contains("A"), "Generated SVG should contain class A name")
        assertTrue(generatedSvg.contains("B"), "Generated SVG should contain class B name")
        
        println("Generated SVG saved to ${generatedSvgFile.absolutePath}")
        println("Official SVG is at ${officialSvgFile.absolutePath}")
        
    }
}
