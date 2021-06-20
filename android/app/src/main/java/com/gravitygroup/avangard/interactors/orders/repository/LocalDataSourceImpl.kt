package com.gravitygroup.avangard.interactors.orders.repository

import com.gravitygroup.avangard.core.network.base.CacheEntry

class LocalDataSourceImpl<in Key: Any, T> : LocalDataSource<Key, T> {

    private val map = mutableMapOf<Key, CacheEntry<T>>()

    override fun has(key: Key) = map.containsKey(key)

    override fun get(key: Key): CacheEntry<T>? {
        return map[key]
    }

    override fun set(key: Key, value: CacheEntry<T>) {
        map[key] = value
    }

    override fun remove(key: Key) {
        map.remove(key)
    }

    override fun clear() {
        map.clear()
    }
}