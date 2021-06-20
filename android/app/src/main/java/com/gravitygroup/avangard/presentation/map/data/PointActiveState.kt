package com.gravitygroup.avangard.presentation.map.data

sealed class PointActiveState {

    object Base: PointActiveState()

    object Active: PointActiveState()

    object Inactive: PointActiveState()

    object Selected: PointActiveState()
}