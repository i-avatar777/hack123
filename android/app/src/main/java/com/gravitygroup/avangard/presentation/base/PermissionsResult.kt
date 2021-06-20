package com.gravitygroup.avangard.presentation.base

data class PermissionsResult(
    val permissions: Map<String, Pair<Boolean, Boolean>>, // permission id to pair of <isGranted, canShowRationale>
    val allGranted: Boolean = false,
    val canRequestAgain: Boolean = true
)