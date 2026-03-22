package org.storyteller_f.planet_uml

import org.example.parsePlantUML
import org.example.Diagram
import kotlin.test.Test
import java.io.File
import kotlin.test.assertTrue

class AllSamplesTest {

    @Test
    fun testAllDiagramSamples() {
        val rootDir = File(System.getProperty("user.dir")).let {
            if (it.name == "composeApp") it.parentFile else it
        }
        
        var totalSuccess = 0
        var totalFail = 0
        val failedSamples = mutableListOf<String>()

        AllSamples.categories.forEach { (categoryName, samples) ->
            // E.g. "Activity Diagrams" -> "activity", "Class Diagrams" -> "class"
            val categoryFolder = categoryName.split(" ").first().lowercase()
            val targetsDir = File(rootDir, "targets/${categoryFolder}")
            targetsDir.mkdirs()

            var categorySuccess = 0
            var categoryFail = 0

            println("Testing category: ${categoryName}")

            samples.forEach { (name, code) ->
                val safeName = name.replace(Regex("[^a-zA-Z0-9.-]"), "_")
                val officialSvgFile = File(targetsDir, "${safeName}_official.svg")
                
                val officialSvg = if (officialSvgFile.exists()) officialSvgFile.readText() else ""
                
                try {
                    val diagram = parsePlantUML(code) as? Diagram
                    assertTrue(diagram != null, "Sample '${name}' in ${categoryName} could not be parsed as Diagram")
                    
                    val generatedSvg = diagram.toSvg()
                    assertTrue(generatedSvg.isNotBlank(), "Sample '${name}' SVG must not be blank")

                    val generatedSvgFile = File(targetsDir, "${safeName}_generated.svg")
                    generatedSvgFile.writeText(generatedSvg)

                    if (officialSvg.isNotBlank()) {
                        assertTrue(officialSvg.contains("<svg"), "Official SVG for '${name}' must be valid SVG")
                        assertTrue(generatedSvg.contains("<svg"), "Generated SVG for '${name}' must be valid SVG")
                    }
                    
                    categorySuccess++
                    totalSuccess++
                } catch (e: Throwable) {
                    categoryFail++
                    totalFail++
                    failedSamples.add("${categoryName} - ${name}")
                }
            }
            println("  -> Success: $categorySuccess, Failed: $categoryFail")
        }
        
        println("\nCompleted Testing All Samples.")
        println("Overall Success: $totalSuccess, Overall Failed: $totalFail")
        if (totalFail > 0) {
            println("Failed samples count: ${failedSamples.size}. (Not listing all to avoid console spam)")
        }
        
        // Assert that all samples succeed or at least check they are processed.
        assertTrue(totalFail == 0, "There were ${totalFail} failed samples across all categories")
    }
}
