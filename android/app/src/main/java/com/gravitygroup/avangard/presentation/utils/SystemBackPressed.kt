package com.gravitygroup.avangard.presentation.utils

/**
 * Контракт для обработчика системной кнопки "Назад"
 */
interface SystemBackPressed {

    /**
     * Вызывает логику на необходимом экране по нажатию на кнопку "Назад"
     */
    fun onBackPressed(): Boolean
}