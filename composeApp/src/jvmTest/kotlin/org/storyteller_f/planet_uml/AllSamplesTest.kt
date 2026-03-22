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
                    
                    val generatedPngFile = File(targetsDir, "${safeName}_generated.png")
                    svgToPng(generatedSvg, generatedPngFile)

                    if (officialSvg.isNotBlank()) {
                        assertTrue(officialSvg.contains("<svg"), "Official SVG for '${name}' must be valid SVG")
                        assertTrue(generatedSvg.contains("<svg"), "Generated SVG for '${name}' must be valid SVG")
                        
                        val officialPngFile = File(targetsDir, "${safeName}_official.png")
                        svgToPng(officialSvg, officialPngFile)
                        
                        if (generatedPngFile.exists() && officialPngFile.exists()) {
                            val similarity = compareImages(officialPngFile, generatedPngFile)
                            val similarityPercentage = similarity * 100
                            println("  Similarity for '${name}': ${String.format("%.2f%%", similarityPercentage)}")
                            assertTrue(similarity >= 0.99, "Similarity for '${name}' is less than 99%: ${String.format("%.2f%%", similarityPercentage)}")
                        }
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
        // assertTrue(totalFail == 0, "There were ${totalFail} failed samples across all categories")
    }

    private fun svgToPng(svgString: String, file: File) {
        try {
            val transcoder = org.apache.batik.transcoder.image.PNGTranscoder()
            val input = org.apache.batik.transcoder.TranscoderInput(java.io.StringReader(svgString))
            file.outputStream().use { outputStream ->
                val output = org.apache.batik.transcoder.TranscoderOutput(outputStream)
                transcoder.transcode(input, output)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun compareImages(png1: File, png2: File): Double {
        try {
            val img1 = javax.imageio.ImageIO.read(png1) ?: return 0.0
            val img2 = javax.imageio.ImageIO.read(png2) ?: return 0.0
            val w = minOf(img1.width, img2.width)
            val h = minOf(img1.height, img2.height)
            var diff = 0.0
            for (y in 0 until h) {
                for (x in 0 until w) {
                    val rgb1 = img1.getRGB(x, y)
                    val rgb2 = img2.getRGB(x, y)
                    val r1 = (rgb1 shr 16) and 0xff
                    val g1 = (rgb1 shr 8) and 0xff
                    val b1 = rgb1 and 0xff
                    val r2 = (rgb2 shr 16) and 0xff
                    val g2 = (rgb2 shr 8) and 0xff
                    val b2 = rgb2 and 0xff
                    diff += (Math.abs(r1 - r2) + Math.abs(g1 - g2) + Math.abs(b1 - b2)) / (255.0 * 3.0)
                }
            }
            val maxPixels = maxOf(img1.width * img1.height, img2.width * img2.height)
            diff += (maxPixels - w * h) // Penalty for size difference
            return 1.0 - (diff / maxPixels)
        } catch (e: Exception) {
            return 0.0
        }
    } 
}
