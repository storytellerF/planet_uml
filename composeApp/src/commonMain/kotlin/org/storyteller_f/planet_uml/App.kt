package org.storyteller_f.planet_uml

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.example.parsePlantUML
import java.nio.charset.StandardCharsets
import java.util.Base64

@Composable
fun App() {
    MaterialTheme {
        var plantUmlText by remember {
            mutableStateOf(
                """@startuml
start
:Hello world;
if (condition) then (yes)
  :Some activity;
else (no)
  :Other activity;
endif
stop
@enduml""".trim()
            )
        }

        var svgDataUri by remember { mutableStateOf<String?>(null) }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        // Update SVG when text changes
        LaunchedEffect(plantUmlText) {
            try {
                val diagram = parsePlantUML(plantUmlText)
                if (diagram != null) {
                    val svg = diagram.toSvg()
                    val bytes = svg.toByteArray(StandardCharsets.UTF_8)
                    val base64 = Base64.getEncoder().encodeToString(bytes)
                    svgDataUri = "data:image/svg+xml;base64,$base64"
                    errorMessage = null
                } else {
                    errorMessage = "Syntax Error parsing diagram"
                }
            } catch (e: Exception) {
                errorMessage = "Error: \${e.message}"
            }
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Left Pane: Editor
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(16.dp)
            ) {
                Text("PlantUML Editor", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = plantUmlText,
                    onValueChange = { plantUmlText = it },
                    modifier = Modifier.fillMaxSize(),
                    textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            // Divider
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Right Pane: Preview
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(16.dp)
            ) {
                Text("SVG Preview", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(8.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    if (svgDataUri != null) {
                        AsyncImage(
                            model = coil3.request.ImageRequest.Builder(coil3.compose.LocalPlatformContext.current)
                                .data(svgDataUri)
                                .decoderFactory(coil3.svg.SvgDecoder.Factory())
                                .build(),
                            contentDescription = "Diagram Preview",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}