package com.gravitygroup.avangard.core.network.base

interface Transformable<T> {

    fun transform(): T
}

fun <T> List<Transformable<T>>.transform(): List<T> =
    map { it.transform() }