package org.storyteller_f.planet_uml

object ComponentSamples {
    val samples = listOf(
        "Components" to """
@startuml

[First component]
[Another component] as Comp2
component Comp3
component [Last\ncomponent] as Comp4

@enduml
""".trimIndent(),
        "Naming exceptions" to """
@startuml
component [${'$'}C1]
component [${'$'}C2] ${'$'}C2
component [${'$'}C2] as dollarC2
remove ${'$'}C1
remove ${'$'}C2
remove dollarC2
@enduml
""".trimIndent(),
        "Interfaces" to """
@startuml

() "First Interface"
() "Another interface" as Interf2
interface Interf3
interface "Last\ninterface" as Interf4

[component]
footer //Adding "component" to force diagram to be a **component diagram**//
@enduml
""".trimIndent(),
        "Basic example" to """
@startuml

DataAccess - [First Component]
[First Component] ..> HTTP : use

@enduml
""".trimIndent(),
        "Using notes" to """
@startuml
[Component] as C

note top of C: A top note

note bottom of C
  A bottom note can also
  be on several lines
end note

note left of C
  A left note can also
  be on several lines
end note

note right of C: A right note
@enduml
""".trimIndent(),
        "Using notes 2" to """
@startuml
[Component] as C

note as N
  A floating note can also
  be on several lines
end note

C .. N
@enduml
""".trimIndent(),
        "Using notes 3" to """
@startuml

interface "Data Access" as DA

DA - [First Component]
[First Component] ..> HTTP : use

note left of HTTP : Web Service only

note right of [First Component]
  A note can also
  be on several lines
end note

@enduml
""".trimIndent(),
        "Grouping Components" to """
@startuml

package "Some Group" {
  HTTP - [First Component]
  [Another Component]
}

node "Other Groups" {
  FTP - [Second Component]
  [First Component] --> FTP
}

cloud {
  [Example 1]
}


database "MySql" {
  folder "This is my folder" {
    [Folder 3]
  }
  frame "Foo" {
    [Frame 4]
  }
}


[Another Component] --> [Example 1]
[Example 1] --> [Folder 3]
[Folder 3] --> [Frame 4]

@enduml
""".trimIndent(),
        "Changing arrows direction" to """
@startuml
[Component] --> Interface1
[Component] -> Interface2
@enduml
""".trimIndent(),
        "Changing arrows direction 2" to """
@startuml
Interface1 <-- [Component]
Interface2 <- [Component]
@enduml
""".trimIndent(),
        "Changing arrows direction 3" to """
@startuml
[Component] -left-> left
[Component] -right-> right
[Component] -up-> up
[Component] -down-> down
@enduml
""".trimIndent(),
        "Changing arrows direction 4" to """
@startuml
left to right direction
[Component] -left-> left
[Component] -right-> right
[Component] -up-> up
[Component] -down-> down
@enduml
""".trimIndent(),
        "Use UML2 notation" to """
@startuml

interface "Data Access" as DA

DA - [First Component]
[First Component] ..> HTTP : use

@enduml
""".trimIndent(),
        "Use UML1 notation" to """
@startuml
skinparam componentStyle uml1

interface "Data Access" as DA

DA - [First Component]
[First Component] ..> HTTP : use

@enduml
""".trimIndent(),
        "Use rectangle notation (remove UML notation)" to """
@startuml
skinparam componentStyle rectangle

interface "Data Access" as DA

DA - [First Component]
[First Component] ..> HTTP : use

@enduml
""".trimIndent(),
        "Long description" to """
@startuml
component comp1 [
This component
has a long comment
on several lines
]
@enduml
""".trimIndent(),
        "Individual colors" to """
@startuml
component  [Web Server] #Yellow
@enduml
""".trimIndent(),
        "Using Sprite in Stereotype" to """
@startuml
sprite ${'$'}businessProcess [16x16/16] {
FFFFFFFFFFFFFFFF
FFFFFFFFFFFFFFFF
FFFFFFFFFFFFFFFF
FFFFFFFFFFFFFFFF
FFFFFFFFFF0FFFFF
FFFFFFFFFF00FFFF
FF00000000000FFF
FF000000000000FF
FF00000000000FFF
FFFFFFFFFF00FFFF
FFFFFFFFFF0FFFFF
FFFFFFFFFFFFFFFF
FFFFFFFFFFFFFFFF
FFFFFFFFFFFFFFFF
FFFFFFFFFFFFFFFF
FFFFFFFFFFFFFFFF
}


rectangle " End to End\nbusiness process" <<${'$'}businessProcess>> {
 rectangle "inner process 1" <<${'$'}businessProcess>> as src
 rectangle "inner process 2" <<${'$'}businessProcess>> as tgt
 src -> tgt
}
@enduml
""".trimIndent(),
        "Skinparam" to """
@startuml

skinparam interface {
  backgroundColor RosyBrown
  borderColor orange
}

skinparam component {
  FontSize 13
  BackgroundColor<<Apache>> Pink
  BorderColor<<Apache>> #FF6655
  FontName Courier
  BorderColor black
  BackgroundColor gold
  ArrowFontName Impact
  ArrowColor #FF6655
  ArrowFontColor #777777
}

() "Data Access" as DA
Component "Web Server" as WS << Apache >>

DA - [First Component]
[First Component] ..> () HTTP : use
HTTP - WS

@enduml
""".trimIndent(),
        "Skinparam 2" to """
@startuml

skinparam component {
  backgroundColor<<static lib>> DarkKhaki
  backgroundColor<<shared lib>> Green
}

skinparam node {
  borderColor Green
  backgroundColor Yellow
  backgroundColor<<shared_node>> Magenta
}
skinparam databaseBackgroundColor Aqua

[AA] <<static lib>>
[BB] <<shared lib>>
[CC] <<static lib>>

node node1
node node2 <<shared_node>>
database Production

@enduml
""".trimIndent(),
        "componentStyle" to """
@startuml
skinparam BackgroundColor transparent
skinparam componentStyle uml2
component A {
   component "A.1" {
}
   component A.44 {
      [A4.1]
}
   component "A.2"
   [A.3]
   component A.5 [
A.5] 
   component A.6 [
]
}
[a]->[b]
@enduml
""".trimIndent(),
        "componentStyle 2" to """
@startuml
skinparam BackgroundColor transparent
skinparam componentStyle rectangle
component A {
   component "A.1" {
}
   component A.44 {
      [A4.1]
}
   component "A.2"
   [A.3]
   component A.5 [
A.5] 
   component A.6 [
]
}
[a]->[b]
@enduml
""".trimIndent(),
        "Hide or Remove unlinked component" to """
@startuml
component C1
component C2
component C3
C1 -- C2
@enduml
""".trimIndent(),
        "Hide or Remove unlinked component 2" to """
@startuml
component C1
component C2
component C3
C1 -- C2

hide @unlinked
@enduml
""".trimIndent(),
        "Hide or Remove unlinked component 3" to """
@startuml
component C1
component C2
component C3
C1 -- C2

remove @unlinked
@enduml
""".trimIndent(),
        "Hide, Remove or Restore tagged component or wildcard" to """
@startuml
component C1 ${'$'}tag13
component C2
component C3 ${'$'}tag13
C1 -- C2
@enduml
""".trimIndent(),
        "Hide, Remove or Restore tagged component or wildcard 2" to """
@startuml
component C1 ${'$'}tag13
component C2
component C3 ${'$'}tag13
C1 -- C2

hide ${'$'}tag13
@enduml
""".trimIndent(),
        "Hide, Remove or Restore tagged component or wildcard 3" to """
@startuml
component C1 ${'$'}tag13
component C2
component C3 ${'$'}tag13
C1 -- C2

remove ${'$'}tag13
@enduml
""".trimIndent(),
        "Hide, Remove or Restore tagged component or wildcard 4" to """
@startuml
component C1 ${'$'}tag13 ${'$'}tag1
component C2
component C3 ${'$'}tag13
C1 -- C2

remove ${'$'}tag13
restore ${'$'}tag1
@enduml
""".trimIndent(),
        "Hide, Remove or Restore tagged component or wildcard 5" to """
@startuml
component C1 ${'$'}tag13 ${'$'}tag1
component C2
component C3 ${'$'}tag13
C1 -- C2

remove *
restore ${'$'}tag1
@enduml
""".trimIndent(),
        "Simple example" to """
@startuml
allowmixing

component Component
()        Interface

json JSON {
   "fruit":"Apple",
   "size":"Large",
   "color": ["Red", "Green"]
}
@enduml
""".trimIndent(),
        "Port" to """
@startuml
[c]
component C {
  port p1
  port p2
  port p3
  component c1
}

c --> p1
c --> p2
c --> p3
p1 --> c1
p2 --> c1
@enduml
""".trimIndent(),
        "PortIn" to """
@startuml
[c]
component C {
  portin p1
  portin p2
  portin p3
  component c1
}

c --> p1
c --> p2
c --> p3
p1 --> c1
p2 --> c1
@enduml
""".trimIndent(),
        "PortOut" to """
@startuml
component C {
  portout p1
  portout p2
  portout p3
  component c1
}
[o]
p1 --> o
p2 --> o
p3 --> o
c1 --> p1
@enduml
""".trimIndent(),
        "Mixing PortIn & PortOut" to """
@startuml
[i]
component C {
  portin p1
  portin p2
  portin p3
  portout po1
  portout po2
  portout po3
  component c1
}
[o]

i --> p1
i --> p2
i --> p3
p1 --> c1
p2 --> c1
po1 --> o
po2 --> o
po3 --> o
c1 --> po1
@enduml
""".trimIndent(),
    )
}
