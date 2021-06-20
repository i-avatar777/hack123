package com.gravitygroup.avangard.presentation.utils.delegates

import com.gravitygroup.avangard.presentation.base.StateBinding
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class RenderProp<T : Any>(
    var value: T,
    private val needInit: Boolean = true,
    private val onChange: ((T) -> Unit)? = null
) : ReadWriteProperty<StateBinding, T> {

    private val listeners: MutableList<() -> Unit> = mutableListOf()

    fun bind() {
        if (needInit) onChange?.invoke(value)
    }

    operator fun provideDelegate(
        thisRef: StateBinding,
        prop: KProperty<*>
    ): ReadWriteProperty<StateBinding, T> {
        val delegate = RenderProp(value, needInit, onChange)
        registerDelegate(thisRef, prop.name, delegate)
        return delegate
    }

    override fun getValue(thisRef: StateBinding, property: KProperty<*>): T = value

    override fun setValue(thisRef: StateBinding, property: KProperty<*>, value: T) {
        if (value == this.value) return
        this.value = value
        onChange?.invoke(this.value)
        if (listeners.isNotEmpty()) listeners.forEach { it.invoke() }
    }

    fun addListener(listener: () -> Unit) {
        listeners.add(listener)
    }

    private fun registerDelegate(thisRef: StateBinding, name: String, delegate: RenderProp<T>) {
        thisRef.delegates[name] = delegate
    }
}