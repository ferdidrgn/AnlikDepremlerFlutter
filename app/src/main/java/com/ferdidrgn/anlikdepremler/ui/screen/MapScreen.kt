package com.ferdi.deprem.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ferdi.deprem.model.Earthquake
import com.ferdi.deprem.model.MockData

// Dahili Renk Tanımları
private val ColorRed = Color(0xFFEF4444)
private val ColorOrange = Color(0xFFF97316)
private val ColorGreen = Color(0xFF10B981)
private val ColorBlue = Color(0xFF3B82F6)
private val ColorDarkBg = Color(0xFF0A1E3C)
private val ColorCardBg = Color(0xFF1F2937)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onEarthquakeClick: (Earthquake) -> Unit = {}
) {
    var selectedEarthquake by remember { mutableStateOf(MockData.sampleEarthquakes.first()) }
    var filterSignificantOnly by remember { mutableStateOf(false) }

    val displayedEarthquakes = if (filterSignificantOnly) {
        MockData.sampleEarthquakes.filter { it.magnitude >= 4.0 }
    } else {
        MockData.sampleEarthquakes
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = "https://picsum.photos/1080/1920?random=88",
            contentDescription = "Map Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ColorDarkBg.copy(alpha = 0.8f))
        )

        val infiniteTransition = rememberInfiniteTransition(label = "mapPulse")
        val pulseRadius by infiniteTransition.animateFloat(
            initialValue = 15f,
            targetValue = 50f,
            animationSpec = infiniteRepeatable(tween(1800, easing = LinearEasing), RepeatMode.Restart),
            label = "pulseRadius"
        )
        val pulseAlpha by infiniteTransition.animateFloat(
            initialValue = 0.8f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(tween(1800, easing = LinearEasing), RepeatMode.Restart),
            label = "pulseAlpha"
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridColor = Color.White.copy(alpha = 0.05f)
            for (i in 0..15) {
                drawLine(gridColor, start = Offset(size.width * i / 15, 0f), end = Offset(size.width * i / 15, size.height), strokeWidth = 1f)
                drawLine(gridColor, start = Offset(0f, size.height * i / 15), end = Offset(size.width, size.height * i / 15), strokeWidth = 1f)
            }

            val faultLine = Path().apply {
                moveTo(0.1f * size.width, 0.4f * size.height)
                cubicTo(
                    0.3f * size.width, 0.35f * size.height,
                    0.6f * size.width, 0.5f * size.height,
                    0.9f * size.width, 0.45f * size.height
                )
            }
            drawPath(path = faultLine, color = ColorRed.copy(alpha = 0.4f), style = Stroke(width = 3f))

            displayedEarthquakes.forEach { eq ->
                val x = ((eq.longitude - 26.0) / 14.0 * size.width).toFloat().coerceIn(100f, size.width - 100f)
                val y = ((eq.latitude - 35.0) / 7.0 * size.height).toFloat().coerceIn(200f, size.height - 300f)
                val center = Offset(x, y)

                val color = when {
                    eq.magnitude >= 7.0 -> ColorRed
                    eq.magnitude >= 5.0 -> ColorOrange
                    eq.magnitude >= 3.0 -> ColorBlue
                    else -> ColorGreen
                }

                if (eq.id == selectedEarthquake.id) {
                    drawCircle(color = color.copy(alpha = pulseAlpha), radius = pulseRadius * 1.5f, center = center)
                }

                drawCircle(color = color.copy(alpha = 0.25f), radius = (eq.magnitude * 5f).toFloat(), center = center)
                drawCircle(color = color, radius = 8f, center = center)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = ColorDarkBg.copy(alpha = 0.9f)
            ) {
                Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Layers, contentDescription = null, tint = ColorBlue, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Canlı Fay Haritası", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = filterSignificantOnly,
                    onClick = { filterSignificantOnly = !filterSignificantOnly },
                    label = { Text("M ≥ 4.0", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(14.dp)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ColorRed,
                        selectedLabelColor = Color.White,
                        containerColor = ColorCardBg.copy(alpha = 0.9f),
                        labelColor = Color.White
                    )
                )

                FloatingActionButton(
                    onClick = { },
                    modifier = Modifier.size(40.dp),
                    containerColor = ColorBlue,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "Konum", modifier = Modifier.size(20.dp))
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 90.dp)
        ) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(displayedEarthquakes) { eq ->
                    val isSelected = eq.id == selectedEarthquake.id
                    Card(
                        modifier = Modifier
                            .width(280.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFF132D5E) else ColorCardBg.copy(alpha = 0.95f)
                        ),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, ColorBlue) else null,
                        onClick = {
                            selectedEarthquake = eq
                            onEarthquakeClick(eq)
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        color = when {
                                            eq.magnitude >= 6.0 -> ColorRed
                                            eq.magnitude >= 4.0 -> ColorOrange
                                            else -> ColorGreen
                                        }.copy(alpha = 0.2f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = String.format("%.1f", eq.magnitude),
                                    color = if (eq.magnitude >= 6.0) ColorRed else ColorBlue,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(eq.location, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("${eq.region} • ${eq.depth} km", color = Color.Gray, fontSize = 11.sp)
                                Text("${eq.date} ${eq.time}", color = ColorBlue, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}