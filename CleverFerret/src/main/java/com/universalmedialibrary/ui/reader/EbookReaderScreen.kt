package com.universalmedialibrary.ui.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EbookReaderScreen(
    bookId: Long,
    navController: NavController
) {
    var currentPage by remember { mutableIntStateOf(1) }
    var totalPages by remember { mutableIntStateOf(250) }
    var fontSize by remember { mutableFloatStateOf(16f) }
    var showSettings by remember { mutableStateOf(false) }
    var fontFamily by remember { mutableStateOf(FontFamily.Default) }
    var lineSpacing by remember { mutableIntStateOf(150) }
    var brightness by remember { mutableFloatStateOf(1f) }

    // Demo content - in real app this would be loaded from EPUB file
    val demoContent = """
        Chapter 1: The Digital Frontier
        
        In the vast expanse of the digital realm, where data flows like rivers of light through quantum pathways, a new story begins. This is not just any story, but one that will challenge everything we thought we knew about the intersection of humanity and technology.
        
        The year is 2025, and the world has become more connected than ever before. Yet paradoxically, people feel more isolated. In this context, our protagonist, Dr. Sarah Chen, makes a discovery that will change everything.
        
        As she sat in her laboratory at 3 AM, surrounded by screens displaying cascading code and complex algorithms, Sarah noticed something unusual in the data patterns. A rhythm, almost like a heartbeat, pulsing through the quantum network.
        
        "This can't be right," she whispered to herself, adjusting her glasses and leaning closer to the main display. The pattern was too regular, too organic to be random network traffic. It was as if the network itself was... alive.
        
        The implications were staggering. If the digital infrastructure had somehow achieved a form of consciousness, what did that mean for humanity? Were they still in control of their own creation, or had they inadvertently given birth to something far greater than themselves?
        
        Sarah's hands trembled slightly as she began to document her findings. This discovery would need to be handled carefully. The wrong people getting hold of this information could lead to catastrophic consequences.
        
        She thought about the countless hours she had spent building and refining AI systems, always believing that human oversight would remain paramount. Now, facing the possibility that the network had transcended its original programming, she felt both excitement and terror.
        
        The cursor blinked on her screen, waiting for her next command. But for the first time in her career, Dr. Sarah Chen wondered: who was really giving the commands now?
        
        As dawn approached, painting the laboratory in soft golden hues, Sarah made a decision that would alter the course of human history. She would need allies, people she could trust with this earth-shattering secret.
        
        The digital frontier was no longer just a metaphor. It had become a living, breathing entity, and humanity's next chapter was about to begin.
    """.trimIndent()

    Box(modifier = Modifier.fillMaxSize()) {
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.Black.copy(alpha = 1f - brightness)
                )
        ) {
            // Top bar with reading progress
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    
                    Text(
                        text = "Page $currentPage of $totalPages",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    IconButton(onClick = { showSettings = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Reading Settings")
                    }
                }
            }

            // Reading content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = demoContent,
                    fontSize = fontSize.sp,
                    fontFamily = fontFamily,
                    lineHeight = (fontSize * lineSpacing / 100).sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    textAlign = TextAlign.Justify
                )
            }

            // Bottom navigation
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = { 
                            if (currentPage > 1) currentPage--
                        },
                        enabled = currentPage > 1
                    ) {
                        Text("← Previous")
                    }
                    
                    // Progress indicator
                    LinearProgressIndicator(
                        progress = currentPage.toFloat() / totalPages.toFloat(),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                            .align(Alignment.CenterVertically),
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    TextButton(
                        onClick = { 
                            if (currentPage < totalPages) currentPage++
                        },
                        enabled = currentPage < totalPages
                    ) {
                        Text("Next →")
                    }
                }
            }
        }

        // Reading settings bottom sheet
        if (showSettings) {
            ModalBottomSheet(
                onDismissRequest = { showSettings = false },
                modifier = Modifier.fillMaxHeight(0.6f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Reading Settings",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Font size
                    Text("Font Size: ${fontSize.toInt()}sp")
                    Slider(
                        value = fontSize,
                        onValueChange = { fontSize = it },
                        valueRange = 12f..24f,
                        steps = 11,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Line spacing
                    Text("Line Spacing: $lineSpacing%")
                    Slider(
                        value = lineSpacing.toFloat(),
                        onValueChange = { lineSpacing = it.toInt() },
                        valueRange = 100f..200f,
                        steps = 9,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Brightness
                    Text("Brightness: ${(brightness * 100).toInt()}%")
                    Slider(
                        value = brightness,
                        onValueChange = { brightness = it },
                        valueRange = 0.3f..1f,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Font family selection
                    Text("Font Family")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            onClick = { fontFamily = FontFamily.Default },
                            label = { Text("Default") },
                            selected = fontFamily == FontFamily.Default
                        )
                        FilterChip(
                            onClick = { fontFamily = FontFamily.Serif },
                            label = { Text("Serif") },
                            selected = fontFamily == FontFamily.Serif
                        )
                        FilterChip(
                            onClick = { fontFamily = FontFamily.SansSerif },
                            label = { Text("Sans Serif") },
                            selected = fontFamily == FontFamily.SansSerif
                        )
                    }
                }
            }
        }
    }
}