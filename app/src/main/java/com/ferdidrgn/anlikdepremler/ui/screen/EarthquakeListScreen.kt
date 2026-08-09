package com.ferdi.deprem.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ferdi.deprem.model.Earthquake
import com.ferdidrgn.anlikdepremler.ui.components.NativeAdCard

enum class EarthquakeSourceFilter(val displayName: String) {
    ALL("Tümü"),
    AFAD("AFAD"),
    KANDILLI("Kandilli")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EarthquakeListScreen(
    earthquakes: List<Earthquake>,
    isLoading: Boolean = false,
    onEarthquakeClick: (Earthquake) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var minMagnitudeFilter by remember { mutableStateOf(0.0) }
    var selectedSource by remember { mutableStateOf(EarthquakeSourceFilter.ALL) }

    val filteredList = earthquakes.filter { eq ->
        val matchesQuery = eq.location.contains(searchQuery, ignoreCase = true) ||
                eq.region.contains(searchQuery, ignoreCase = true)
        val matchesMag = eq.magnitude >= minMagnitudeFilter
        val matchesSource = when (selectedSource) {
            EarthquakeSourceFilter.ALL -> true
            EarthquakeSourceFilter.AFAD -> eq.source.contains("AFAD", ignoreCase = true)
            EarthquakeSourceFilter.KANDILLI -> eq.source.contains("Kandilli", ignoreCase = true)
        }
        matchesQuery && matchesMag && matchesSource
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Sismik Akış",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Tüm Depremler",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "${filteredList.size} Kayıt",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Şehir veya bölge ara...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Temizle")
                    }
                }
            },
            shape = RoundedCornerShape(18.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Kaynak Filtresi (AFAD / Kandilli / Tümü)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EarthquakeSourceFilter.values().forEach { sourceFilter ->
                FilterChip(
                    selected = selectedSource == sourceFilter,
                    onClick = { selectedSource = sourceFilter },
                    label = {
                        Text(
                            sourceFilter.displayName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    },
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Büyüklük Filtresi
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filterOptions = listOf(0.0 to "Tümü", 3.0 to "3.0+", 4.0 to "4.0+", 5.0 to "5.0+")
            filterOptions.forEach { (mag, label) ->
                FilterChip(
                    selected = minMagnitudeFilter == mag,
                    onClick = { minMagnitudeFilter = mag },
                    label = { Text(label, fontSize = 11.sp) },
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            itemsIndexed(
                items = filteredList,
                key = { _, item -> item.id }
            ) { index, eq ->
                PremiumEarthquakeCard(
                    earthquake = eq,
                    onClick = { onEarthquakeClick(eq) }
                )

                if ((index + 1) % 10 == 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    NativeAdCard()
                    Spacer(modifier = Modifier.height(4.dp))
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
            // Görsel Destekli Kart Resmi
            AsyncImage(
                model = earthquake.cityImageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(54.dp)
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
                Text(
                    text = "${earthquake.region} • ${earthquake.date} ${earthquake.time}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
                Text(
                    text = "Kaynak: ${earthquake.source}",
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