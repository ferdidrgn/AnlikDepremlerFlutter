package com.ferdidrgn.anlikdepremler.ui.screen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.billingclient.api.*
import com.ferdidrgn.anlikdepremler.R
import com.ferdidrgn.anlikdepremler.core.language.AppLanguage
import com.ferdidrgn.anlikdepremler.core.util.ReviewHelper
import com.ferdidrgn.anlikdepremler.ui.components.NativeAdCard
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    onOpenLegalDocument: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val currentLang by settingsViewModel.currentLanguage.collectAsState(initial = AppLanguage.TURKISH)

    var showLanguageDialog by remember { mutableStateOf(false) }

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
            .padding(horizontal = 18.dp)
            .padding(top = 16.dp, bottom = 100.dp)
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            fontSize = 30.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // 1. TERCİHLER
        SettingsCategoryTitle("TERCİHLER")
        SettingsCardContainer {
            ModernSettingsTile(
                icon = Icons.Default.Language,
                iconBgColor = Color(0xFF2196F3),
                title = stringResource(R.string.select_language),
                valueText = "${currentLang.flag} ${currentLang.displayName}",
                onClick = { showLanguageDialog = true }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 2. BİLDİRİM VE İZİNLER
        SettingsCategoryTitle("BİLDİRİMLER & İZİNLER")
        SettingsCardContainer {
            ModernSettingsTile(
                icon = Icons.Default.Notifications,
                iconBgColor = Color(0xFFFF9800),
                title = "Bildirim Ayarları",
                subtitle = "Deprem uyarısı tercihlerini yönetin",
                onClick = { settingsViewModel.onNotificationSettingsClick() }
            )
            DividerLine()
            ModernSettingsTile(
                icon = Icons.Default.LocationOn,
                iconBgColor = Color(0xFF4CAF50),
                title = "Konum İzinleri",
                subtitle = "Yakındaki sarsıntıları görmek için gereklidir",
                onClick = { settingsViewModel.onLocationSettingsClick() }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 3. DESTEK VE İLETİŞİM
        SettingsCategoryTitle("DESTEK & TOPLULUK")
        SettingsCardContainer {
            ModernSettingsTile(
                icon = Icons.Default.LocalCafe,
                iconBgColor = Color(0xFF795548),
                title = stringResource(R.string.buy_coffee),
                subtitle = "Geliştiriciye destek olun",
                onClick = { settingsViewModel.onBuyCoffeeClick() }
            )
            DividerLine()
            ModernSettingsTile(
                icon = Icons.Default.Star,
                iconBgColor = Color(0xFFFFC107),
                title = stringResource(R.string.rate_app),
                subtitle = "Google Play Store'da değerlendirin",
                onClick = { settingsViewModel.onRateAppClick() }
            )
            DividerLine()
            ModernSettingsTile(
                icon = Icons.Default.Share,
                iconBgColor = Color(0xFF9C27B0),
                title = "Uygulamayı Paylaş",
                subtitle = "Aileniz ve sevdiklerinizle paylaşın",
                onClick = { settingsViewModel.onShareAppClick() }
            )
            DividerLine()
            ModernSettingsTile(
                icon = Icons.Default.Email,
                iconBgColor = Color(0xFF00BCD4),
                title = "Geri Bildirim Gönder",
                subtitle = "Hata ve önerilerinizi iletin",
                onClick = { settingsViewModel.onFeedbackClick() }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 4. BİLGİ VE YASAL HAKLAR
        SettingsCategoryTitle("YASAL & HAKKINDA")
        SettingsCardContainer {
            ModernSettingsTile(
                icon = Icons.Default.PrivacyTip,
                iconBgColor = Color(0xFF607D8B),
                title = "Gizlilik Politikası",
                subtitle = "Firebase verileri ve gizlilik metni",
                onClick = { onOpenLegalDocument("privacy_policy") }
            )
            DividerLine()
            ModernSettingsTile(
                icon = Icons.Default.Gavel,
                iconBgColor = Color(0xFF3F51B5),
                title = "Kullanıcı Sözleşmesi",
                subtitle = "Kullanım şartları ve koşullar",
                onClick = { onOpenLegalDocument("terms_and_conditions") }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 💰 EN YÜKSEK KAZANÇLI KART İÇİ NATIVE REKLAM (KULLANICIYI BOĞMAZ)
        NativeAdCard()
    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.select_language),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    AppLanguage.values().forEach { language ->
                        val isSelected = language == currentLang
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                .clickable {
                                    settingsViewModel.onLanguageSelected(language)
                                    showLanguageDialog = false
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(language.flag, fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                language.displayName,
                                modifier = Modifier.weight(1f),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            if (isSelected) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showLanguageDialog = false }) { Text("İptal") }
            }
        )
    }
}

@Composable
private fun SettingsCategoryTitle(title: String) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        letterSpacing = 1.2.sp,
        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
    )
}

@Composable
private fun SettingsCardContainer(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            content()
        }
    }
}

@Composable
private fun ModernSettingsTile(
    icon: ImageVector,
    iconBgColor: Color,
    title: String,
    subtitle: String? = null,
    valueText: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(iconBgColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconBgColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (valueText != null) {
            Text(
                text = valueText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 6.dp)
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun DividerLine() {
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
        modifier = Modifier.padding(start = 68.dp)
    )
}

private fun sendEmailIntent(context: Context, email: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:$email")
        putExtra(Intent.EXTRA_SUBJECT, "Anlık Depremler İletişim")
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
            "Anlık Depremler Uygulaması: https://play.google.com/store/apps/details?id=${context.packageName}"
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
                        consumeCoffeePurchase(billingClient, context, purchase)
                    }
                }
            }
        }
        .enablePendingPurchases()
        .build()

    billingClient.startConnection(object : BillingClientStateListener {
        override fun onBillingServiceDisconnected() {}
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
                    }
                }
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
        }
    }
}