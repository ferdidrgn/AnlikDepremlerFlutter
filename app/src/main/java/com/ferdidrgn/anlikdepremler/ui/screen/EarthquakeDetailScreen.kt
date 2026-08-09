package com.ferdidrgn.anlikdepremler.ui.screen

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ferdi.deprem.model.Earthquake
import com.ferdidrgn.anlikdepremler.R
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun EarthquakeDetailScreen(
    earthquake: Earthquake,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var isWhistleBlowing by remember { mutableStateOf(false) }

    DisposableEffect(isWhistleBlowing) {
        var toneGenerator: ToneGenerator? = null
        if (isWhistleBlowing) {
            toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 100)
            toneGenerator.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK, 10000)
        }
        onDispose {
            toneGenerator?.stopTone()
            toneGenerator?.release()
        }
    }

    val magnitudeColor = when {
        earthquake.magnitude >= 5.0 -> Color(0xFFD32F2F)
        earthquake.magnitude >= 3.5 -> Color(0xFFE65100)
        else -> Color(0xFF2E7D32)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // 1. HARİTA ÜST HEADER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        ) {
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(
                    LatLng(earthquake.latitude, earthquake.longitude), 9f
                )
            }

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(zoomControlsEnabled = false)
            ) {
                Circle(
                    center = LatLng(earthquake.latitude, earthquake.longitude),
                    radius = (earthquake.magnitude * 7500),
                    fillColor = magnitudeColor.copy(alpha = 0.25f),
                    strokeColor = magnitudeColor,
                    strokeWidth = 3f
                )
                Marker(
                    state = MarkerState(
                        position = LatLng(
                            earthquake.latitude,
                            earthquake.longitude
                        )
                    ),
                    title = earthquake.location
                )
            }

            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(16.dp)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f), CircleShape)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            // 2. BÖLGE RESMİ & LOKASYON KART
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = earthquake.cityImageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = earthquake.location,
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${earthquake.region} • ${earthquake.date} ${earthquake.time}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(magnitudeColor, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = String.format("%.1f", earthquake.magnitude),
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. MODELDEKİ ALANLAR
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DetailInfoTile(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.label_depth),
                    value = "${earthquake.depth} km",
                    icon = Icons.Default.Layers
                )
                DetailInfoTile(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.label_perceived_intensity),
                    value = "Mercalli ${earthquake.intensity}",
                    icon = Icons.Default.GraphicEq
                )
                DetailInfoTile(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.label_source),
                    value = earthquake.source,
                    icon = Icons.Default.Sensors
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. HAYAT KURTARICI DÜDÜĞÜ
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.whistle_title),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = stringResource(R.string.whistle_desc),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                        )
                    }

                    Button(
                        onClick = { isWhistleBlowing = !isWhistleBlowing },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isWhistleBlowing) Color.Black else MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(if (isWhistleBlowing) stringResource(R.string.whistle_stop) else stringResource(R.string.whistle_start))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 5. PAYLAŞ BUTONU
            OutlinedButton(
                onClick = { shareEarthquakeDetail(context, earthquake) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.share_earthquake_info))
            }
        }
    }
}

@Composable
private fun DetailInfoTile(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

private fun shareEarthquakeDetail(context: Context, earthquake: Earthquake) {
    val text = context.getString(
        R.string.share_template,
        earthquake.location,
        earthquake.region,
        earthquake.magnitude,
        earthquake.depth,
        earthquake.date,
        earthquake.time,
        earthquake.id
    )

    val intent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_earthquake_info)))
}