package com.ferdidrgn.anlikdepremler.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
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

    val defaultLocation = LatLng(39.92077, 32.85411)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 6f)
    }

    var selectedEarthquakeId by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    val lazyListState = rememberLazyListState()

    val mapList: List<Earthquake> = remember(uiState.earthquakes, searchQuery) {
        uiState.earthquakes.filter {
            it.location.contains(searchQuery, ignoreCase = true) ||
                    it.region.contains(searchQuery, ignoreCase = true)
        }
    }

    val clusterItems: List<EarthquakeMapItem> = remember(mapList) {
        mapList.map { EarthquakeMapItem(it) }
    }

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
        // 1. GOOGLE MAPS COMPOSER & CUSTOM MARKERS
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

        // 2. ÜST YÜZEN ARAMA PANELİ
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
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
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

        // 3. ALT DEPREM KARTLARI CAROUSEL
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

// 📍 LÜKS NEOMORFİK HARİTA PİNİ
@Composable
fun CustomMarkerBadge(
    magnitude: Double,
    isSelected: Boolean
) {
    val (baseColor, accentColor) = when {
        magnitude >= 5.0 -> Color(0xFFE53935) to Color(0xFFFF5252)
        magnitude >= 3.5 -> Color(0xFFFB8C00) to Color(0xFFFFB74D)
        else -> Color(0xFF43A047) to Color(0xFF81C784)
    }

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.3f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "markerScale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val waveScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 2.0f,
        animationSpec = infiniteRepeatable(tween(1300, easing = LinearEasing), RepeatMode.Restart),
        label = "waveScale"
    )
    val waveAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(tween(1300, easing = LinearEasing), RepeatMode.Restart),
        label = "waveAlpha"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .wrapContentSize(),
        contentAlignment = Alignment.Center
    ) {
        if (magnitude >= 3.5 || isSelected) {
            Box(
                modifier = Modifier
                    .size(52.dp * waveScale)
                    .clip(CircleShape)
                    .background(baseColor.copy(alpha = waveAlpha))
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(y = (-10).dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color.Transparent,
                shadowElevation = if (isSelected) 10.dp else 4.dp
            ) {
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 46.dp else 38.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(accentColor, baseColor)
                            ),
                            shape = CircleShape
                        )
                        .border(
                            width = if (isSelected) 3.dp else 2.dp,
                            color = if (isSelected) Color(0xFFFFD700) else Color.White,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = String.format("%.1f", magnitude),
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = if (isSelected) 15.sp else 13.sp,
                        letterSpacing = (-0.5).sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .offset(y = (-3).dp)
                    .size(width = 10.dp, height = 8.dp)
                    .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
                    .background(baseColor)
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)
                    )
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