package com.ferdidrgn.anlikdepremler.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ferdi.deprem.model.Earthquake
import com.ferdidrgn.anlikdepremler.R
import com.ferdidrgn.anlikdepremler.core.ui.animation.AppAnimations
import com.ferdidrgn.anlikdepremler.core.ui.animation.AppAnimations.shimmer
import com.ferdidrgn.anlikdepremler.data.remote.EarthquakeSource
import com.ferdidrgn.anlikdepremler.ui.components.NativeAdCard
import com.ferdidrgn.anlikdepremler.ui.util.shouldShowAdAtIndex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EarthquakeListScreen(
    viewModel: MainViewModel,
    onEarthquakeClick: (Earthquake) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var minMagnitudeFilter by remember { mutableStateOf(0.0) }

    val filteredList = uiState.earthquakes.filter { eq ->
        val matchesQuery = eq.location.contains(searchQuery, ignoreCase = true) ||
                eq.region.contains(searchQuery, ignoreCase = true)
        val matchesMag = eq.magnitude >= minMagnitudeFilter
        matchesQuery && matchesMag
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        // ... (Üst Arama & Filtreleme Kısmı) ...

        Spacer(modifier = Modifier.height(10.dp))

        // 🎯 YÜKLENİYORSA SHIMMER (SKELETON) ANİMASYONU ÇALIŞTIRILIYOR
        if (uiState.isLoading) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                repeat(6) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .shimmer()
                    )
                }
            }
        } else {
            // 🎯 LİSTE ELEMANLARI YAYLANARAK GELEN (SPRING ENTRANCE) ANİMASYONUYLA YÜKLENİYOR
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                itemsIndexed(
                    items = filteredList,
                    key = { _, item -> item.id }
                ) { index, eq ->
                    AppAnimations.SpringEntranceContainer {
                        PremiumEarthquakeCard(
                            earthquake = eq,
                            onClick = { onEarthquakeClick(eq) }
                        )
                    }

                    if (shouldShowAdAtIndex(index, filteredList.size)) {
                        Spacer(modifier = Modifier.height(4.dp))
                        NativeAdCard()
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun PremiumEarthquakeCard(
    earthquake: Earthquake,
    onClick: () -> Unit
) {
    val (magBgColor, magTextColor) = when {
        earthquake.magnitude >= 5.0 -> Color(0xFFFFEBEE) to Color(0xFFD32F2F)
        earthquake.magnitude >= 3.5 -> Color(0xFFFFF3E0) to Color(0xFFE65100)
        earthquake.magnitude >= 2.0 -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        else -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = earthquake.cityImageUrl.ifEmpty { "https://picsum.photos/400/250?random=${earthquake.id}" },
                contentDescription = earthquake.location,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = earthquake.location,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${earthquake.region} • ${earthquake.date} ${earthquake.time}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
                Text(
                    text = stringResource(R.string.source_label, earthquake.source),
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(magBgColor, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = String.format("%.1f", earthquake.magnitude),
                    color = magTextColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
            }
        }
    }
}