package com.gravitygroup.avangard.core.network.base

sealed class CachePolicy {

    object Always : CachePolicy()

    object Refresh : CachePolicy()

    data class Expire(
        val expires: Long = 0L // ms
    ) : CachePolicy()
}