package com.gravitygroup.avangard.interactors.orders.repository

import com.gravitygroup.avangard.core.network.base.CacheEntry

interface LocalDataSource<in Key : Any, T> {
    fun has(key: Key): Boolean
    fun get(key: Key): CacheEntry<T>?
    fun set(key: Key, value: CacheEntry<T>)
    fun remove(key: Key)
    fun clear()
}