package org.storyteller_f.planet_uml

object ClassSamples {
    val samples = listOf(
        "Declarative element" to """
@startuml
hide empty members
abstract        abstract
abstract class  "abstract class"
annotation      annotation
circle          circle
()              circle_short_form
class           class
class           class_stereo  <<stereotype>>
dataclass       dataclass
diamond         diamond
<>              diamond_short_form
entity          entity
enum            enum
exception       exception
interface       interface
metaclass       metaclass
protocol        protocol
record          record
stereotype      stereotype
struct          struct
@enduml
""".trimIndent(),
        "Relations between classes" to """
@startuml
Class01 <|-- Class02
Class03 *-- Class04
Class05 o-- Class06
Class07 .. Class08
Class09 -- Class10
@enduml
""".trimIndent(),
        "Relations between classes 2" to """
@startuml
Class11 <|.. Class12
Class13 --> Class14
Class15 ..> Class16
Class17 ..|> Class18
Class19 <--* Class20
@enduml
""".trimIndent(),
        "Relations between classes 3" to """
@startuml
Class21 #-- Class22
Class23 x-- Class24
Class25 }-- Class26
Class27 +-- Class28
Class29 ^-- Class30
@enduml
""".trimIndent(),
        "Label on relations" to """
@startuml

Class01 "1" *-- "many" Class02 : contains

Class03 o-- Class04 : aggregation

Class05 --> "1" Class06

@enduml
""".trimIndent(),
        "Label on relations 2" to """
@startuml
class Car

Driver - Car : drives >
Car *- Wheel : have 4 >
Car -- Person : < owns

@enduml
""".trimIndent(),
        "Using non-letters in element names and relation labels" to """
@startuml
class "This is my class" as class1
class class2 as "It works this way too"

class2 *-- "foo/dummy" : use
@enduml
""".trimIndent(),
        "Starting names with $" to """
@startuml
class ${'$'}C1
class ${'$'}C2 ${'$'}C2
class "${'$'}C2" as dollarC2
remove ${'$'}C1
remove ${'$'}C2
remove dollarC2
@enduml
""".trimIndent(),
        "Adding methods" to """
@startuml
Object <|-- ArrayList

Object : equals()
ArrayList : Object[] elementData
ArrayList : size()

@enduml
""".trimIndent(),
        "Adding methods 2" to """
@startuml
class Dummy {
  String data
  void methods()
}

class Flight {
   flightNumber : Integer
   departureTime : Date
}
@enduml
""".trimIndent(),
        "Adding methods 3" to """
@startuml
class Dummy {
  {field} A field (despite parentheses)
  {method} Some method
}

@enduml
""".trimIndent(),
        "Visibility for methods or fields" to """
@startuml

class Dummy {
 -field1
 #field2
 ~method1()
 +method2()
}

@enduml
""".trimIndent(),
        "Visibility for methods or fields 2" to """
@startuml
skinparam classAttributeIconSize 0
class Dummy {
 -field1
 #field2
 ~method1()
 +method2()
}

@enduml
""".trimIndent(),
        "Visibility for methods or fields 3" to """
@startuml
class Dummy {
 field1
 field2
 method1()
 method2()
}

@enduml
""".trimIndent(),
        "Visibility for methods or fields 4" to """
@startuml
class Dummy {
 field1
 \~Dummy()
 method1()
}

@enduml
""".trimIndent(),
        "Visibility for class" to """
@startuml
-class "private Class" {
}

#class "protected Class" {
}

~class "package private Class" {
}

+class "public Class" {
}
@enduml
""".trimIndent(),
        "Visibility on compositions and aggregations" to """
@startuml
class Aaa {
    -bbb : int
    +ccc : string
    #aa : float
    +void addEntry(mmm : Entry)
    +int setFactory(ddd : string)
}
class Factory {
    #fff : string
}
class Entry {
    -yyy : int
}
class Parent {
}
Aaa *--> "1..100" Entry : -entries
Aaa o--> Factory : #factory
Aaa o--> Parent : +parent
@enduml
""".trimIndent(),
        "Abstract and Static" to """
@startuml
class Dummy {
  {static} String id
  {abstract} void methods()
}
@enduml
""".trimIndent(),
        "Advanced class body" to """
@startuml
class Foo1 {
  You can use
  several lines
  ..
  as you want
  and group
  ==
  things together.
  __
  You can have as many groups
  as you want
  --
  End of class
}

class User {
  .. Simple Getter ..
  + getName()
  + getAddress()
  .. Some setter ..
  + setName()
  __ private data __
  int age
  -- encrypted --
  String password
}

@enduml
""".trimIndent(),
        "Notes and stereotypes" to """
@startuml
class Object << general >>
Object <|--- ArrayList

note top of Object : In java, every class\nextends this one.

note "This is a floating note" as N1
note "This note is connected\nto several objects." as N2
Object .. N2
N2 .. ArrayList

class Foo
note left: On last defined class

@enduml
""".trimIndent(),
        "More on notes" to """
@startuml

class Foo
note left: On last defined class

note top of Foo
  In java, <size:18>every</size> <u>class</u>
  <b>extends</b>
  <i>this</i> one.
end note

note as N1
  This note is <u>also</u>
  <b><color:royalBlue>on several</color>
  <s>words</s> lines
  And this is hosted by <img:https://plantuml.com/sourceforge.jpg>
end note

@enduml
""".trimIndent(),
        "Note on field or method" to """
@startuml
class A {
{static} int counter
+void {abstract} start(int timeout)
}
note right of A::counter
  This member is annotated
end note
note right of A::start
  This method is now explained in a UML note
end note
@enduml
""".trimIndent(),
        "Note on method with the same name" to """
@startuml
class A {
{static} int counter
+void {abstract} start(int timeoutms)
+void {abstract} start(Duration timeout)
}
note left of A::counter
  This member is annotated
end note
note right of A::"start(int timeoutms)"
  This method with int
end note
note right of A::"start(Duration timeout)"
  This method with Duration
end note
@enduml
""".trimIndent(),
        "Note on links" to """
@startuml

class Dummy
Dummy --> Foo : A link
note on link #red: note that is red

Dummy --> Foo2 : Another link
note right on link #blue
this is my note on right link
and in blue
end note

@enduml
""".trimIndent(),
        "Abstract class and interface" to """
@startuml

abstract class AbstractList
abstract AbstractCollection
interface List
interface Collection

List <|-- AbstractList
Collection <|-- AbstractCollection

Collection <|- List
AbstractCollection <|- AbstractList
AbstractList <|-- ArrayList

class ArrayList {
  Object[] elementData
  size()
}

enum TimeUnit {
  DAYS
  HOURS
  MINUTES
}

annotation SuppressWarnings

annotation Annotation {
  annotation with members
  String foo()
  String bar()
}


@enduml
""".trimIndent(),
        "Hide attributes, methods..." to """
@startuml

class Dummy1 {
  +myMethods()
}

class Dummy2 {
  +hiddenMethod()
}

class Dummy3 <<Serializable>> {
String name
}

hide members
hide <<Serializable>> circle
show Dummy1 methods
show <<Serializable>> fields

@enduml
""".trimIndent(),
        "Hide attributes, methods... 2" to """
@startuml
hide private members
hide protected members
hide package members

class Foo {
  - private
  # protected
  ~ package
}
@enduml
""".trimIndent(),
        "Hide classes" to """
@startuml

class Foo1
class Foo2

Foo2 *-- Foo1

hide Foo2

@enduml
""".trimIndent(),
        "Remove classes" to """
@startuml

class Foo1
class Foo2

Foo2 *-- Foo1

remove Foo2

@enduml
""".trimIndent(),
        "Hide, Remove or Restore tagged element or wildcard" to """
@startuml
class C1 ${'$'}tag13
enum E1
interface I1 ${'$'}tag13
C1 -- I1
@enduml
""".trimIndent(),
        "Hide, Remove or Restore tagged element or wildcard 2" to """
@startuml
class C1 ${'$'}tag13
enum E1
interface I1 ${'$'}tag13
C1 -- I1

hide ${'$'}tag13
@enduml
""".trimIndent(),
        "Hide, Remove or Restore tagged element or wildcard 3" to """
@startuml
class C1 ${'$'}tag13
enum E1
interface I1 ${'$'}tag13
C1 -- I1

remove ${'$'}tag13
@enduml
""".trimIndent(),
        "Hide, Remove or Restore tagged element or wildcard 4" to """
@startuml
class C1 ${'$'}tag13 ${'$'}tag1
enum E1
interface I1 ${'$'}tag13
C1 -- I1

remove ${'$'}tag13
restore ${'$'}tag1
@enduml
""".trimIndent(),
        "Hide, Remove or Restore tagged element or wildcard 5" to """
@startuml
class C1 ${'$'}tag13 ${'$'}tag1
enum E1
interface I1 ${'$'}tag13
C1 -- I1

remove *
restore ${'$'}tag1
@enduml
""".trimIndent(),
        "Hide or Remove unlinked class" to """
@startuml
class C1
class C2
class C3
C1 -- C2
@enduml
""".trimIndent(),
        "Hide or Remove unlinked class 2" to """
@startuml
class C1
class C2
class C3
C1 -- C2

hide @unlinked
@enduml
""".trimIndent(),
        "Hide or Remove unlinked class 3" to """
@startuml
class C1
class C2
class C3
C1 -- C2

remove @unlinked
@enduml
""".trimIndent(),
        "Use generics" to """
@startuml

class Foo<? extends Element> {
  int size()
}
Foo *- Element

@enduml
""".trimIndent(),
        "Specific Spot" to """
@startuml

class System << (S,#FF7700) Singleton >>
class Date << (D,orchid) >>
@enduml
""".trimIndent(),
        "Packages" to """
@startuml

package "Classic Collections" #DDDDDD {
  Object <|-- ArrayList
}

package com.plantuml {
  Object <|-- Demo1
  Demo1 *- Demo2
}

@enduml
""".trimIndent(),
        "Packages style" to """
@startuml
scale 750 width
package foo1 <<Node>> {
  class Class1
}

package foo2 <<Rectangle>> {
  class Class2
}

package foo3 <<Folder>> {
  class Class3
}

package foo4 <<Frame>> {
  class Class4
}

package foo5 <<Cloud>> {
  class Class5
}

package foo6 <<Database>> {
  class Class6
}

@enduml
""".trimIndent(),
        "Packages style 2" to """
@startuml

skinparam packageStyle rectangle

package foo1.foo2 {
}

package foo1.foo2.foo3 {
  class Object
}

foo1.foo2 +-- foo1.foo2.foo3

@enduml
""".trimIndent(),
        "Automatic package creation" to """
@startuml

set separator ::
class X1::X2::foo {
  some info
}

@enduml
""".trimIndent(),
        "Automatic package creation 2" to """
@startuml

set separator none
class X1.X2.foo {
  some info
}

@enduml
""".trimIndent(),
        "Lollipop interface" to """
@startuml
class foo
bar ()- foo
@enduml
""".trimIndent(),
        "Changing arrows orientation" to """
@startuml
Room o- Student
Room *-- Chair
@enduml
""".trimIndent(),
        "Changing arrows orientation 2" to """
@startuml
Student -o Room
Chair --* Room
@enduml
""".trimIndent(),
        "Changing arrows orientation 3" to """
@startuml
foo -left-> dummyLeft
foo -right-> dummyRight
foo -up-> dummyUp
foo -down-> dummyDown
@enduml
""".trimIndent(),
        "Changing arrows orientation 4" to """
@startuml
left to right direction
foo -left-> dummyLeft
foo -right-> dummyRight
foo -up-> dummyUp
foo -down-> dummyDown
@enduml
""".trimIndent(),
        "Association classes" to """
@startuml
class Student {
  Name
}
Student "0..*" - "1..*" Course
(Student, Course) .. Enrollment

class Enrollment {
  drop()
  cancel()
}
@enduml
""".trimIndent(),
        "Association classes 2" to """
@startuml
class Student {
  Name
}
Student "0..*" -- "1..*" Course
(Student, Course) . Enrollment

class Enrollment {
  drop()
  cancel()
}
@enduml
""".trimIndent(),
        "Association on same class" to """
@startuml
class Station {
    +name: string
}

class StationCrossing {
    +cost: TimeInterval
}

<> diamond

StationCrossing . diamond
diamond - "from 0..*" Station
diamond - "to 0..* " Station
@enduml
""".trimIndent(),
        "Skinparam" to """
@startuml

skinparam class {
BackgroundColor PaleGreen
ArrowColor SeaGreen
BorderColor SpringGreen
}
skinparam stereotypeCBackgroundColor YellowGreen

Class01 "1" *-- "many" Class02 : contains

Class03 o-- Class04 : aggregation

@enduml
""".trimIndent(),
        "Skinned Stereotypes" to """
@startuml

skinparam class {
BackgroundColor PaleGreen
ArrowColor SeaGreen
BorderColor SpringGreen
BackgroundColor<<Foo>> Wheat
BorderColor<<Foo>> Tomato
}
skinparam stereotypeCBackgroundColor YellowGreen
skinparam stereotypeCBackgroundColor<< Foo >> DimGray

class Class01 <<Foo>>
class Class03 <<Foo>>
Class01 "1" *-- "many" Class02 : contains

Class03 o-- Class04 : aggregation

@enduml
""".trimIndent(),
        "Color gradient" to """
@startuml

skinparam backgroundcolor AntiqueWhite/Gold
skinparam classBackgroundColor Wheat|CornflowerBlue

class Foo #red-green
note left of Foo #blue\9932CC
  this is my
  note on this class
end note

package example #GreenYellow/LightGoldenRodYellow {
  class Dummy
}

@enduml
""".trimIndent(),
        "Help on layout" to """
@startuml

class Bar1
class Bar2
together {
  class Together1
  class Together2
  class Together3
}
Together1 - Together2
Together2 - Together3
Together2 -[hidden]--> Bar1
Bar1 -[hidden]> Bar2


@enduml
""".trimIndent(),
        "Splitting large files" to """
@startuml
' Split into 4 pages
page 2x2
skinparam pageMargin 10
skinparam pageExternalColor gray
skinparam pageBorderColor black

class BaseClass

namespace net.dummy #DDDDDD {
    .BaseClass <|-- Person
    Meeting o-- Person

    .BaseClass <|- Meeting

}

namespace net.foo {
  net.dummy.Person  <|- Person
  .BaseClass <|-- Person

  net.dummy.Meeting o-- Person
}

BaseClass <|-- net.unused.Person
@enduml
""".trimIndent(),
        "Extends and implements" to """
@startuml
class ArrayList implements List
class ArrayList extends AbstractList
@enduml
""".trimIndent(),
        "Extends and implements 2" to """
@startuml
class A extends B, C {
}
@enduml
""".trimIndent(),
        "Line style" to """
@startuml
title Bracketed line style without label
class foo
class bar
bar1 : [bold]  
bar2 : [dashed]
bar3 : [dotted]
bar4 : [hidden]
bar5 : [plain] 

foo --> bar
foo -[bold]-> bar1
foo -[dashed]-> bar2
foo -[dotted]-> bar3
foo -[hidden]-> bar4
foo -[plain]-> bar5
@enduml
""".trimIndent(),
        "Line style 2" to """
@startuml
title Bracketed line style with label
class foo
class bar
bar1 : [bold]  
bar2 : [dashed]
bar3 : [dotted]
bar4 : [hidden]
bar5 : [plain] 

foo --> bar          : ∅
foo -[bold]-> bar1   : [bold]
foo -[dashed]-> bar2 : [dashed]
foo -[dotted]-> bar3 : [dotted]
foo -[hidden]-> bar4 : [hidden]
foo -[plain]-> bar5  : [plain]

@enduml
""".trimIndent(),
        "Line color" to """
@startuml
title Bracketed line color
class foo
class bar
bar1 : [#red]
bar2 : [#green]
bar3 : [#blue]

foo --> bar
foo -[#red]-> bar1     : [#red]
foo -[#green]-> bar2   : [#green]
foo -[#blue]-> bar3    : [#blue]
'foo -[#blue;#yellow;#green]-> bar4
@enduml
""".trimIndent(),
        "Line thickness" to """
@startuml
title Bracketed line thickness
class foo
class bar
bar1 : [thickness=1]
bar2 : [thickness=2]
bar3 : [thickness=4]
bar4 : [thickness=8]
bar5 : [thickness=16]

foo --> bar                 : ∅
foo -[thickness=1]-> bar1   : [1]
foo -[thickness=2]-> bar2   : [2]
foo -[thickness=4]-> bar3   : [4]
foo -[thickness=8]-> bar4   : [8]
foo -[thickness=16]-> bar5  : [16]

@enduml
""".trimIndent(),
        "Mix" to """
@startuml
title Bracketed line style mix
class foo
class bar
bar1 : [#red,thickness=1]
bar2 : [#red,dashed,thickness=2]
bar3 : [#green,dashed,thickness=4]
bar4 : [#blue,dotted,thickness=8]
bar5 : [#blue,plain,thickness=16]

foo --> bar                             : ∅
foo -[#red,thickness=1]-> bar1          : [#red,1]
foo -[#red,dashed,thickness=2]-> bar2   : [#red,dashed,2]
foo -[#green,dashed,thickness=4]-> bar3 : [#green,dashed,4]
foo -[#blue,dotted,thickness=8]-> bar4  : [blue,dotted,8]
foo -[#blue,plain,thickness=16]-> bar5  : [blue,plain,16]
@enduml
""".trimIndent(),
        "Change relation (linking or arrow) color and style (inline style)" to """
@startuml
class foo
foo --> bar : normal
foo --> bar1 #line:red;line.bold;text:red  : red bold
foo --> bar2 #green;line.dashed;text:green : green dashed
foo --> bar3 #blue;line.dotted;text:blue   : blue dotted
@enduml
""".trimIndent(),
        "Change class color and style (inline style)" to """
@startuml
abstract   abstract
annotation annotation #pink ##[bold]red
class      class      #palegreen ##[dashed]green
interface  interface  #aliceblue ##[dotted]blue
@enduml
""".trimIndent(),
        "Change class color and style (inline style) 2" to """
@startuml
abstract   abstract
annotation annotation #pink;line:red;line.bold;text:red
class      class      #palegreen;line:green;line.dashed;text:green
interface  interface  #aliceblue;line:blue;line.dotted;text:blue
@enduml
""".trimIndent(),
        "Change class color and style (inline style) 3" to """
@startuml
class bar #line:green;back:lightblue
class bar2 #lightblue;line:green

class Foo1 #back:red;line:00FFFF
class FooDashed #line.dashed:blue
class FooDotted #line.dotted:blue
class FooBold #line.bold
class Demo1 #back:lightgreen|yellow;header:blue/red
@enduml
""".trimIndent(),
        "Arrows from/to class members" to """
@startuml
class Foo {
+ field1
+ field2
}

class Bar {
+ field3
+ field4
}

Foo::field1 --> Bar::field3 : foo
Foo::field2 --> Bar::field4 : bar
@enduml
""".trimIndent(),
        "Arrows from/to class members 2" to """
@startuml
left to right direction

class User {
  id : INTEGER
  ..
  other_id : INTEGER
}

class Email {
  id : INTEGER
  ..
  user_id : INTEGER
  address : INTEGER
}

User::id *-- Email::user_id
@enduml
""".trimIndent(),
        "GroupInheritance 1 (no grouping)" to """
@startuml
skinparam groupInheritance 1

A1 <|-- B1

A2 <|-- B2
A2 <|-- C2

A3 <|-- B3
A3 <|-- C3
A3 <|-- D3

A4 <|-- B4
A4 <|-- C4
A4 <|-- D4
A4 <|-- E4
@enduml
""".trimIndent(),
        "GroupInheritance 2 (grouping from 2)" to """
@startuml
skinparam groupInheritance 2

A1 <|-- B1

A2 <|-- B2
A2 <|-- C2

A3 <|-- B3
A3 <|-- C3
A3 <|-- D3

A4 <|-- B4
A4 <|-- C4
A4 <|-- D4
A4 <|-- E4
@enduml
""".trimIndent(),
        "GroupInheritance 3 (grouping only from 3)" to """
@startuml
skinparam groupInheritance 3

A1 <|-- B1

A2 <|-- B2
A2 <|-- C2

A3 <|-- B3
A3 <|-- C3
A3 <|-- D3

A4 <|-- B4
A4 <|-- C4
A4 <|-- D4
A4 <|-- E4
@enduml
""".trimIndent(),
        "GroupInheritance 4 (grouping only from 4)" to """
@startuml
skinparam groupInheritance 4

A1 <|-- B1

A2 <|-- B2
A2 <|-- C2

A3 <|-- B3
A3 <|-- C3
A3 <|-- D3

A4 <|-- B4
A4 <|-- C4
A4 <|-- D4
A4 <|-- E4
@enduml
""".trimIndent(),
        "Simple example" to """
@startuml
class Class
object Object
json JSON {
   "fruit":"Apple",
   "size":"Large",
   "color": ["Red", "Green"]
}
@enduml
""".trimIndent(),
        "Packages and Namespaces Enhancement" to """
@startuml
class A.B.C.D.Z {
}
@enduml
""".trimIndent(),
        "Packages and Namespaces Enhancement 2" to """
@startuml
set separator none
class A.B.C.D.Z {
}
@enduml
""".trimIndent(),
        "Packages and Namespaces Enhancement 3" to """
@startuml
!pragma useIntermediatePackages false
class A.B.C.D.Z {
}
@enduml
""".trimIndent(),
        "Packages and Namespaces Enhancement 4" to """
@startuml
set separator none
package A.B.C.D {
  class Z {
  }
}
@enduml
""".trimIndent(),
        "Minimal example" to """
@startuml
class class1
class class2

class1 [Qualifier] - class2
@enduml
""".trimIndent(),
        "Another example" to """
@startuml
    interface Map<K,V>
    class HashMap<Long,Customer>

    Map <|.. HashMap
    Shop [customerId: long] ---> "customer\n1" Customer
    HashMap [id: Long] -r-> "value" Customer
@enduml
""".trimIndent(),
        "With Graphviz (layout engine by default)" to """
@startuml
class a
class b
package A {
  class a1
  class a2
  class a3
  class a4
  class a5
  package sub_a {
   class sa1
   class sa2
   class sa3
  }
}
  
package B {
  class b1
  class b2
  class b3
  class b4
  class b5
  package sub_b {
   class sb1
   class sb2
   class sb3
  }
}
@enduml
""".trimIndent(),
        "With Smetana (internal layout engine)" to """
@startuml
!pragma layout smetana
class a
class b
package A {
  class a1
  class a2
  class a3
  class a4
  class a5
  package sub_a {
   class sa1
   class sa2
   class sa3
  }
}
  
package B {
  class b1
  class b2
  class b3
  class b4
  class b5
  package sub_b {
   class sb1
   class sb2
   class sb3
  }
}
@enduml
""".trimIndent(),
        "With Graphviz (layout engine by default) 2" to """
@startuml
left to right direction
class a
class b
package A {
  class a1
  class a2
  class a3
  class a4
  class a5
  package sub_a {
   class sa1
   class sa2
   class sa3
  }
}
  
package B {
  class b1
  class b2
  class b3
  class b4
  class b5
  package sub_b {
   class sb1
   class sb2
   class sb3
  }
}
@enduml
""".trimIndent(),
        "With Smetana (internal layout engine) 2" to """
@startuml
!pragma layout smetana
left to right direction
class a
class b
package A {
  class a1
  class a2
  class a3
  class a4
  class a5
  package sub_a {
   class sa1
   class sa2
   class sa3
  }
}
  
package B {
  class b1
  class b2
  class b3
  class b4
  class b5
  package sub_b {
   class sb1
   class sb2
   class sb3
  }
}
@enduml
""".trimIndent(),
        "Role label to associations" to """
@startuml
class User
class Item
User "owner which is very long"/1 -- "0..n"/items Item
@enduml
""".trimIndent(),
    )
}
