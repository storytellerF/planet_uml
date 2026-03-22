package org.storyteller_f.planet_uml

import org.jetbrains.skia.*
import org.jetbrains.skia.svg.SVGDOM
import kotlin.test.Test
import kotlin.test.assertTrue

class SkiaTest {
    @Test
    fun testSkia() {
        val data = Data.makeFromBytes("<svg></svg>".toByteArray())
        val dom = SVGDOM(data)
        assertTrue(dom != null)
    }
}
