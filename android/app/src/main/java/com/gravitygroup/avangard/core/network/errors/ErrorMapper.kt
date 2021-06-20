package com.gravitygroup.avangard.core.network.errors

import com.gravitygroup.avangard.core.network.base.BaseResponseObj

interface ErrorMapper {

    fun <T, D : BaseResponseObj<T>> mapError(error: Throwable, data: D? = null): Throwable
}
