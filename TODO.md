# PlantUML Functionality TODO

## Implemented
- [x] Conditional with stop on an action [kill, detach]
- [x] Repeat loop [repeat, repeat while]
- [x] While loop [while, endwhile]
- [x] Notes [note, end note, floating]
- [x] Arrows [->, -->]
- [x] Detach or kill [detach, kill]

## Partially Implemented
- [x] Parallel processing [fork, fork again, end fork, end merge] (missing `end merge`)

## Missing (To Implement)
- [x] Switch and case [switch, case, endswitch]
- [x] Break on a repeat loop [break]
- [x] Goto and Label Processing [label, goto]
- [x] Split processing [split, split again, end split]
- [x] Colors (e.g., `#red`)
- [x] Lines without arrows (e.g., `--`)
- [x] Simple colored arrow [link] (e.g., `-[#red]->`)
- [x] Multiple colored arrow (e.g., `-[#red,dashed]->`)
- [x] Connector (or Circle) (e.g., `(A)`)
- [x] Color on connector (e.g., `(A) #red`)
- [x] Grouping or partition (e.g., `partition "Name" { ... }`)
- [x] Swimlanes (e.g., `|Lane|`)
- [x] Emoji as action (with `+icon+` stereotype)
# Class Diagram Implementation Tasks

## Easy / Fundamental
- [ ] Declarative element (classes, interfaces, etc.)
- [ ] Relations between classes (arrows)
- [ ] Label on relations
- [ ] Changing arrows orientation

## Moderate / Body
- [ ] Adding methods and fields
- [ ] Defining visibility (public `+`, private `-`, protected `#`, package `~`)
- [ ] Using non-letters in element names and relation labels
- [ ] Abstract and Static (modifiers)
- [ ] Advanced class body (separators like `==`, `--`)
- [ ] Visibility on compositions and aggregations
- [ ] Abstract class and interface (declaration `abstract class`, `interface`)

## Advanced / Features
- [ ] Notes and stereotypes
- [ ] More on notes
- [ ] Note on field (field, attribute, member) or method
- [ ] Note on links
- [ ] Use generics (e.g., `class List<T>`)
- [ ] Specific Spot (custom spot icons)
- [ ] Extends and implements

## Packages / Grouping
- [ ] Packages
- [ ] Namespaces
- [ ] Packages style
- [ ] Automatic package creation
- [ ] Packages and Namespaces Enhancement

## Visiblity / Hiding
- [ ] Hide attributes, methods...
- [ ] Hide classes
- [ ] Remove classes
- [ ] Hide, Remove or Restore tagged element or wildcard
- [ ] Hide or Remove unlinked class

## Styling / Layout
- [ ] Skinparam
- [ ] Skinned Stereotypes
- [ ] Color gradient
- [ ] Change relation (linking or arrow) color and style (inline style)
- [ ] Change class color and style (inline style)
- [ ] Bracketed relations (linking or arrow) style
- [ ] Change diagram orientation
- [ ] Help on layout

## Complex Relations / Features
- [ ] Association classes
- [ ] Association on same class
- [ ] Qualified associations
- [ ] Role label to associations
- [ ] Lollipop interface
- [ ] Arrows from/to class members
- [ ] Grouping inheritance arrow heads
- [ ] Splitting large files (includes/includesub)
- [ ] Display JSON Data on Class or Object diagram
