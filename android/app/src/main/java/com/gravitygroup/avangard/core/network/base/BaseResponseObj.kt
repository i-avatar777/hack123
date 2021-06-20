package com.gravitygroup.avangard.core.network.base

data class BaseResponseObj<T>(
    val error: String?,
    val code: Int,
    val data: T?
)