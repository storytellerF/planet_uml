package org.example

import com.strumenta.antlrkotlin.parsers.generated.PlantUMLParser

enum class ClassType {
    CLASS, INTERFACE, ABSTRACT_CLASS,
    ANNOTATION, CIRCLE, DATACLASS, DIAMOND, ENTITY, ENUM, EXCEPTION,
    METACLASS, PROTOCOL, RECORD, STEREOTYPE, STRUCT
}

data class ClassNode(
    val name: String,
    var type: ClassType,
    var stereotype: String? = null,
    val members: MutableList<String> = mutableListOf()
)

data class RelationNode(
    val from: String,
    val to: String,
    val arrow: String,
    val label: String?
)

class ClassDiagram(classDiagramCtx: PlantUMLParser.Class_diagramContext) : Diagram {
    val classes = mutableMapOf<String, ClassNode>()
    val relations = mutableListOf<RelationNode>()

    init {
        classDiagramCtx.class_statement().forEach { stmt ->
            when {
                stmt.class_declaration() != null -> {
                    val declCtx = stmt.class_declaration()!!
                    val name = declCtx.identifier()?.text ?: return@forEach
                    val stereotype = declCtx.stereotype()?.paragraph_text()?.text
                    
                    val typeStr = declCtx.children?.firstOrNull()?.text?.lowercase() ?: "class"
                    val type = when {
                        typeStr == "interface" -> ClassType.INTERFACE
                        typeStr.contains("abstract") -> ClassType.ABSTRACT_CLASS
                        typeStr == "annotation" -> ClassType.ANNOTATION
                        typeStr == "circle" || typeStr == "()" -> ClassType.CIRCLE
                        typeStr == "dataclass" -> ClassType.DATACLASS
                        typeStr == "diamond" || typeStr == "<>" -> ClassType.DIAMOND
                        typeStr == "entity" -> ClassType.ENTITY
                        typeStr == "enum" -> ClassType.ENUM
                        typeStr == "exception" -> ClassType.EXCEPTION
                        typeStr == "metaclass" -> ClassType.METACLASS
                        typeStr == "protocol" -> ClassType.PROTOCOL
                        typeStr == "record" -> ClassType.RECORD
                        typeStr == "stereotype" -> ClassType.STEREOTYPE
                        typeStr == "struct" -> ClassType.STRUCT
                        else -> ClassType.CLASS
                    }
                    
                    val node = classes.getOrPut(name) { ClassNode(name, type, stereotype) }
                    if (node.type == ClassType.CLASS && type != ClassType.CLASS) {
                        node.type = type
                    }
                    if (stereotype != null) {
                        node.stereotype = stereotype
                    }
                    
                    declCtx.class_body_element().forEach { bodyCtx ->
                        bodyCtx.paragraph_text()?.text?.let { node.members.add(it.trim()) }
                    }
                }
                stmt.class_relation() != null -> {
                    val relCtx = stmt.class_relation()!!
                    val ids = relCtx.identifier()
                    if (ids.size >= 2) {
                        val from = ids[0].text
                        val to = ids[1].text
                        val arrow = relCtx.class_relation_arrow()?.text ?: "--"
                        val label = relCtx.paragraph_text()?.text?.trim()
                        
                        classes.getOrPut(from) { ClassNode(from, ClassType.CLASS) }
                        classes.getOrPut(to) { ClassNode(to, ClassType.CLASS) }
                        
                        relations.add(RelationNode(from, to, arrow, label))
                    }
                }
            }
        }
    }

    override fun toSvg(): String {
        val nodeW = 150.0
        val hSpacing = 150.0
        val vSpacing = 100.0
        
        val maxW = 800.0
        var maxH = 600.0
        
        val coordinates = mutableMapOf<String, Pair<Double, Double>>()
        var currX = 50.0
        var currY = 50.0
        
        classes.values.forEach { node ->
            coordinates[node.name] = currX to currY
            currX += nodeW + hSpacing
            if (currX + nodeW > maxW) {
                currX = 50.0
                currY += node.members.size * 20.0 + 50.0 + vSpacing
            }
        }
        maxH = currY + 200.0
        
        return buildString {
            appendLine("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"$maxW\" height=\"$maxH\">")
            
            appendLine("  <g stroke=\"#333333\" fill=\"none\" stroke-width=\"1.5\">")
            relations.forEach { rel ->
                val p1 = coordinates[rel.from]
                val p2 = coordinates[rel.to]
                if (p1 != null && p2 != null) {
                    val h1 = 40.0 + (classes[rel.from]?.members?.size ?: 0) * 20.0
                    val h2 = 40.0 + (classes[rel.to]?.members?.size ?: 0) * 20.0
                    val x1 = p1.first + nodeW / 2
                    val y1 = p1.second + h1 / 2
                    val x2 = p2.first + nodeW / 2
                    val y2 = p2.second + h2 / 2
                    
                    val isDashed = rel.arrow.contains("..")
                    val styleAttr = if (isDashed) " stroke-dasharray=\"5,5\"" else ""
                    appendLine("    <line x1=\"$x1\" y1=\"$y1\" x2=\"$x2\" y2=\"$y2\" stroke=\"#333333\"$styleAttr/>")
                    
                    if (rel.label != null && rel.label.isNotBlank()) {
                        val cx = (x1 + x2) / 2
                        val cy = (y1 + y2) / 2
                        val provider = textToPathProvider
                        if (provider != null) {
                            val pathD = provider(rel.label, cx, cy.toDouble(), 12)
                            appendLine("    <path d=\"$pathD\" fill=\"#000000\" stroke=\"none\" />")
                        } else {
                            appendLine("    <text x=\"$cx\" y=\"${cy - 5}\" fill=\"#000000\" font-size=\"12\" text-anchor=\"middle\">${rel.label}</text>")
                        }
                    }
                }
            }
            appendLine("  </g>")
            
            appendLine("  <g font-family=\"sans-serif\" font-size=\"14\">")
            classes.values.forEach { node ->
                val p = coordinates[node.name]!!
                val x = p.first
                val y = p.second
                
                val isShape = node.type == ClassType.CIRCLE || node.type == ClassType.DIAMOND
                if (isShape) {
                    if (node.type == ClassType.CIRCLE) {
                        appendLine("    <circle cx=\"${x + nodeW/2}\" cy=\"${y + 20}\" r=\"8\" fill=\"#F9F9CA\" stroke=\"#a80036\" stroke-width=\"1.5\"/>")
                    } else {
                        appendLine("    <polygon points=\"${x + nodeW/2},${y + 12} ${x + nodeW/2 + 8},${y + 20} ${x + nodeW/2},${y + 28} ${x + nodeW/2 - 8},${y + 20}\" fill=\"#F9F9CA\" stroke=\"#a80036\" stroke-width=\"1.5\"/>")
                    }
                    val nameProvider = textToPathProvider
                    val textY = y + 40
                    if (nameProvider != null) {
                        val pathD = nameProvider(node.name, x + nodeW/2, textY, 14)
                        appendLine("    <path d=\"$pathD\" fill=\"#000000\" stroke=\"none\" />")
                    } else {
                        appendLine("    <text x=\"${x + nodeW/2}\" y=\"$textY\" fill=\"#000000\" text-anchor=\"middle\">${node.name}</text>")
                    }
                } else {
                    val h = 40.0 + kotlin.math.max(0, node.members.size) * 20.0 + if (node.stereotype != null) 20.0 else 0.0
                    appendLine("    <rect x=\"$x\" y=\"$y\" width=\"$nodeW\" height=\"$h\" fill=\"#f9f9ca\" stroke=\"#a80036\" stroke-width=\"1.5\" rx=\"2\" ry=\"2\"/>")
                    
                    val typeInfo = when (node.type) {
                        ClassType.INTERFACE -> Pair("I", "#B4A7E5")
                        ClassType.ABSTRACT_CLASS -> Pair("A", "#A9DCDF")
                        ClassType.ANNOTATION -> Pair("@", "#E3664A")
                        ClassType.DATACLASS -> Pair("D", "#8961B0")
                        ClassType.ENTITY -> Pair("E", "#ADD1B2")
                        ClassType.ENUM -> Pair("E", "#EB937F")
                        ClassType.EXCEPTION -> Pair("X", "#E95034")
                        ClassType.METACLASS -> Pair("M", "#C7C7C7")
                        ClassType.PROTOCOL -> Pair("P", "#F0F0F0")
                        ClassType.RECORD -> Pair("R", "#F29900")
                        ClassType.STEREOTYPE -> Pair("S", "#F07CDE")
                        ClassType.STRUCT -> Pair("S", "#FFFFFF")
                        else -> Pair("C", "#ADD1B2")
                    }
                    val letter = typeInfo.first
                    val bg = typeInfo.second
                    
                    val cx = x + 18
                    val cy = y + 18
                    appendLine("    <circle cx=\"$cx\" cy=\"$cy\" r=\"10\" fill=\"$bg\" stroke=\"#a80036\" stroke-width=\"1\"/>")
                    val p = textToPathProvider
                    if (p != null) {
                        val pathD = p(letter, cx, cy + 4, 12)
                        appendLine("    <path d=\"$pathD\" fill=\"#000000\" stroke=\"none\" />")
                    } else {
                        appendLine("    <text x=\"$cx\" y=\"${cy + 4}\" fill=\"#000000\" font-size=\"12\" font-weight=\"bold\" font-style=\"italic\" text-anchor=\"middle\">$letter</text>")
                    }
                    
                    var textY = y + 15
                    if (node.stereotype != null) {
                        val stereo = "&lt;&lt;${node.stereotype}&gt;&gt;"
                        if (p != null) {
                            val pathD = p(stereo, x + nodeW/2, textY, 12)
                            appendLine("    <path d=\"$pathD\" fill=\"#000000\" stroke=\"none\" />")
                        } else {
                            appendLine("    <text x=\"${x + nodeW/2}\" y=\"$textY\" fill=\"#000000\" font-size=\"12\" font-style=\"italic\" text-anchor=\"middle\">$stereo</text>")
                        }
                        textY += 15
                    }
                    
                    val nameProvider = textToPathProvider
                    if (nameProvider != null) {
                        val pathD = nameProvider(node.name, x + nodeW/2, textY, 14)
                        appendLine("    <path d=\"$pathD\" fill=\"#000000\" stroke=\"none\" />")
                    } else {
                        appendLine("    <text x=\"${x + nodeW/2}\" y=\"$textY\" fill=\"#000000\" font-weight=\"bold\" text-anchor=\"middle\">${node.name}</text>")
                    }
                    textY += 10
                    
                    if (node.members.isNotEmpty() || true) {
                        appendLine("    <line x1=\"$x\" y1=\"$textY\" x2=\"${x + nodeW}\" y2=\"$textY\" stroke=\"#a80036\" stroke-width=\"1.5\"/>")
                        textY += 15
                    }
                    
                    node.members.forEach { member ->
                        val cleanMember = member.replace("<", "&lt;").replace(">", "&gt;")
                        val provider = textToPathProvider
                        if (provider != null) {
                            val pathD = provider(cleanMember, x + 5, textY, 12)
                            appendLine("    <path d=\"$pathD\" fill=\"#000000\" stroke=\"none\" />")
                        } else {
                            appendLine("    <text x=\"${x + 5}\" y=\"$textY\" fill=\"#000000\" font-size=\"12\" text-anchor=\"start\">$cleanMember</text>")
                        }
                        textY += 20
                    }
                }
            }
            appendLine("  </g>")
            
            appendLine("</svg>")
        }
    }
}
