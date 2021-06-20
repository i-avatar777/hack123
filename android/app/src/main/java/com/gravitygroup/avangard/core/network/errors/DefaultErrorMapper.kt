package com.gravitygroup.avangard.core.network.errors

import com.gravitygroup.avangard.core.network.base.BaseResponseObj
import com.gravitygroup.avangard.core.network.base.CODE_ERROR
import com.gravitygroup.avangard.core.network.base.CODE_NO_DATA
import com.gravitygroup.avangard.core.network.base.CODE_TOKEN_ERROR

class DefaultErrorMapper : ErrorMapper {

    override fun <T, D : BaseResponseObj<T>> mapError(error: Throwable, data: D?): Throwable {
        return when (error) {
            is BaseApiError -> handleBaseApiError(error, data)
            is ApiError -> error
            else -> UnknownError()
        }
    }

    private fun <T, D : BaseResponseObj<T>> handleBaseApiError(
        error: BaseApiError,
        data: D?
    ): Throwable {
        val message = error.errorMessage
        val code = error.code
        return when (code) {
            CODE_ERROR -> ApiError.GeneralError(message)
            CODE_NO_DATA -> ApiError.NoDataError(message)
            CODE_TOKEN_ERROR -> ApiError.TokenError(message)
            else -> UnknownError()
        }
    }

}
