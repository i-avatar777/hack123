package com.gravitygroup.avangard.utils

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout.LayoutParams
import androidx.annotation.IdRes
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gravitygroup.avangard.R

/** Быстрый, простой, меленький и очень удобный BottomSheet-Builder */
@Suppress("DataClassPrivateConstructor")
data class BottomSheet internal constructor(val context: Context) {

    private var header: View? = null
    private var items: Array<out View>? = null
    private var closeByClick: Boolean = false
    private var closeButtonId: Int = View.NO_ID
    private var closeButtonContentDescription: String = context.getString(R.string.blind_close)
    private var onDismissed: (() -> Unit)? = null

    /**
     * @param value вью заголовка. Это - та часть контента,
     *              которая будет фиксироваться к верхней границе экрана при полном развороте диалога.
     *              Наиболее частые случаи - toolbar, различного рода заголовки, и иные header-контейнеры.
     *
     * @return BottomSheet инстанс, для сохранения "цепочки"
     */
    fun header(value: View) = apply { header = value }

    /**
     * @param value массив вью элементов списка. Различного рода кнопки, пункты меню, действия и тд.
     *
     * @return BottomSheet инстанс, для сохранения "цепочки"
     */
    fun items(value: Array<out View>) = apply { items = value.toMutableList().toTypedArray() }

    /**
     * Флаг определяет закрытие диалога по клику на любой элемент списка.
     *
     * @return BottomSheet инстанс, для сохранения "цепочки"
     */
    fun closeByClick() = apply { closeByClick = true }

    /**
     * @param value id кнопки "закрыть" на панели с заголовком
     *              Может быть указано если в header-view присутствует кнопка "X"
     *
     * @return BottomSheet инстанс, для сохранения "цепочки"
     */
    fun closeButtonId(@IdRes value: Int) = apply { closeButtonId = value }

    /**
     * @param value описание контента кнопки "закрыть" для озвучки
     *
     * @return BottomSheet инстанс, для сохранения "цепочки"
     */
    fun closeButtonContentDescription(value: String) = apply { closeButtonContentDescription = value }

    /**
     * @param value функция обработчик закрытия диалога,
     *              всегда отработает по факту закрытия окна, вне зависимости от того,
     *              как это закрытие было иницировано (back-pressed, swipe-to-dismiss,
     *              touch-outside, close-button, выбор пункта из списка или программное закрытие)
     *
     *
     * @return BottomSheet инстанс, для сохранения "цепочки"
     */
    fun onDismissed(value: () -> Unit) = apply { onDismissed = value }

    /**
     * Отображает сконфигурированый диалог на экране.
     *
     * @param click обработчик кликов по элементам списка.
     *              Возвращает порядковый номер выбранного элемента
     *              Если указан, каждый view-элемент будет также снабжён специальным
     *              selectableItemBackground, чтобы дать адекватный touch-feedback пользователю.
     *
     * @return функция закрытия этого диалога (onDismissed будет отработан в любом случае)
     */
    fun show(click: ((Int) -> Unit)? = null): () -> Unit {
        return BottomSheetDialog(context).apply {

            val layout =
                ViewGroup.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT
                )
            val background = R.color.yachtButtonPressed
//            val background =
//                context.theme
//                    .resolveAttribute(BACKGROUND)
//                    .resourceId

            items?.forEach {
                it.layoutParams = layout
                it.setBackgroundResource(background)
            }

            if (closeButtonId != View.NO_ID) {
                header?.findViewById<View>(closeButtonId)?.apply {
                    contentDescription = closeButtonContentDescription
                    setOnClickListener { cancel() }
                }
            }

            setContentView(LinearLayoutCompat(context).apply {
                orientation = LinearLayoutCompat.VERTICAL
                addView(header, layout)
                addView(RecyclerView(context).apply {
                    layoutManager = LinearLayoutManager(context).also {
                        it.orientation = LinearLayoutManager.VERTICAL
                    }
                    adapter = SelectableAdapter(onItemClicked = {
                        if (closeByClick) cancel()
                        click?.invoke(it)
                    }).apply {
                        submitList(items?.toList())
                    }
                    addItemDecoration(FooterItemDecoration(context.resources.getDimensionPixelOffset(R.dimen.size_more_extra_large)))
                }, layout)
            }, layout)

            onDismissed?.let {
                setOnDismissListener {
                    setOnDismissListener(null)
                    it()
                }
            }

            show()
        }::cancel
    }

    companion object {

        private val BACKGROUND = R.attr.selectableItemBackground
    }
}

fun Context.bottomSheet() = BottomSheet(this)

private class SelectableAdapter(
    diffCallback: ItemCallback<View> = DiffCallback(),
    private val onItemClicked: (Int) -> Unit
) : ListAdapter<View, ViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //viewType == position
        return object : ViewHolder(getItem(viewType)) {
            init {
                itemView.setOnClickListener {
                    onItemClicked.invoke(adapterPosition)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    }

    companion object {
        private class DiffCallback : ItemCallback<View>() {

            override fun areItemsTheSame(oldItem: View, newItem: View): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: View, newItem: View): Boolean {
                return false
            }
        }
    }
}

/**
 * Декоратор для создания отступа снизу от последнего элемента списка
 *
 * @property height высота отступа
 */
private class FooterItemDecoration(
    private val height: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val adapter = parent.adapter
        if (adapter != null && parent.getChildAdapterPosition(view) == adapter.itemCount - 1) {
            outRect.bottom = height
        }
    }
}


