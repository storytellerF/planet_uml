package org.storyteller_f.planet_uml

object ActivitySamples {
    val samples = listOf(
        "Simple action" to """
@startuml
:Hello world;
:This is defined on
several **lines**;
@enduml
""".trimIndent(),
        "Simple action list separated by -" to """
@startuml
- Action 1
- Action 2
- Action 3
@enduml
""".trimIndent(),
        "With one level" to """
@startuml
* Action 1
* Action 2
* Action 3
@enduml
""".trimIndent(),
        "With several levels" to """
@startuml
<style>
element {MinimumWidth 150}
</style>
* Action 1
** Sub-Action 1.1
** Sub-Action 1.2
*** Sub-Action 1.2.1
*** Sub-Action 1.2.2
* Action 2
@enduml
""".trimIndent(),
        "Start/Stop/End" to """
@startuml
start
:Hello world;
:This is defined on
several **lines**;
stop
@enduml
""".trimIndent(),
        "Start/Stop/End 2" to """
@startuml
start
:Hello world;
:This is defined on
several **lines**;
end
@enduml
""".trimIndent(),
        "Conditional [if, then, else, endif]" to """
@startuml

start

if (Graphviz installed?) then (yes)
  :process all\ndiagrams;
else (no)
  :process only
  __sequence__ and __activity__ diagrams;
endif

stop

@enduml
""".trimIndent(),
        "Conditional [if, then, else, endif] 2" to """
@startuml
if (color?) is (<color:red>red) then
:print red;
else 
:print not red;
endif
@enduml
""".trimIndent(),
        "Conditional [if, then, else, endif] 3" to """
@startuml
if (counter?) equals (5) then
:print 5;
else 
:print not 5;
endif
@enduml
""".trimIndent(),
        "Several tests (horizontal mode)" to """
@startuml
start
if (condition A) then (yes)
  :Text 1;
elseif (condition B) then (yes)
  :Text 2;
  stop
(no) elseif (condition C) then (yes)
  :Text 3;
(no) elseif (condition D) then (yes)
  :Text 4;
else (nothing)
  :Text else;
endif
stop
@enduml
""".trimIndent(),
        "Several tests (vertical mode)" to """
@startuml
!pragma useVerticalIf on
start
if (condition A) then (yes)
  :Text 1;
elseif (condition B) then (yes)
  :Text 2;
  stop
elseif (condition C) then (yes)
  :Text 3;
elseif (condition D) then (yes)
  :Text 4;
else (nothing)
  :Text else;
endif
stop
@enduml
""".trimIndent(),
        "Switch and case [switch, case, endswitch]" to """
@startuml
start
switch (test?)
case ( condition A )
  :Text 1;
case ( condition B ) 
  :Text 2;
case ( condition C )
  :Text 3;
case ( condition D )
  :Text 4;
case ( condition E )
  :Text 5;
endswitch
stop
@enduml
""".trimIndent(),
        "Conditional with stop on an action [kill, detach]" to """
@startuml
if (condition?) then
  :error;
  stop
endif
:action; <<#palegreen>>
@enduml
""".trimIndent(),
        "Conditional with stop on an action [kill, detach] 2" to """
@startuml
if (condition?) then
  :error; <<#pink>>
  kill
endif
:action; <<#palegreen>>
@enduml
""".trimIndent(),
        "Conditional with stop on an action [kill, detach] 3" to """
@startuml
if (condition?) then
  :error; <<#pink>>
  detach
endif
:action; <<#palegreen>>
@enduml
""".trimIndent(),
        "Simple repeat loop" to """
@startuml

start

repeat
  :read data;
  :generate diagrams;
repeat while (more data?) is (yes) not (no)

stop

@enduml
""".trimIndent(),
        "Repeat loop with repeat action and backward action" to """
@startuml

start

repeat :foo as starting label;
  :read data;
  :generate diagrams;
backward:This is backward;
repeat while (more data?) is (yes)
->no;

stop

@enduml
""".trimIndent(),
        "Break on a repeat loop [break]" to """
@startuml
start
repeat
  :Test something;
    if (Something went wrong?) then (no)
      :OK; <<#palegreen>>
      break
    endif
    ->NOK;
    :Alert "Error with long text";
repeat while (Something went wrong with long text?) is (yes) not (no)
->//merged step//;
:Alert "Success";
stop
@enduml
""".trimIndent(),
        "Goto and Label Processing [label, goto]" to """
@startuml
title Point two queries to same activity\nwith `goto`
start
if (Test Question?) then (yes)
'space label only for alignment
label sp_lab0
label sp_lab1
'real label
label lab
:shared;
else (no)
if (Second Test Question?) then (yes)
label sp_lab2
goto sp_lab1
else
:nonShared;
endif
endif
:merge;
@enduml
""".trimIndent(),
        "Simple while loop" to """
@startuml

start

while (data available?)
  :read data;
  :generate diagrams;
endwhile

stop

@enduml
""".trimIndent(),
        "Simple while loop 2" to """
@startuml
while (check filesize ?) is (not empty)
  :read file;
endwhile (empty)
:close file;
@enduml
""".trimIndent(),
        "While loop with backward action" to """
@startuml
while (check filesize ?) is (not empty)
  :read file;
  backward:log;
endwhile (empty)
:close file;
@enduml
""".trimIndent(),
        "Infinite while loop" to """
@startuml
:Step 1;
if (condition1) then
  while (loop forever)
   :Step 2;
  endwhile
  -[hidden]->
  detach
else
  :end normally;
  stop
endif
@enduml
""".trimIndent(),
        "Simple fork" to """
@startuml
start
fork
  :action 1;
fork again
  :action 2;
end fork
stop
@enduml
""".trimIndent(),
        "fork with end merge" to """
@startuml
start
fork
  :action 1;
fork again
  :action 2;
end merge
stop
@enduml
""".trimIndent(),
        "fork with end merge 2" to """
@startuml
start
fork
  :action 1;
fork again
  :action 2;
fork again
  :action 3;
fork again
  :action 4;
end merge
stop
@enduml
""".trimIndent(),
        "fork with end merge 3" to """
@startuml
start
fork
  :action 1;
fork again
  :action 2;
  end
end merge
stop
@enduml
""".trimIndent(),
        "Label on end fork (or UML joinspec):" to """
@startuml
start
fork
  :action A;
fork again
  :action B;
end fork {or}
stop
@enduml
""".trimIndent(),
        "Label on end fork (or UML joinspec): 2" to """
@startuml
start
fork
  :action A;
fork again
  :action B;
end fork {and}
stop
@enduml
""".trimIndent(),
        "Other example" to """
@startuml

start

if (multiprocessor?) then (yes)
  fork
    :Treatment 1;
  fork again
    :Treatment 2;
  end fork
else (monoproc)
  :Treatment 1;
  :Treatment 2;
endif

@enduml
""".trimIndent(),
        "Split" to """
@startuml
start
split
   :A;
split again
   :B;
split again
   :C;
split again
   :a;
   :b;
end split
:D;
end
@enduml
""".trimIndent(),
        "Input split (multi-start)" to """
@startuml
split
   -[hidden]->
   :A;
split again
   -[hidden]->
   :B;
split again
   -[hidden]->
   :C;
end split
:D;
@enduml
""".trimIndent(),
        "Input split (multi-start) 2" to """
@startuml
split
   -[hidden]->
   :A;
split again
   -[hidden]->
   :a;
   :b;
split again
   -[hidden]->
   (Z)
end split
:D;
@enduml
""".trimIndent(),
        "Output split (multi-end)" to """
@startuml
start
split
   :A;
   kill
split again
   :B;
   detach
split again
   :C;
   kill
end split
@enduml
""".trimIndent(),
        "Output split (multi-end) 2" to """
@startuml
start
split
   :A;
   kill
split again
   :b;
   :c;
   detach
split again
   (Z)
   detach
split again
   end
split again
   stop
end split
@enduml
""".trimIndent(),
        "Notes" to """
@startuml

start
:foo1;
floating note left: This is a note
:foo2;
note right
  This note is on several
  //lines// and can
  contain <b>HTML</b>
  ====
  * Calling the method ""foo()"" is prohibited
end note
stop

@enduml
""".trimIndent(),
        "Notes 2" to """
@startuml
start
repeat :Enter data;
:Submit;
backward :Warning;
note right: Note
repeat while (Valid?) is (No) not (Yes)
stop
@enduml
""".trimIndent(),
        "Notes 3" to """
@startuml
start
partition "**process** HelloWorld" {
    note
        This is my note
        ----
        //Creole test//
    end note
    :Ready;
    :HelloWorld(i); <<output>>
    :Hello-Sent;
}
@enduml
""".trimIndent(),
        "Colors" to """
@startuml

start
:starting progress;
:reading configuration files
These files should be edited at this point!; <<#HotPink>>
:ending of the process; <<#AAAAAA>>

@enduml
""".trimIndent(),
        "Colors 2" to """
@startuml
start
partition #red/white testPartition {
        :testActivity; <<#blue\green>>
}
@enduml
""".trimIndent(),
        "Lines without arrows" to """
@startuml
skinparam ArrowHeadColor none
start
:Hello world;
:This is on defined on
several **lines**;
stop
@enduml
""".trimIndent(),
        "Lines without arrows 2" to """
@startuml
skinparam ArrowHeadColor none
start
repeat :Enter data;
:Submit;
backward :Warning;
repeat while (Valid?) is (No) not (Yes)
stop
@enduml
""".trimIndent(),
        "Arrows" to """
@startuml
:foo1;
-> You can put text on arrows;
if (test) then
  -[#blue]->
  :foo2;
  -[#green,dashed]-> The text can
  also be on several lines
  and **very** long...;
  :foo3;
else
  -[#black,dotted]->
  :foo4;
endif
-[#gray,bold]->
:foo5;
@enduml
""".trimIndent(),
        "Simple colored arrow [link]" to """
@startuml
:a;
link #blue
:b;
@enduml
""".trimIndent(),
        "Multiple colored arrow" to """
@startuml
skinparam colorArrowSeparationSpace 1
start
-[#red;#green;#orange;#blue]->
if(a?)then(yes)
-[#red]->
:activity;
-[#red]->
if(c?)then(yes)
-[#maroon,dashed]->
else(no)
-[#red]->
if(b?)then(yes)
-[#maroon,dashed]->
else(no)
-[#blue,dashed;dotted]->
:do a;
-[#red]->
:do b;
-[#red]->
endif
-[#red;#maroon,dashed]->
endif
-[#red;#maroon,dashed]->
elseif(e?)then(yes)
-[#green]->
if(c?)then(yes)
-[#maroon,dashed]->
else(no)
-[#green]->
if(d?)then(yes)
-[#maroon,dashed]->
else(no)
-[#green]->
:do something; <<continuous>>
-[#green]->
endif
-[#green;#maroon,dashed]->
partition dummy {
:some function;
}
-[#green;#maroon,dashed]->
endif
-[#green;#maroon,dashed]->

elseif(f?)then(yes)
-[#orange]->
:activity; <<continuous>>
-[#orange]->
else(no)
-[#blue,dashed;dotted]->
endif
stop
@enduml
""".trimIndent(),
        "Connector (or Circle)" to """
@startuml
start
:Some activity;
(A)
detach
(A)
:Other activity;
@enduml
""".trimIndent(),
        "Color on connector" to """
@startuml
start
:The connector below
wishes he was blue;
#blue:(B)
:This next connector
feels that she would
be better off green;
#green:(G)
stop
@enduml
""".trimIndent(),
        "Color on connector 2" to """
@startuml
<style>
circle {
  Backgroundcolor palegreen
  LineColor green
  LineThickness 2
}
</style>

(1)
:a;
(A)
@enduml
""".trimIndent(),
        "Group" to """
@startuml
start
group Initialization {
    :read config file;
    :init internal variable;
}
group Running group {
    :wait for user interaction;
    :print information;
}

stop
@enduml
""".trimIndent(),
        "Partition" to """
@startuml
start
partition Initialization {
    :read config file;
    :init internal variable;
}
partition Running {
    :wait for user interaction;
    :print information;
}

stop
@enduml
""".trimIndent(),
        "Partition 2" to """
@startuml
start
partition #lightGreen "Input Interface" {
    :read config file;
    :init internal variable;
}
partition Running {
    :wait for user interaction;
    :print information;
}
stop
@enduml
""".trimIndent(),
        "Partition 3" to """
@startuml
start
partition "[[http://plantuml.com partition_name]]" {
    :read doc. on [[http://plantuml.com plantuml_website]];
    :test diagram;
}
end
@enduml
""".trimIndent(),
        "Group, Partition, Package, Rectangle or Card" to """
@startuml
start
group Group {
  :Activity;
}
floating note: Note on Group

partition Partition {
  :Activity;
}
floating note: Note on Partition

package Package {
  :Activity;
}
floating note: Note on Package 

rectangle Rectangle {
  :Activity;
}
floating note: Note on Rectangle 

card Card {
  :Activity;
}
floating note: Note on Card
end
@enduml
""".trimIndent(),
        "Swimlanes" to """
@startuml
|Swimlane1|
start
:foo1;
|#AntiqueWhite|Swimlane2|
:foo2;
:foo3;
|Swimlane1|
:foo4;
|Swimlane2|
:foo5;
stop
@enduml
""".trimIndent(),
        "Swimlanes 2" to """
@startuml
|#pink|Actor_For_red|
start
if (color?) is (red) then
:**action red**; <<#pink>>
:foo1;
else (not red)
|#lightgray|Actor_For_no_red|
:**action not red**; <<#lightgray>>
:foo2;
endif
|Next_Actor|
:foo3; <<#lightblue>>
:foo4;
|Final_Actor|
:foo5; <<#palegreen>>
stop
@enduml
""".trimIndent(),
        "Swimlanes 3" to """
@startuml
|#palegreen|f| fisherman
|c| cook
|#gold|e| eater
|f|
start
:go fish;
|c|
:fry fish;
|e|
:eat fish;
stop
@enduml
""".trimIndent(),
        "Detach or kill [detach, kill]" to """
@startuml
 :start;
 fork
   :foo1;
   :foo2;
 fork again
   :foo3;
   detach
 endfork
 if (foo4) then
   :foo5;
   detach
 endif
 :foo6;
 detach
 :foo7;
 stop
@enduml
""".trimIndent(),
        "Detach or kill [detach, kill] 2" to """
@startuml
 :start;
 fork
   :foo1;
   :foo2;
 fork again
   :foo3;
   kill
 endfork
 if (foo4) then
   :foo5;
   kill
 endif
 :foo6;
 kill
 :foo7;
 stop
@enduml
""".trimIndent(),
        "Emoji as action (with icon stereotype)" to """
@startuml
while (<:cloud_with_rain:>)
  :<:umbrella:>; <<icon>>
endwhile
-<<icon>><:closed_umbrella:>
@enduml
""".trimIndent(),
        "SDL using stereotype (Current official form)" to """
@startuml
start
:SDL Shape;
:input; <<input>>
:output; <<output>>
:procedure; <<procedure>>
:load; <<load>>
:save; <<save>>
:continuous; <<continuous>>
:task; <<task>>
end
@enduml
""".trimIndent(),
        "SDL using stereotype (Current official form) 2" to """
@startuml
:Ready;
:next(o); <<procedure>>
:Receiving;
split
 :nak(i); <<input>>
 :ack(o); <<output>>
split again
 :ack(i); <<input>>
 :next(o)
 on several lines; <<procedure>>
 :i := i + 1; <<task>>
 :ack(o); <<output>>
split again
 :err(i); <<input>>
 :nak(o); <<output>>
split again
 :foo; <<save>>
split again
 :bar; <<load>>
split again
 :i > 5; <<continuous>>
stop
end split
:finish;
@enduml
""".trimIndent(),
        "UML Shape Example using Stereotype" to """
@startuml
:action;
:object; <<object>>

:ObjectNode
typed by signal; <<objectSignal>>

:AcceptEventAction
without TimeEvent trigger; <<acceptEvent>>

:SendSignalAction; <<sendSignal>>

:SendObjectAction
with signal type; <<sendSignal>>

:Trigger; <<trigger>>

:\t\t\t\t\t\tAcceptEventAction
\t\t\t\t\t\twith TimeEvent trigger; <<timeEvent>>
:an action;
@enduml
""".trimIndent(),
        "Complete example" to """
@startuml

start
:ClickServlet.handleRequest();
:new page;
if (Page.onSecurityCheck) then (true)
  :Page.onInit();
  if (isForward?) then (no)
    :Process controls;
    if (continue processing?) then (no)
      stop
    endif

    if (isPost?) then (yes)
      :Page.onPost();
    else (no)
      :Page.onGet();
    endif
    :Page.onRender();
  endif
else (false)
endif

if (do redirect?) then (yes)
  :redirect process;
else
  if (do forward?) then (yes)
    :Forward request;
  else (no)
    :Render page template;
  endif
endif

stop

@enduml
""".trimIndent(),
        "Inside style (by default)" to """
@startuml
skinparam conditionStyle inside
start
repeat
  :act1;
  :act2;
repeatwhile (<b>end)
:act3;
@enduml
""".trimIndent(),
        "Inside style (by default) 2" to """
@startuml
start
repeat
  :act1;
  :act2;
repeatwhile (<b>end)
:act3;
@enduml
""".trimIndent(),
        "Diamond style" to """
@startuml
skinparam conditionStyle diamond
start
repeat
  :act1;
  :act2;
repeatwhile (<b>end)
:act3;
@enduml
""".trimIndent(),
        "InsideDiamond (or Foo1 ) style" to """
@startuml
skinparam conditionStyle InsideDiamond
start
repeat
  :act1;
  :act2;
repeatwhile (<b>end)
:act3;
@enduml
""".trimIndent(),
        "InsideDiamond (or Foo1 ) style 2" to """
@startuml
skinparam conditionStyle foo1
start
repeat
  :act1;
  :act2;
repeatwhile (<b>end)
:act3;
@enduml
""".trimIndent(),
        "Diamond style (by default)" to """
@startuml
skinparam ConditionEndStyle diamond
:A;
if (decision) then (yes)
    :B1;
else (no)
endif
:C;
@enduml
""".trimIndent(),
        "Diamond style (by default) 2" to """
@startuml
skinparam ConditionEndStyle diamond
:A;
if (decision) then (yes)
    :B1;
else (no)
    :B2;
endif
:C;
@enduml
""".trimIndent(),
        "Horizontal line (hline) style" to """
@startuml
skinparam ConditionEndStyle hline
:A;
if (decision) then (yes)
    :B1;
else (no)
endif
:C;
@enduml
""".trimIndent(),
        "Horizontal line (hline) style 2" to """
@startuml
skinparam ConditionEndStyle hline
:A;
if (decision) then (yes)
    :B1;
else (no)
    :B2;
endif
:C;
@enduml
""".trimIndent(),
        "Without style (by default)" to """
@startuml
start
:init;
-> test of color;
if (color?) is (<color:red>red) then
:print red;
else 
:print not red;
note right: no color
endif
partition End {
:end;
}
-> this is the end;
end
@enduml
""".trimIndent(),
        "With style" to """
@startuml
<style>
activityDiagram {
  BackgroundColor #33668E
  BorderColor #33668E
  FontColor #888
  FontName arial

  diamond {
    BackgroundColor #ccf
    LineColor #00FF00
    FontColor green
    FontName arial
    FontSize 15
  }
  arrow {
    FontColor gold
    FontName arial
    FontSize 15
  }
  partition {
    LineColor red
    FontColor green
    RoundCorner 10
    BackgroundColor PeachPuff
  }
  note {
    FontColor Blue
    LineColor Navy
    BackgroundColor #ccf
  }
}
document {
   BackgroundColor transparent
}
</style>
start
:init;
-> test of color;
if (color?) is (<color:red>red) then
:print red;
else 
:print not red;
note right: no color
endif
partition End {
:end;
}
-> this is the end;
end
@enduml
""".trimIndent(),
        "Creole on Activity" to """
@startuml
:Creole:
  wave: ~~wave~~
  bold: **bold**
  italics: //italics//
  monospaced: ""monospaced""
  stricken-out: --stricken-out--
  underlined: __underlined__
  not-underlined: ~__not underlined__
  wave-underlined: ~~wave-underlined~~;
:HTML Creole:
  bold: <b>bold
  italics: <i>italics
  monospaced: <font:monospaced>monospaced
  stroked: <s>stroked
  underlined: <u>underlined
  waved: <w>waved
  green-stroked: <s:green>stroked
  red-underlined: <u:red>underlined
  blue-waved: <w:#0000FF>waved
  Blue: <color:blue>Blue
  Orange: <back:orange>Orange background
  big: <size:20>big;
:Graphic:
  OpenIconic: account-login <&account-login> 
  Unicode: This is <U+221E> long
  Emoji: <:calendar:> Calendar
  Image:
  <img:https://plantuml.com/logo3.png>;
@enduml
""".trimIndent(),
    )
}
