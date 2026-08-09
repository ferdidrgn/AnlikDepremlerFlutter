package com.ferdidrgn.anlikdepremler.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TurkeyKandilliEarthquakeDto(
    @SerializedName("date") val date: String? = null,
    @SerializedName("time") val time: String? = null,
    @SerializedName("location") val location: String? = null,
    @SerializedName("latitude") val latitude: String? = null,
    @SerializedName("longitude") val longitude: String? = null,
    @SerializedName("depth") val depth: String? = null,
    @SerializedName("md") val md: String? = null,
    @SerializedName("ml") val ml: String? = null,
    @SerializedName("mw") val mw: String? = null,
    @SerializedName("revize") val revize: String? = null
) : Serializable