package com.ferdidrgn.anlikdepremler.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

data class OnboardingItemData(
    val title: String,
    val description: String,
    val imageUrl: String,
    val badgeTitle: String,
    val floatingCardTitle: String,
    val floatingCardSubtitle: String,
    val floatingCardImageUrl: String
)

val onboardingPagesList = listOf(
    OnboardingItemData(
        title = "Sismik Takip\nGüvenli Ellerde",
        description = "Kandilli, AFAD ve USGS verileriyle 7/24 anlık sarsıntı analizi ve canlı fay hattı takibi.",
        // 1. GÖRSEL DÜZELTİLDİ (Çalışan Yüksek Kaliteli Sismograf/Harita Görseli)
        imageUrl = "https://images.unsplash.com/photo-1524661135-423995f22d0b?q=80&w=1200&auto=format&fit=crop",
        badgeTitle = "🔴 CANLI SİSMİK",
        floatingCardTitle = "Son Sarsıntı: Marmara",
        floatingCardSubtitle = "4.2 Mw • 8.5 km derinlik",
        floatingCardImageUrl = "https://picsum.photos/200/200?random=10"
    ),
    OnboardingItemData(
        title = "Erken Uyarı &\nKonum Analizi",
        description = "Bölgenizdeki fay hatlarını inceleyin, 3 saniyede otomatik sorgulama ile güvende kalın.",
        // 2. GÖRSEL DÜZELTİLDİ (Çok Daha Kaliteli ve Anlamlı Akıllı Şehir/Harita Görseli)
        imageUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?q=80&w=1200&auto=format&fit=crop",
        badgeTitle = "📍 AKILLI KONUM",
        floatingCardTitle = "Bölgesel Risk Haritası",
        floatingCardSubtitle = "Yüksek hassasiyetli sismograf",
        floatingCardImageUrl = "https://picsum.photos/200/200?random=20"
    ),
    OnboardingItemData(
        title = "Güvende Kalın,\nHazırlıklı Olun!",
        // 3. SAYFA AÇIKLAMALARI VE METİNLERİ DAHA BÜYÜK VE DİKKAT ÇEKİCİ HALE GETİRİLDİ
        description = "Acil durum çantası kontrol listesi ve hayat kurtaran çök-kapan-tutun rehberleri ile ailenizin güvenliğini şansa bırakmayın.",
        imageUrl = "https://images.unsplash.com/photo-1584036561566-baf8f5f1b144?q=80&w=1200&auto=format&fit=crop",
        badgeTitle = "🎒 AFET BİLİNCİ",
        floatingCardTitle = "Acil Durum Çantası Kontrolü",
        floatingCardSubtitle = "Stok durumu %100 hazır ve güncel",
        floatingCardImageUrl = "https://picsum.photos/200/200?random=30"
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPagesList.size })
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. TAM EKRAN KAYDIRILABİLİR SİNEMATİK ONBOARDING
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            OnboardingFullImagePage(
                data = onboardingPagesList[pageIndex],
                isLastPage = pageIndex == onboardingPagesList.size - 1
            )
        }

        // 2. SABİT ÜST BAR (BRANDING & ATLAMA)
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.Black.copy(alpha = 0.45f)
            ) {
                Text(
                    text = "Sarsıntı Takip",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            Text(
                text = "Atla",
                color = Color.White.copy(alpha = 0.9f),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable { onFinishOnboarding() }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // 3. ALT KONTROL ALANI (SAYFA İNDİKATÖRÜ VE İLERLE BUTONU)
        Row(
            modifier = Modifier
                .navigationBarsPadding()
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sayfa İndikatörleri (Noktalar)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(onboardingPagesList.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(if (isSelected) 28.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) Color.White else Color.White.copy(alpha = 0.4f)
                            )
                    )
                }
            }

            // Yuvarlak İlerleme / Başla Butonu
            FloatingActionButton(
                onClick = {
                    if (pagerState.currentPage < onboardingPagesList.size - 1) {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        onFinishOnboarding()
                    }
                },
                containerColor = Color.White,
                contentColor = Color.Black,
                shape = CircleShape,
                modifier = Modifier.size(60.dp)
            ) {
                Icon(
                    imageVector = if (pagerState.currentPage == onboardingPagesList.size - 1) Icons.Default.Check else Icons.Default.ArrowForward,
                    contentDescription = "İlerle",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun OnboardingFullImagePage(
    data: OnboardingItemData,
    isLastPage: Boolean
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // A. Tam Ekran Yüksek Çözünürlüklü Görsel
        AsyncImage(
            model = data.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // B. Metin Okunabilirliği İçin Dikey Gradyan Karartma Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.35f),
                            Color.Black.copy(alpha = 0.15f),
                            Color.Black.copy(alpha = 0.92f)
                        )
                    )
                )
        )

        // C. Görsel Üzerindeki Tipografi ve Yüzen Kartlar
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(bottom = 105.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            // Rozet (Badge)
            Surface(
                shape = RoundedCornerShape(30.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(
                    text = data.badgeTitle,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            // Ana Başlık (Son sayfada veya kritik anlarda belirgin font)
            Text(
                text = data.title,
                style = MaterialTheme.typography.displayLarge,
                fontSize = if (isLastPage) 38.sp else 34.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                lineHeight = if (isLastPage) 44.sp else 40.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Alt Açıklama Metni (Metin boyutu büyütüldü ve okunabilirlik artırıldı)
            Text(
                text = data.description,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = if (isLastPage) 16.sp else 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.95f),
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // D. Glassmorphic Yüzen Cam Kart
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = Color.White.copy(alpha = 0.18f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = data.floatingCardImageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = data.floatingCardTitle,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = data.floatingCardSubtitle,
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}