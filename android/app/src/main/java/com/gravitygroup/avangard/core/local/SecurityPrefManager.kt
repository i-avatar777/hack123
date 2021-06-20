package com.gravitygroup.avangard.core.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.security.crypto.MasterKey.Builder
import androidx.security.crypto.MasterKey.KeyScheme.AES256_GCM
import com.gravitygroup.avangard.core.delegates.PrefDelegate
import com.gravitygroup.avangard.core.local.PrefManager.Companion.STRING_DEFAULT

class SecurityPrefManager(private val context: Context): PrefManager {

    private val masterKey = Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
        .setKeyScheme(AES256_GCM)
        .build()

    override val preferences: SharedPreferences =
        EncryptedSharedPreferences.create(
            context, SP_FILE,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    var token by PrefDelegate(STRING_DEFAULT)
    var login by PrefDelegate(STRING_DEFAULT)


    fun doLogout() {
        token = STRING_DEFAULT
        login = STRING_DEFAULT
    }

    companion object {

        private const val SP_FILE = "spFile"
    }
}