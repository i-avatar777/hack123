package com.gravitygroup.avangard.core.network.interceptors

import com.gravitygroup.avangard.core.ext.bodySnapshotUtf8
import com.gravitygroup.avangard.core.network.errors.ErrorBody
import com.gravitygroup.avangard.core.network.errors.RequestError
import com.squareup.moshi.JsonEncodingException
import com.squareup.moshi.Moshi
import okhttp3.Interceptor
import okhttp3.Response
import org.koin.core.KoinComponent

class MapErrorInterceptor(
    private val moshi: Moshi
) : Interceptor, KoinComponent {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val res = chain.proceed(originalRequest)

        val bodyObj: ErrorBody? = try {
            val str = res.bodySnapshotUtf8 ?: ""
//            var listMyData = Types.newParameterizedType(MutableList::class.java, ListYachtResItem::class.java)
//            val adapter: JsonAdapter<List<ListYachtResItem>> =
//                Moshi.Builder().add(KotlinJsonAdapterFactory()).build().adapter(listMyData)
//            adapter.fromJson(str)
            ErrorBody(1, "bla")
        } catch (e: JsonEncodingException) {
            ErrorBody(0, "")
        }

        val errMessage = bodyObj?.message

        if (res.isSuccessful) return res

        // проверка кода ответа
        when (res.code) {
            400 -> throw RequestError.BadRequest(errMessage)
            401 -> throw RequestError.Unauthorized(errMessage)
            403 -> throw RequestError.Forbidden(errMessage)
            404 -> throw RequestError.NotFound(errMessage)
            500 -> throw RequestError.InternalServerError(errMessage)
            else -> throw RequestError.UnknownError(errMessage)
        }
    }
}