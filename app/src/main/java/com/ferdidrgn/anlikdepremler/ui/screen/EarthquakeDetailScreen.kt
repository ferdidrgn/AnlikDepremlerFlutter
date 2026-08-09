package com.ferdidrgn.anlikdepremler.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ferdi.deprem.model.Earthquake

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EarthquakeDetailScreen(
    earthquake: Earthquake,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val (magBgColor, magTextColor) = when {
        earthquake.magnitude >= 5.0 -> Color(0xFFFFEBEE) to Color(0xFFD32F2F)
        earthquake.magnitude >= 3.5 -> Color(0xFFFFF3E0) to Color(0xFFE65100)
        else -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Deprem Detayı", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "⚠️ Sarsıntı Bildirimi:\n${earthquake.location} bölgesinde ${earthquake.magnitude} Mw büyüklüğünde deprem meydana geldi.\nTarih: ${earthquake.date} ${earthquake.time}\nDerinlik: ${earthquake.depth} km"
                            )
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Depremi Paylaş"))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Paylaş")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // 1. Üst Büyüklük Hero Kartı
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = magBgColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = String.format("%.1f", earthquake.magnitude),
                        fontSize = 54.sp,
                        fontWeight = FontWeight.Black,
                        color = magTextColor
                    )
                    Text(
                        text = "Moment Büyüklüğü (Mw)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = magTextColor.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = earthquake.location,
                        style = MaterialTheme.typography.headlineMedium,
                        color = magTextColor,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "${earthquake.region} • ${earthquake.date} ${earthquake.time}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = magTextColor.copy(alpha = 0.9f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Teknik Parametreler Grid Kartı
            Text("📊 Teknik Parametreler", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailRow(label = "Derinlik", value = "${earthquake.depth} km")
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    DetailRow(label = "Hissedilen Şiddet", value = earthquake.intensity)
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    DetailRow(label = "Enlem (Latitude)", value = earthquake.latitude.toString())
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    DetailRow(label = "Boylam (Longitude)", value = earthquake.longitude.toString())
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Haritada Aç Butonu
            Button(
                onClick = {
                    val gmmIntentUri =
                        Uri.parse("geo:${earthquake.latitude},${earthquake.longitude}?q=${earthquake.latitude},${earthquake.longitude}(${earthquake.location})")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    context.startActivity(mapIntent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Map, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Google Haritalar'da Göster", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}