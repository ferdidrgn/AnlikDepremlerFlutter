package com.ferdidrgn.anlikdepremler.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class WorldGeneralEarthquakeDto(
    @SerializedName("la") val latitude: String? = null, // Depremin enlemi
    @SerializedName("lo") val longitude: String? = null, // Depremin boylamı
    @SerializedName("de") val depth: String? = null, // Depremin derinliği (km cinsinden)
    @SerializedName("ma") val magnitude: String? = null, // Depremin büyüklüğü
    @SerializedName("mt") val magnitudeType: String? = null, // Büyüklük ölçüm türü (örn: ml, m, mb)
    @SerializedName("p1") val locationCode: String? = null, // Konum kodu
    @SerializedName("it") val isTsunami: String? = null, // Tsunami oluşumu (1 ise evet, 0 ise hayır)
    @SerializedName("pl") val locationName: String? = null, // Depremin meydana geldiği yerin adı
    @SerializedName("pr") val provider: String? = null, // Deprem bilgi sağlayıcısı
    @SerializedName("dt") val dateTime: String? = null, // Depremin tarihi ve saati
    @SerializedName("di") val distance: String? = null, // Depremden bir referans noktasına olan mesafe
    @SerializedName("mr") val isReported: String? = null, // Bildirilen büyüklük durumu (1 ise bildirildi, 0 ise bildirilmedi)
    @SerializedName("py") val isAftershock: String? = null, // Artçı şok oluşumu (1 ise evet, 0 ise hayır)
    @SerializedName("sm") val shakingMap: String? = null, // Sarsıntı haritası (0 ise mevcut değil)
    @SerializedName("rp") val isReviewed: String? = null, // İnceleme durumu (1 ise incelendi, 0 ise incelenmedi)
) : Serializable
