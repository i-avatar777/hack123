@file:JvmName("Popups")
package com.gravitygroup.avangard.utils

import android.R.string.cancel
import android.R.string.ok
import android.content.Context
import android.content.DialogInterface
import android.view.ContextThemeWrapper
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialog
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gravitygroup.avangard.R.style

@Suppress("DataClassPrivateConstructor")
data class PopupBuilder internal constructor(
    private val builder: MaterialAlertDialogBuilder,
    private val context: Context,
    private val id: Int? = 0,
    private val message: CharSequence? = null,
    private val onItem: ((Int) -> Unit)? = null) {

    private var closer: (() -> Unit)? = null

    fun icon(value: Int = android.R.drawable.ic_dialog_alert) = apply {
        builder.setIcon(value)
    }

    fun title(value: Int = android.R.string.dialog_alert_title) = apply {
        builder.setTitle(value)
    }
    fun title(value: CharSequence) = apply {
        builder.setTitle(value)
    }

    @JvmOverloads fun positive(value: CharSequence, call: (() -> Unit)? = null) = apply {
        val callback: () -> Unit = { call?.invoke(); closer?.invoke() }
        builder.setPositiveButton(value, callback.listener())
    }

    @JvmOverloads fun positive(value: Int = ok, call: (() -> Unit)? = null) = apply {
        val callback: () -> Unit = { call?.invoke(); closer?.invoke() }
        builder.setPositiveButton(value, callback.listener())
    }

    fun positive(call: () -> Unit) = apply { builder.setPositiveButton(ok, call.listener()) }

    fun negative(call: () -> Unit) = apply { builder.setNegativeButton(cancel, call.listener()) }

    @JvmOverloads fun neutral(value: CharSequence, call: (() -> Unit)? = null) = apply { builder.setNeutralButton(value, call.listener()) }
    @JvmOverloads fun neutral(value: Int, call: (() -> Unit)? = null) = apply { builder.setNeutralButton(value, call.listener()) }

    @JvmOverloads fun negative(value: CharSequence, call: (() -> Unit)? = null) = apply {
        val callback: () -> Unit = { call?.invoke(); closer?.invoke() }
        builder.setNegativeButton(value, callback.listener())
    }

    @JvmOverloads fun negative(value: Int = cancel, call: (() -> Unit)? = null) = apply {
        val callback: () -> Unit = { call?.invoke(); closer?.invoke() }
        builder.setNegativeButton(value, callback.listener())
    }

    @JvmOverloads fun alert(value: (() -> Unit)? = null): Popup {
        value?.let { builder.setOnDismissListener { it() } }
        return object : Popup {
            private val dialog = builder.create().apply { show() }
            override fun update() = dialog.setMessage(message)
            override fun close() = dialog.dismiss()
        }
    }

    companion object {

        private fun AppCompatDialog.show(builder: PopupBuilder,
                                         alert: AlertDialog,
                                         content: View? = null) {
            if (content != null) setContentView(content)
            builder.closer = this::dismiss
            setOnDismissListener {
                alert.dismiss()
            }
            alert.setOnDismissListener {
                dismiss()
            }
        }
    }
}

interface Popup { fun update(); fun close() }

@JvmOverloads fun Fragment.popup(value: View, call: ((Int) -> Unit)? = null) = popup(arrayOf(value), call)
@JvmOverloads fun Context.popup(value: View, call: ((Int) -> Unit)? = null) = popup(arrayOf(value), call)

fun Fragment.popup(value: Array<out View>, call: ((Int) -> Unit)? = null) = requireContext().popup(value, call)
fun Context.popup(value: Array<out View>, call: ((Int) -> Unit)? = null): PopupBuilder {
    val single = value.size == 1
    val builder = MaterialAlertDialogBuilder(ContextThemeWrapper(this, style.AlertDialogTheme))
    if (single) builder.setView(value[0])
    else builder.setAdapter(object:BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?) = value[position]
        override fun getItem(position: Int) = value[position]
        override fun getItemId(position: Int) = value[position].hashCode().toLong()
        override fun getCount() = value.size
    }, call.listener())
    return PopupBuilder(builder, this, null, null, if (single) null else call)
}

fun Fragment.popup(value: CharSequence) = requireContext().popup(value)
fun Context.popup(value: CharSequence): PopupBuilder {
    val builder = MaterialAlertDialogBuilder(ContextThemeWrapper(this, style.AlertDialogTheme)).setMessage(value)
    return PopupBuilder(builder, this, 0, value)
}

fun Fragment.popup(value: Array<out CharSequence>, call: (Int) -> Unit) = requireContext().popup(value, call)
fun Context.popup(value: Array<out CharSequence>, call: (Int) -> Unit): PopupBuilder {
    val builder = MaterialAlertDialogBuilder(ContextThemeWrapper(this, style.AlertDialogTheme)).setItems(value, call.listener())
    return PopupBuilder(builder, this, null, null, call)
}
@JvmOverloads fun Fragment.popup(value: Int = 0, call: ((Int) -> Unit)? = null) = requireContext().popup(value, call)
@JvmOverloads fun Context.popup(value: Int = 0, call: ((Int) -> Unit)? = null): PopupBuilder {
    val builder = MaterialAlertDialogBuilder(ContextThemeWrapper(this, style.AlertDialogTheme))
    if (value != 0)
        call?.let { builder.setItems(value, it.listener()) }
            ?: run { builder.setMessage(value) }
    return PopupBuilder(builder, this, call?.let { null } ?: value, null, call)
}

private fun (() -> Unit)?.listener() = this?.let { DialogInterface.OnClickListener { _, _, -> it.invoke()} }
private fun ((Int) -> Unit)?.listener() = this?.let { DialogInterface.OnClickListener { _, index, -> it.invoke(index)} }