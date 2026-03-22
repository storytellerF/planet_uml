package org.example

class PlanetUml {
    lateinit var diagram: Diagram
}

interface Diagram {
    fun toSvg(): String
}