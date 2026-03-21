package org.storyteller_f.planet_uml

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.awt.Font
import java.awt.font.FontRenderContext
import java.awt.geom.PathIterator

fun buildPathFromText(text: String, x: Double, y: Double, size: Int): String {
    val font = Font("SansSerif", Font.PLAIN, size)
    val frc = FontRenderContext(null, true, true)
    val gv = font.createGlyphVector(frc, text)
    // To align it similarly to text-anchor="middle" dominant-baseline="middle" (approximately)
    val bounds = gv.visualBounds
    val startX = x - bounds.width / 2.0 - bounds.x
    val startY = y - bounds.height / 2.0 - bounds.y 
    val shape = gv.getOutline(startX.toFloat(), startY.toFloat())
    val pi = shape.getPathIterator(null)
    val coords = FloatArray(6)
    val sb = StringBuilder()
    while (!pi.isDone) {
        when (pi.currentSegment(coords)) {
            PathIterator.SEG_MOVETO -> sb.append("M ").append(coords[0]).append(" ").append(coords[1]).append(" ")
            PathIterator.SEG_LINETO -> sb.append("L ").append(coords[0]).append(" ").append(coords[1]).append(" ")
            PathIterator.SEG_QUADTO -> sb.append("Q ").append(coords[0]).append(" ").append(coords[1]).append(" ").append(coords[2]).append(" ").append(coords[3]).append(" ")
            PathIterator.SEG_CUBICTO -> sb.append("C ").append(coords[0]).append(" ").append(coords[1]).append(" ").append(coords[2]).append(" ").append(coords[3]).append(" ").append(coords[4]).append(" ").append(coords[5]).append(" ")
            PathIterator.SEG_CLOSE -> sb.append("Z ")
        }
        pi.next()
    }
    return sb.toString()
}

fun main() {
    org.example.textToPathProvider = ::buildPathFromText
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "planet_uml",
        ) {
            App()
        }
    }
}