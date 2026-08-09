package com.ferdidrgn.anlikdepremler.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TurkeyAllEarthquakeDto(
    @SerializedName("status") val status: Boolean? = null,
    @SerializedName("httpStatus") val httpStatus: Int? = null,
    @SerializedName("serverloadms") val serverloadms: Int? = null,
    @SerializedName("desc") val desc: String? = null,
    @SerializedName("metadata") val metadata: Metadata? = null,
    @SerializedName("result") val result: List<Earthquake>? = null
) : Serializable {

    data class Metadata(
        @SerializedName("date_starts") val dateStarts: String? = null,
        @SerializedName("date_ends") val dateEnds: String? = null,
        @SerializedName("total") val total: Int? = null
    )

    data class Earthquake(
        @SerializedName("_id") val id: String? = null,
        @SerializedName("earthquake_id") val earthquakeId: String? = null,
        @SerializedName("provider") val provider: String? = null,
        @SerializedName("title") val title: String? = null,
        @SerializedName("date") val date: String? = null,
        @SerializedName("mag") val mag: Double? = null,
        @SerializedName("depth") val depth: Double? = null,
        @SerializedName("geojson") val geojson: GeoJson? = null,
        @SerializedName("location_properties") val locationProperties: LocationProperties? = null,
        @SerializedName("rev") val rev: String? = null,
        @SerializedName("date_time") val dateTime: String? = null,
        @SerializedName("created_at") val createdAt: Long? = null,
        @SerializedName("location_tz") val locationTz: String? = null
    ) : Serializable

    data class GeoJson(
        @SerializedName("type") val type: String? = null,
        @SerializedName("coordinates") val coordinates: List<Double>? = null
    )

    data class LocationProperties(
        @SerializedName("closestCity") val closestCity: City? = null,
        @SerializedName("epiCenter") val epiCenter: City? = null,
        @SerializedName("closestCities") val closestCities: List<City>? = null,
        @SerializedName("airports") val airports: List<Airport>? = null
    )

    data class City(
        @SerializedName("name") val name: String? = null,
        @SerializedName("cityCode") val cityCode: Int? = null,
        @SerializedName("distance") val distance: Double? = null,
        @SerializedName("population") val population: Int? = null
    )

    data class Airport(
        @SerializedName("distance") val distance: Double? = null,
        @SerializedName("name") val name: String? = null,
        @SerializedName("code") val code: String? = null,
        @SerializedName("coordinates") val coordinates: GeoJson? = null
    )
}