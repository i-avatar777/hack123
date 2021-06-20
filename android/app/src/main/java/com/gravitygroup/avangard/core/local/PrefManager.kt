package com.gravitygroup.avangard.core.local

import android.content.SharedPreferences

interface PrefManager {

    val preferences: SharedPreferences

    companion object {

        const val STRING_DEFAULT = ""
    }
}