package com.gravitygroup.avangard.interactors.orders.repository

import com.gravitygroup.avangard.core.network.base.CachePolicy
import com.gravitygroup.avangard.domain.orders.GeoPointData
import com.gravitygroup.avangard.interactors.orders.MapApi
import com.gravitygroup.avangard.core.network.base.CacheEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class MapGeoCodeRepository(
    private val mapApi: MapApi
) {

    private val latLonCache = mutableMapOf<String, CacheEntry<Pair<String, String>>>()

    suspend fun getGeoPoint(addressPair: Pair<Long, String>, cachePolicy: CachePolicy): Flow<GeoPointData> {
        val (id, address) = addressPair
        return flow {
            emit(
                when (cachePolicy) {
                    CachePolicy.Always -> {
                        if (latLonCache.containsKey(address)) {
                            GeoPointData(
                                id,
                                latLonCache[address]!!.value
                            )
                        } else {
                            fetchAndCache(id, address)
                        }
                    }
                    else -> fetchAndCache(id, address)
                }
            )
        }
    }

    private suspend fun fetchAndCache(id: Long, address: String) = try {
        mapApi.addressGeocode(address)
            .response.let {
                val listPoints =
                    it?.geoObjectCollection?.featureMember?.first()?.geoObject?.point?.pos?.split(
                        DELIMITER_GEO_PARAMS
                    )
                val geoPointData = if (listPoints?.isNotEmpty() == true && listPoints.size == 2) {
                    val latLon = listPoints.last() to listPoints.first()
                    latLonCache[address] = CacheEntry(address, latLon)
                    GeoPointData(
                        id,
                        latLon
                    )
                } else {
                    GeoPointData(
                        id,
                        DEFAULT_POINT to DEFAULT_POINT
                    )
                }
                geoPointData
            }
    } catch (e: Exception) {
        Timber.tag(TAG).e("error at get geoPoint with $e")
        GeoPointData.empty()
    }

    companion object {

        private const val TAG = "MAP_GEO_CODE_REPO"

        private const val DEFAULT_POINT = "-"

        private const val DELIMITER_GEO_PARAMS = " "
    }
}