package org.example

import kotlin.test.Test

class ActivityDiagramTest {
    @Test
    fun testParse() {
        val input = """@startuml
start
:Hello world;
if (condition) then (yes)
  :Some activity;
else (no)
  :Other activity;
endif
stop
@enduml""".trim()
        
        parsePlantUML(input)
    }
}
