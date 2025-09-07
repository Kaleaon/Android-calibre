package com.universalmedialibrary.ui.reader

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.universalmedialibrary.data.settings.AutoScrollMode
import com.universalmedialibrary.data.settings.ReaderSettings

@Composable
fun AutoScrollController(
    isEnabled: Boolean,
    settings: ReaderSettings,
    onToggle: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    if (isEnabled) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Auto-Scroll Active",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    IconButton(onClick = onToggle) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "Stop auto-scroll",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Slider(
                        value = settings.autoScrollSpeed,
                        onValueChange = onSpeedChange,
                        valueRange = 10f..100f,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            activeTrackColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            inactiveTrackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${settings.autoScrollSpeed.toInt()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.width(30.dp)
                    )
                }
                
                Text(
                    text = "Mode: ${settings.autoScrollMode.name.replace("_", " ").lowercase().split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun AutoScrollingText(
    text: String,
    isScrolling: Boolean,
    speed: Float,
    mode: AutoScrollMode,
    modifier: Modifier = Modifier,
    onScrollEnd: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var targetOffset by remember { mutableStateOf(0) }
    
    // Calculate scroll animation based on mode and speed
    val animationSpec = when (mode) {
        AutoScrollMode.ROLLING_BLIND -> infiniteRepeatable(
            animation = tween<Float>(
                durationMillis = (60000 / speed).toInt(), // Convert speed to duration
                easing = LinearEasing
            )
        )
        AutoScrollMode.BY_PIXEL -> infiniteRepeatable(
            animation = tween<Float>(
                durationMillis = (1000 / speed * 10).toInt(), // Adjust for pixel-based scrolling
                easing = LinearEasing
            )
        )
        AutoScrollMode.BY_LINE -> infiniteRepeatable(
            animation = tween<Float>(
                durationMillis = (2000 / speed * 10).toInt(), // Line-based scrolling
                easing = LinearEasing
            )
        )
    }
    
    LaunchedEffect(isScrolling, speed, mode) {
        if (isScrolling) {
            when (mode) {
                AutoScrollMode.ROLLING_BLIND -> {
                    // Smooth continuous scrolling
                    while (isScrolling) {
                        val maxScroll = scrollState.maxValue
                        val currentScroll = scrollState.value
                        val increment = (speed / 10).toInt()
                        
                        if (currentScroll + increment >= maxScroll) {
                            onScrollEnd()
                            break
                        }
                        
                        scrollState.animateScrollTo(
                            value = currentScroll + increment,
                            animationSpec = tween(100, easing = LinearEasing)
                        )
                        delay(100)
                    }
                }
                AutoScrollMode.BY_PIXEL -> {
                    // Pixel-by-pixel scrolling
                    while (isScrolling) {
                        val maxScroll = scrollState.maxValue
                        val currentScroll = scrollState.value
                        
                        if (currentScroll + 1 >= maxScroll) {
                            onScrollEnd()
                            break
                        }
                        
                        scrollState.scrollTo(currentScroll + 1)
                        delay((100 / speed * 10).toLong())
                    }
                }
                AutoScrollMode.BY_LINE -> {
                    // Line-by-line scrolling
                    while (isScrolling) {
                        val maxScroll = scrollState.maxValue
                        val currentScroll = scrollState.value
                        val lineHeight = 24 // Approximate line height in pixels
                        
                        if (currentScroll + lineHeight >= maxScroll) {
                            onScrollEnd()
                            break
                        }
                        
                        scrollState.animateScrollTo(
                            value = currentScroll + lineHeight,
                            animationSpec = tween(200, easing = LinearEasing)
                        )
                        delay((1000 / speed * 10).toLong())
                    }
                }
            }
        }
    }
    
    Text(
        text = text,
        modifier = modifier.verticalScroll(scrollState),
        style = MaterialTheme.typography.bodyLarge
    )
}