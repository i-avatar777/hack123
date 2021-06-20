package com.gravitygroup.avangard.core.delegates

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import androidx.lifecycle.DefaultLifecycleObserver
import java.lang.Exception

class FragmentViewBindingDelegate<T : ViewBinding>(
    private val fragment: Fragment,
    val viewBindingFactory: (View) -> T
) : ReadOnlyProperty<Fragment, T?> {

    private var binding: T? = null

    init {
        fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                fragment.viewLifecycleOwnerLiveData.observe(fragment) { viewLifecycleOwner ->
                    viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                        override fun onDestroy(owner: LifecycleOwner) {
                            binding = null
                        }
                    })
                }
            }
        })
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T? {
        if (binding != null) {
            return binding
        }
        val viewLifecycleOwner = try {
            fragment.viewLifecycleOwner
        } catch (e: Exception) {
            return null
        }
        val lifecycle = viewLifecycleOwner.lifecycle
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            return null
        }
        return viewBindingFactory(thisRef.requireView()).also { this.binding = it }
    }
}