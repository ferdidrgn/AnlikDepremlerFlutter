package com.ferdidrgn.anlikdepremler.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ferdi.deprem.model.Earthquake
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.compose.*
import com.google.maps.android.compose.clustering.Clustering
import kotlinx.coroutines.launch

// 📌 Map Cluster Item Yapısı
data class EarthquakeMapItem(
    val earthquake: Earthquake
) : ClusterItem {
    override fun getPosition(): LatLng = LatLng(earthquake.latitude, earthquake.longitude)
    override fun getTitle(): String = earthquake.location
    override fun getSnippet(): String = "${earthquake.magnitude} Mw"
    override fun getZIndex(): Float? = 0f
}

@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun MapScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // 🗺️ Türkiye Merkezli Kamera Pozisyonu (Ankara)
    val defaultLocation = LatLng(39.92077, 32.85411)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 6f)
    }

    var selectedEarthquakeId by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    val lazyListState = rememberLazyListState()

    // Filtrelenmiş Deprem Listesi
    val mapList: List<Earthquake> = remember(uiState.earthquakes, searchQuery) {
        uiState.earthquakes.filter {
            it.location.contains(searchQuery, ignoreCase = true) ||
                    it.region.contains(searchQuery, ignoreCase = true)
        }
    }

    val clusterItems: List<EarthquakeMapItem> = remember(mapList) {
        mapList.map { EarthquakeMapItem(it) }
    }

    // 📌 Kart Sağa/Sola Kaydırıldığında Kamerayı Depreme Kaydırma
    val firstVisibleIndex by remember { derivedStateOf { lazyListState.firstVisibleItemIndex } }
    LaunchedEffect(firstVisibleIndex) {
        if (mapList.isNotEmpty() && firstVisibleIndex in mapList.indices) {
            val targetEq = mapList[firstVisibleIndex]
            selectedEarthquakeId = targetEq.id
            coroutineScope.launch {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(targetEq.latitude, targetEq.longitude),
                        8.5f
                    ),
                    durationMs = 800
                )
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. GOOGLE MAPS COMPOSER & CUSTOM CLUSTER MARKERS
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false
            )
        ) {
            Clustering(
                items = clusterItems,
                // 🎯 BURADA KENDİ ÖZEL ŞİDDET YAZILI RADYANLI MARKER'IMIZI BAĞLIYORUZ
                clusterItemContent = { item ->
                    val isSelected = item.earthquake.id == selectedEarthquakeId
                    CustomMarkerBadge(
                        magnitude = item.earthquake.magnitude,
                        isSelected = isSelected
                    )
                },
                onClusterItemClick = { clusterItem ->
                    val index = mapList.indexOfFirst { it.id == clusterItem.earthquake.id }
                    if (index != -1) {
                        selectedEarthquakeId = clusterItem.earthquake.id
                        coroutineScope.launch {
                            lazyListState.animateScrollToItem(index)
                        }
                    }
                    true
                }
            )
        }

        // 2. ÜST YÜZEN ARAMA & KONTROL PANELİ
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    onClick = onBackClick,
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    tonalElevation = 6.dp,
                    shadowElevation = 6.dp
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Kapat",
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    tonalElevation = 6.dp,
                    shadowElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Haritada Şehir/Bölge Ara...", fontSize = 13.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // 3. ALT HORIZONTAL DEPREM KARTLARI CAROUSEL
        if (mapList.isNotEmpty()) {
            LazyRow(
                state = lazyListState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 90.dp)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(
                    items = mapList,
                    key = { _, item -> item.id }
                ) { _, eq ->
                    val isSelected = eq.id == selectedEarthquakeId
                    MapEarthquakeCard(
                        earthquake = eq,
                        isSelected = isSelected,
                        onClick = {
                            selectedEarthquakeId = eq.id
                            coroutineScope.launch {
                                cameraPositionState.animate(
                                    CameraUpdateFactory.newLatLngZoom(
                                        LatLng(eq.latitude, eq.longitude),
                                        9.5f
                                    ),
                                    durationMs = 600
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

// 🎨 HARİTA ÜZERİNDEKİ ÖZEL ŞİDDET ROZETİ (CUSTOM MARKER COMPOSABLE)
@Composable
fun CustomMarkerBadge(
    magnitude: Double,
    isSelected: Boolean
) {
    val backgroundColor = when {
        magnitude >= 5.0 -> Color(0xFFD32F2F) // Kırmızı
        magnitude >= 3.5 -> Color(0xFFE65100) // Turuncu
        else -> Color(0xFF2E7D32)             // Yeşil
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = backgroundColor,
        shadowElevation = if (isSelected) 12.dp else 4.dp,
        border = BorderStroke(
            width = if (isSelected) 3.dp else 1.5.dp,
            color = if (isSelected) Color.Yellow else Color.White
        )
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = String.format("%.1f", magnitude),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = if (isSelected) 15.sp else 12.sp
            )
        }
    }
}

@Composable
fun MapEarthquakeCard(
    earthquake: Earthquake,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val magnitudeColor = when {
        earthquake.magnitude >= 5.0 -> Color(0xFFD32F2F)
        earthquake.magnitude >= 3.5 -> Color(0xFFE65100)
        else -> Color(0xFF2E7D32)
    }

    Card(
        modifier = Modifier
            .width(280.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 4.dp),
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(magnitudeColor.copy(alpha = 0.15f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = String.format("%.1f", earthquake.magnitude),
                    color = magnitudeColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = earthquake.location,
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${earthquake.time} • Derinlik: ${earthquake.depth} km",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}