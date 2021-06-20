package com.gravitygroup.avangard.domain.orders

data class GeoPointData(
    val id: Long,
    val geoPoint: Pair<String, String>
) {

    companion object {

        private const val ID_DEFAULT = 0L
        private const val GEO_LOCATION_DEFAULT = ""

        fun empty(): GeoPointData =
            GeoPointData(
                ID_DEFAULT,
                GEO_LOCATION_DEFAULT to GEO_LOCATION_DEFAULT
            )
    }
}