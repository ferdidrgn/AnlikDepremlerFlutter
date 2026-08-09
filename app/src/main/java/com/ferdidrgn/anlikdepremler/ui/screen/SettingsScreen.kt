package com.ferdidrgn.anlikdepremler.ui.screen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ferdidrgn.anlikdepremler.core.language.AppLanguage
import com.ferdidrgn.anlikdepremler.core.util.ReviewHelper
import android.widget.Toast
import com.android.billingclient.api.*
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SettingsScreen(
    mainViewModel: MainViewModel,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by mainViewModel.uiState.collectAsState()
    val currentLang by settingsViewModel.currentLanguage.collectAsState()

    var showLanguageDialog by remember { mutableStateOf(false) }

    // Event Dinleyicisi
    LaunchedEffect(Unit) {
        settingsViewModel.eventFlow.collectLatest { event ->
            when (event) {
                is SettingsEvent.SendEmail -> sendEmailIntent(context, event.email)
                is SettingsEvent.OpenNotificationSettings -> openNotificationSettings(context)
                is SettingsEvent.OpenLocationSettings -> openLocationSettings(context)
                is SettingsEvent.RequestReview -> ReviewHelper.launchInAppReview(context)
                is SettingsEvent.ShareApp -> shareApp(context)
                is SettingsEvent.NavigateToWeb -> openWebPage(context, event.url)
                is SettingsEvent.BuyCoffee -> launchCoffeeDonationFlow(context, event.productId)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 80.dp)
    ) {
        Text(
            text = "Ayarlar",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        // 1. DİL SEÇİMİ VE TEMA
        SettingsSectionTitle("🎨 Görünüm & Dil")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                SettingsActionTile(
                    icon = Icons.Default.Language,
                    title = "Uygulama Dili",
                    subtitle = "${currentLang.flag} ${currentLang.displayName}",
                    onClick = { showLanguageDialog = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. İZİNLER
        SettingsSectionTitle("🔔 İzinler")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                SettingsActionTile(
                    icon = Icons.Default.Notifications,
                    title = "Bildirim Ayarları",
                    subtitle = "Anlık bildirim tercihleri",
                    onClick = { settingsViewModel.onNotificationPermissionClick() }
                )
                HorizontalDivider()
                SettingsActionTile(
                    icon = Icons.Default.LocationOn,
                    title = "Konum İzinleri",
                    subtitle = "Bölgesel deprem analizi için",
                    onClick = { settingsViewModel.onLocationPermissionClick() }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. DESTEK VE İLETİŞİM
        SettingsSectionTitle("☕ Destek & İletişim")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                SettingsActionTile(
                    icon = Icons.Default.LocalCafe,
                    title = "Geliştiriciye Kahve Ismarla",
                    subtitle = "Uygulama gelişimini destekleyin",
                    onClick = { settingsViewModel.onBuyCoffeeClick() }
                )
                HorizontalDivider()
                SettingsActionTile(
                    icon = Icons.Default.Email,
                    title = "Bize Ulaşın",
                    subtitle = "Görüş ve önerilerinizi iletin",
                    onClick = { settingsViewModel.onContactUsClick() }
                )
                HorizontalDivider()
                SettingsActionTile(
                    icon = Icons.Default.Star,
                    title = "Uygulamayı Değerlendirin",
                    subtitle = "Google Play'de puan verin",
                    onClick = { settingsViewModel.onRateAppClick() }
                )
            }
        }
    }

    // Dil Seçim Diyaloğu
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Dil Seçiniz") },
            text = {
                Column {
                    AppLanguage.values().forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    settingsViewModel.onLanguageSelected(lang)
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(lang.flag, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(lang.displayName, fontSize = 15.sp)
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
}

// YARDIMCI İNTENT FONKSİYONLARI (Gelen Kodların Compose Karşılıkları)

private fun sendEmailIntent(context: Context, email: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:$email")
        putExtra(Intent.EXTRA_SUBJECT, "Sarsıntı Takip İletişim")
    }
    context.startActivity(Intent.createChooser(intent, "E-posta Gönder"))
}

private fun openNotificationSettings(context: Context) {
    val intent = Intent().apply {
        action = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Settings.ACTION_APP_NOTIFICATION_SETTINGS
        } else {
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        }
        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    }
    context.startActivity(intent)
}

private fun openLocationSettings(context: Context) {
    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    context.startActivity(intent)
}

private fun openWebPage(context: Context, url: String) {
    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}

private fun shareApp(context: Context) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        putExtra(
            Intent.EXTRA_TEXT,
            "Sarsıntı Takip Uygulaması: https://play.google.com/store/apps/details?id=${context.packageName}"
        )
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, null))
}

private const val DONATION_SMALL = "donation_small"

private fun launchCoffeeDonationFlow(context: Context, productId: String = DONATION_SMALL) {
    val activity = context as? Activity ?: return

    lateinit var billingClient: BillingClient

    billingClient = BillingClient.newBuilder(context)
        .setListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (purchase in purchases) {
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        // Satın alınan kahveyi tüketelim ki tekrar ısmarlayabilsin
                        consumeCoffeePurchase(billingClient, context, purchase)
                    }
                }
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                Toast.makeText(context, "İşlem iptal edildi.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Hata: ${billingResult.debugMessage}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        .enablePendingPurchases()
        .build()

    billingClient.startConnection(object : BillingClientStateListener {
        override fun onBillingServiceDisconnected() {
            Toast.makeText(context, "Google Play servis bağlantısı kesildi.", Toast.LENGTH_SHORT)
                .show()
        }

        override fun onBillingSetupFinished(billingResult: BillingResult) {
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val productList = listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                )

                val params = QueryProductDetailsParams.newBuilder()
                    .setProductList(productList)
                    .build()

                billingClient.queryProductDetailsAsync(params) { result, productDetailsList ->
                    if (result.responseCode == BillingClient.BillingResponseCode.OK && productDetailsList.isNotEmpty()) {
                        val productDetails = productDetailsList.first()
                        val flowParams = BillingFlowParams.newBuilder()
                            .setProductDetailsParamsList(
                                listOf(
                                    BillingFlowParams.ProductDetailsParams.newBuilder()
                                        .setProductDetails(productDetails)
                                        .build()
                                )
                            )
                            .build()

                        billingClient.launchBillingFlow(activity, flowParams)
                    } else {
                        activity.runOnUiThread {
                            Toast.makeText(
                                context,
                                "Ürün detayları alınamadı. Ürün ID'sini Play Console'da kontrol edin.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            } else {
                Toast.makeText(
                    context,
                    "Billing Servis Hatası: ${billingResult.debugMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    })
}

private fun consumeCoffeePurchase(
    billingClient: BillingClient,
    context: Context,
    purchase: Purchase
) {
    val consumeParams = ConsumeParams.newBuilder()
        .setPurchaseToken(purchase.purchaseToken)
        .build()

    billingClient.consumeAsync(consumeParams) { billingResult, _ ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            Toast.makeText(
                context,
                "☕ Destek olduğunuz ve kahve ısmarladığınız için teşekkürler!",
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(
                context,
                "Tüketim Hatası: ${billingResult.debugMessage}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

@Composable
private fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
    )
}

@Composable
private fun SettingsActionTile(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontSize = 14.sp)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null)
    }
}