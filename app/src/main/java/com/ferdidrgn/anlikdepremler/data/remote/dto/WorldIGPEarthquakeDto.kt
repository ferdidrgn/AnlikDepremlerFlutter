package com.ferdidrgn.anlikdepremler.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class WorldIGPEarthquakeDto(
    @SerializedName("la")
    val latitude: Double? = null,   // Enlem (latitude)
    @SerializedName("lo")
    val longitude: Double? = null,   // Boylam (longitude)
    @SerializedName("de")
    val depth: String? = null,   // Derinlik (depth)
    @SerializedName("ma")
    val magnitude: String? = null,   // Büyüklük (magnitude)
    @SerializedName("mt")
    val magnitudeType: String? = null,  // Deprem türü (magnitude type)
    @SerializedName("p1")
    val p1: String? = null,  // Belirtilen bir kod (belirli bir anlamı olabilir)
    @SerializedName("it")
    val it: String? = null,  // Bilinmiyor (belirtilen kullanım durumu yok)
    @SerializedName("pl")
    val place: String? = null,    // Depremin gerçekleştiği yer (place)
    @SerializedName("pr")
    val provider: String? = null,   // Kaynak (provider)
    @SerializedName("dt")
    val dateTime: String? = null,   // Tarih ve saat (date and time)
    @SerializedName("di")
    val di: String? = null,   // Bilinmiyor (belirtilen kullanım durumu yok)
    @SerializedName("mr")
    val mr: String? = null,   // Bilinmiyor (belirtilen kullanım durumu yok)
    @SerializedName("py")
    val py: String? = null,    // Bilinmiyor (belirtilen kullanım durumu yok)
    @SerializedName("sm")
    val sm: String? = null,   // Bilinmiyor (belirtilen kullanım durumu yok)
    @SerializedName("rp")
    val rp: String? = null,    // Bilinmiyor (belirtilen kullanım durumu yok)
) : Serializable
