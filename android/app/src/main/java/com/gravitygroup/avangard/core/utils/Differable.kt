package com.gravitygroup.avangard.core.utils

class Differable<T>(initial: T) {

    var value: T = initial
        set(value) {
            if (value != this.value) {
                field = value
                isConsumed = false
            }
        }

    var isConsumed: Boolean = false
}

fun <T> T.differable(): Differable<T> = Differable(this)

fun <T> Nothing?.nullDifferable(): Differable<T?> = Differable(null)

fun <T> Differable<T>.takeIfDifferent(block: (T) -> Unit) {
    if (!isConsumed) {
        block(value)
        isConsumed = true
    }
}

fun <T> Differable<T>.forceSet(newValue: T) {
    value = newValue
    isConsumed = false
}