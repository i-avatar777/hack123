package com.gravitygroup.avangard.interactors.yacht

import com.gravitygroup.avangard.interactors.yacht.data.YachtUIModel
import com.gravitygroup.avangard.interactors.yacht.data.YachtUIModel.Companion.toUiModel
import kotlinx.coroutines.flow.collect

class YachtFilterInteractor(
    private val yachtNetworkRepo: YachtNetworkRepo
) {

    suspend fun requestList(
        placeCount: Int,
        typeRent: Int,
        priceFrom: Int,
        priceTo: Int
    ): List<YachtUIModel> {
        var yachtList = emptyList<YachtUIModel>()
        yachtNetworkRepo.requestList(placeCount, typeRent, priceFrom, priceTo)
            .collect { data ->
                yachtList = data.map { it.toUiModel() }
            }
        return yachtList
    }
}