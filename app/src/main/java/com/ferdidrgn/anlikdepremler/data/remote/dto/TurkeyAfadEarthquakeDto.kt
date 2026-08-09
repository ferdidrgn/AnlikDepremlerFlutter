package com.ferdidrgn.anlikdepremler.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TurkeyAfadEarthquakeDto(
    @SerializedName("rms") val rms: String? = null,
    @SerializedName("eventID") val eventID: String? = null,
    @SerializedName("location") val location: String? = null,
    @SerializedName("latitude") val latitude: String? = null,
    @SerializedName("longitude") val longitude: String? = null,
    @SerializedName("depth") val depth: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("magnitude") val magnitude: String? = null,
    @SerializedName("country") val country: String? = null,
    @SerializedName("province") val province: String? = null,
    @SerializedName("district") val district: String? = null,
    @SerializedName("neighborhood") val neighborhood: String? = null,
    @SerializedName("date") val date: String? = null,
    @SerializedName("isEventUpdate") val isEventUpdate: Boolean? = null,
    @SerializedName("lastUpdateDate") val lastUpdateDate: String? = null
) : Serializable