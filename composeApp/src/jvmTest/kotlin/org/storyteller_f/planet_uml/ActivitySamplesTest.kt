package org.storyteller_f.planet_uml

import org.example.parsePlantUML
import org.example.ActivityDiagram
import kotlin.test.Test
import java.io.File
import kotlin.test.assertTrue

class ActivitySamplesTest {

    @Test
    fun testAllActivitySamples() {
        val rootDir = File(System.getProperty("user.dir")).let {
            if (it.name == "composeApp") it.parentFile else it
        }
        val targetsDir = File(rootDir, "targets/activity")
        
        var successCount = 0
        var failCount = 0
        val failedSamples = mutableListOf<String>()

        ActivitySamples.samples.forEach { (name, code) ->
            val safeName = name.replace(Regex("[^a-zA-Z0-9.-]"), "_")
            val officialSvgFile = File(targetsDir, "${safeName}_official.svg")
            
            val officialSvg = if (officialSvgFile.exists()) officialSvgFile.readText() else ""
            
            try {
                val diagram = parsePlantUML(code) as? ActivityDiagram
                assertTrue(diagram != null, "Sample '${name}' should be parsed as an ActivityDiagram")
                
                val generatedSvg = diagram.toSvg()
                assertTrue(generatedSvg.isNotBlank(), "Sample '${name}' SVG must not be blank")

                val generatedSvgFile = File(targetsDir, "${safeName}_generated.svg")
                generatedSvgFile.writeText(generatedSvg)

                if (officialSvg.isNotBlank()) {
                    assertTrue(officialSvg.contains("<svg"), "Official SVG for '${name}' must be valid SVG")
                    assertTrue(generatedSvg.contains("<svg"), "Generated SVG for '${name}' must be valid SVG")
                }
                
                successCount++
            } catch (e: Throwable) {
                // We record the failure but continue testing other samples
                println("FAILED sample '${name}': ${e.message}")
                failCount++
                failedSamples.add(name)
            }
        }
        
        println("Generated all SVGs. Success: ${successCount}, Failed: ${failCount}")
        if (failCount > 0) {
            println("Failed samples: ${failedSamples.joinToString(", ")}")
        }
        
        // Assert that all samples succeed or at least check they are processed.
        // We will assert no failures so the CI would know if something broke.
        assertTrue(failCount == 0, "There were ${failCount} failed Activity samples")
    }
}
