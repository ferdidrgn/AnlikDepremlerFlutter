package com.ferdidrgn.anlikdepremler.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class EmscEarthquakeDto(
    @SerializedName("type") val type: String? = null,
    @SerializedName("features") val features: List<Feature>? = null
) : Serializable {

    data class Feature(
        @SerializedName("type") val type: String? = null,
        @SerializedName("id") val id: String? = null,
        @SerializedName("geometry") val geometry: Geometry? = null,
        @SerializedName("properties") val properties: Properties? = null
    ) : Serializable

    data class Geometry(
        @SerializedName("type") val type: String? = null,
        @SerializedName("coordinates") val coordinates: List<Double>? = null // [longitude, latitude, depth]
    ) : Serializable

    data class Properties(
        @SerializedName("flynn_region") val flynnRegion: String? = null,
        @SerializedName("time") val time: String? = null,
        @SerializedName("mag") val mag: Double? = null,
        @SerializedName("magtype") val magType: String? = null,
        @SerializedName("depth") val depth: Double? = null,
        @SerializedName("source_id") val sourceId: String? = null
    ) : Serializable
}