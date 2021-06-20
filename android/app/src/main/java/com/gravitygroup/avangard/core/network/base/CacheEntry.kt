package com.gravitygroup.avangard.core.network.base

data class CacheEntry<T>(
    val key: String,
    val value: T,
    val createdAt: Long = System.currentTimeMillis()
)