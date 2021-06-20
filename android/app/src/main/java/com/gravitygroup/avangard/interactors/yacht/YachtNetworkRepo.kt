package com.gravitygroup.avangard.interactors.yacht

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class YachtNetworkRepo(
    private val api: YachtApi
) {

    suspend fun requestList(
        placeCount: Int,
        typeRent: Int,
        priceFrom: Int,
        priceTo: Int
    ) =
        flow {
            emit(
                api.requestList(
                    placeCount, typeRent, priceFrom, priceTo
                )
            )
        }
            .flowOn(Dispatchers.IO)
}