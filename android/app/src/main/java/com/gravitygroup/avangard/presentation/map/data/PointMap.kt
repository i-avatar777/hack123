package com.gravitygroup.avangard.presentation.map.data

import com.yandex.mapkit.geometry.Point

data class PointMap(
    val address: String,
    val geoPoint: Point,
    val activationStatus: PointActiveState
)
