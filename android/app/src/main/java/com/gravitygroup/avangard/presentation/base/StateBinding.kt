package com.gravitygroup.avangard.presentation.base

import android.os.Bundle
import com.gravitygroup.avangard.presentation.utils.delegates.RenderProp
import kotlin.reflect.KProperty

abstract class StateBinding {

    val delegates = mutableMapOf<String, RenderProp<out Any>>()
    var isInflated = false

    open val afterInflated: (() -> Unit)? = null

    fun onFinishInflate() {
        if (!isInflated) {
            afterInflated?.invoke()
            isInflated = true
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <A, B, C, D> dependsOn(
        vararg fields: KProperty<*>,
        onChange: (A, B, C, D) -> Unit
    ) {
        check(fields.size == 4) { "Names size must be 4, current ${fields.size}" }
        val names = fields.map { it.name }

        names.forEach {
            delegates[it]?.addListener {
                onChange(
                    delegates[names[0]]?.value as A,
                    delegates[names[1]]?.value as B,
                    delegates[names[2]]?.value as C,
                    delegates[names[3]]?.value as D
                )
            }
        }
    }

    fun rebind() {
        delegates.forEach { it.value.bind() }
    }

    abstract fun bind(data: IViewModelState)

    /**
     * override this if need save binding in bundle
     */
    open fun saveUi(outState: Bundle) {
        //empty default implementation
    }

    /**
     * override this if need restore binding from bundle
     */
    open fun restoreUi(savedState: Bundle?) {
        //empty default implementation
    }
}