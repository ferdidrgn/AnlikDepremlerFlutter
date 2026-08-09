package com.ferdidrgn.anlikdepremler.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class WorldUSGSEarthquakeDto(
    @SerializedName("type") val type: String? = null,
    @SerializedName("metadata") val metadata: Metadata? = null,
    @SerializedName("features") val features: List<Feature>? = null,
    @SerializedName("bbox") val bbox: List<Double>? = null
) : Serializable {

    data class Metadata(
        @SerializedName("generated") val generated: Long? = null,
        @SerializedName("url") val url: String? = null,
        @SerializedName("title") val title: String? = null,
        @SerializedName("status") val status: Int? = null,
        @SerializedName("api") val api: String? = null,
        @SerializedName("count") val count: Int? = null
    )

    data class Feature(
        @SerializedName("type") val type: String? = null,
        @SerializedName("properties") val properties: Properties? = null,
        @SerializedName("geometry") val geometry: Geometry? = null,
        @SerializedName("id") val id: String? = null
    ) {
        data class Properties(
            @SerializedName("mag") val mag: Double? = null,
            @SerializedName("place") val place: String? = null,
            @SerializedName("time") val time: Long? = null,
            @SerializedName("updated") val updated: Long? = null,
            @SerializedName("url") val url: String? = null,
            @SerializedName("detail") val detail: String? = null,
            @SerializedName("felt") val felt: Int? = null,
            @SerializedName("cdi") val cdi: Double? = null,
            @SerializedName("mmi") val mmi: Double? = null,
            @SerializedName("alert") val alert: String? = null,
            @SerializedName("status") val status: String? = null,
            @SerializedName("tsunami") val tsunami: Byte? = null,
            @SerializedName("sig") val sig: Int? = null,
            @SerializedName("net") val net: String? = null,
            @SerializedName("code") val code: String? = null,
            @SerializedName("ids") val ids: String? = null,
            @SerializedName("sources") val sources: String? = null,
            @SerializedName("types") val types: String? = null,
            @SerializedName("nst") val nst: Int? = null,
            @SerializedName("dmin") val dmin: Double? = null,
            @SerializedName("rms") val rms: Double? = null,
            @SerializedName("gap") val gap: Double? = null,
            @SerializedName("magType") val magType: String? = null,
            @SerializedName("type") val type: String? = null,
            @SerializedName("title") val title: String? = null
        )

        data class Geometry(
            @SerializedName("type") val type: String? = null,
            @SerializedName("coordinates") val coordinates: List<Double>? = null
        )
    }
}