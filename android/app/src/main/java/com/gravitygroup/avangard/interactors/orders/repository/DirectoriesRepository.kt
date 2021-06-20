package com.gravitygroup.avangard.interactors.orders.repository

import com.gravitygroup.avangard.core.ext.handleResultWithError
import com.gravitygroup.avangard.core.network.base.CachePolicy
import com.gravitygroup.avangard.core.network.base.CachePolicy.Expire
import com.gravitygroup.avangard.domain.orders.TypeTechItem.Companion.toUIModel
import com.gravitygroup.avangard.core.network.base.CacheEntry
import com.gravitygroup.avangard.presentation.order_info.data.DirectoryUIModel
import timber.log.Timber

class DirectoriesRepository(
    private val remoteRepo: RemoteOrdersRepository,
    private val localRepo: LocalDataSource<Any, DirectoryUIModel>
) {

    suspend fun getDirectories(cachePolicy: CachePolicy): DirectoryUIModel {
        val dir =  when (cachePolicy) {
            is Expire -> {
                if (localRepo.has(DIR_KEY) &&
                    localRepo.get(DIR_KEY)!!.createdAt + cachePolicy.expires > System.currentTimeMillis()
                ) {
                    localRepo.get(DIR_KEY)!!.value
                } else {
                    fetchAndCache(DIR_KEY)
                }
            }
            // Refresh
            else -> fetchAndCache(DIR_KEY)
        }
        return dir
    }

    private suspend fun fetchAndCache(key: String): DirectoryUIModel {
        var dirValue: DirectoryUIModel? = null
            remoteRepo.getDirectory()
                .handleResultWithError({ result ->
                        result.data?.also {
                            dirValue = it.toUIModel()
                            localRepo.set(key, CacheEntry(key = DIR_KEY, value = it.toUIModel()))
                        }
                    }, {
                        Timber.e("getDirectoryError: $it")
                        dirValue = localRepo.get(DIR_KEY)?.value ?: DirectoryUIModel.emptyUIModel()
                    })
        return dirValue!!
    }

    companion object {

        private const val DIR_KEY = "DIR_KEY"
    }
}