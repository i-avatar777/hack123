package com.gravitygroup.avangard.core.network.errors

class BaseApiError(
    val code: Int,
    val errorMessage: String
) : Throwable()
