package com.gravitygroup.avangard.core.delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnAttach
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class CustomViewBindingDelegate<out T : ViewBinding>(
    val view: View,
    val viewBindingFactory: (LayoutInflater, ViewGroup) -> T,
    ignoreLifecycle: Boolean = false
) : ReadOnlyProperty<View, T> {

    private var binding: T? = null

    init {
        if (ignoreLifecycle.not()) {
            view.doOnAttach {
                val lifecycleOwner = checkNotNull(view.findViewTreeLifecycleOwner()) {
                    "View: $view does not appear to have a lifecycleOwner"
                }
                lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                    override fun onDestroy(owner: LifecycleOwner) {
                        super.onDestroy(owner)
                        binding = null
                    }
                })
            }
        }
    }

    override fun getValue(thisRef: View, property: KProperty<*>): T {
        val binding = binding
        if (binding != null) {
            return binding
        }
        return viewBindingFactory(
            LayoutInflater.from(thisRef.context),
            thisRef as ViewGroup
        ).also { this@CustomViewBindingDelegate.binding = it }
    }
}
