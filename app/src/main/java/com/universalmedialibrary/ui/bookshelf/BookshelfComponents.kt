package com.universalmedialibrary.ui.bookshelf

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.universalmedialibrary.data.local.model.BookDetails
import kotlin.math.absoluteValue

@Composable
fun EnhancedBookCard(
    book: BookDetails,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Box {
                EnhancedPlaceholderCover(
                    title = book.metadata.title,
                    author = book.authorName
                )
                
                // Favorite button overlay
                IconButton(
                    onClick = onFavoriteToggle,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = if (book.metadata.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Toggle Favorite",
                        tint = if (book.metadata.isFavorite) Color(0xFFE91E63) else Color.White,
                        modifier = Modifier
                            .background(
                                Color.Black.copy(alpha = 0.3f),
                                RoundedCornerShape(50)
                            )
                            .padding(4.dp)
                    )
                }
                
                // Download status indicator
                if (book.metadata.isDownloaded) {
                    Icon(
                        Icons.Default.CloudDone,
                        contentDescription = "Downloaded",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .background(
                                Color.Black.copy(alpha = 0.3f),
                                RoundedCornerShape(50)
                            )
                            .padding(4.dp)
                            .size(16.dp)
                    )
                }
            }
            
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = book.metadata.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (book.authorName != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = book.authorName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                if (book.metadata.rating != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    RatingStars(
                        rating = book.metadata.rating,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                // Progress bar (placeholder - would need reading progress data)
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = 0.3f, // Placeholder progress
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "30% complete", // Placeholder
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun CompactBookCard(
    book: BookDetails,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(120.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column {
            EnhancedPlaceholderCover(
                title = book.metadata.title,
                author = book.authorName,
                modifier = Modifier.height(160.dp)
            )
            
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = book.metadata.title,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (book.authorName != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = book.authorName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun ListBookItem(
    book: BookDetails,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Compact cover
            EnhancedPlaceholderCover(
                title = book.metadata.title,
                author = book.authorName,
                modifier = Modifier
                    .width(60.dp)
                    .height(80.dp)
                    .clip(RoundedCornerShape(6.dp))
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Book info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = book.metadata.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (book.authorName != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = book.authorName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                if (book.metadata.rating != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    RatingStars(rating = book.metadata.rating)
                }
                
                // Progress indicator
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LinearProgressIndicator(
                        progress = 0.3f, // Placeholder
                        modifier = Modifier
                            .weight(1f)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "30%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Action buttons
            Column {
                IconButton(onClick = onFavoriteToggle) {
                    Icon(
                        imageVector = if (book.metadata.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Toggle Favorite",
                        tint = if (book.metadata.isFavorite) Color(0xFFE91E63) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                if (book.metadata.isDownloaded) {
                    Icon(
                        Icons.Default.CloudDone,
                        contentDescription = "Downloaded",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CoverFlowCard(
    book: BookDetails,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(200.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Box {
                EnhancedPlaceholderCover(
                    title = book.metadata.title,
                    author = book.authorName,
                    modifier = Modifier.height(280.dp)
                )
                
                // Favorite overlay
                IconButton(
                    onClick = onFavoriteToggle,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = if (book.metadata.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Toggle Favorite",
                        tint = if (book.metadata.isFavorite) Color(0xFFE91E63) else Color.White,
                        modifier = Modifier
                            .background(
                                Color.Black.copy(alpha = 0.4f),
                                RoundedCornerShape(50)
                            )
                            .padding(6.dp)
                    )
                }
            }
            
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = book.metadata.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                if (book.authorName != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = book.authorName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                if (book.metadata.rating != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    RatingStars(
                        rating = book.metadata.rating,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun EnhancedPlaceholderCover(
    title: String,
    author: String?,
    modifier: Modifier = Modifier
) {
    val colors = listOf(
        Color(0xFF6200EE),
        Color(0xFF03DAC6),
        Color(0xFFFF6B6B),
        Color(0xFF4ECDC4),
        Color(0xFF45B7D1),
        Color(0xFF96CEB4),
        Color(0xFFFECEA8),
        Color(0xFFFF9999)
    )
    
    val color = colors[title.hashCode().absoluteValue % colors.size]
    val contentColor = Color.White
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 220.dp)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Book,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = contentColor.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
                color = contentColor,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Medium
            )
            
            if (author != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = author,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = contentColor.copy(alpha = 0.9f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun RatingStars(
    rating: Float,
    modifier: Modifier = Modifier,
    maxStars: Int = 5
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(maxStars) { index ->
            Icon(
                imageVector = if (index < rating.toInt()) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = Color(0xFFFFC107)
            )
        }
    }
}