package com.gravitygroup.avangard.core.utils

import android.Manifest.permission
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionUtils {

    /**
     * Флаг наличия разрешений на запись в файловое хранилище и к камере в текущей activity
     */
    fun Activity.hasStorageCamPermission() =
        ContextCompat.checkSelfPermission(
            this,
            permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this, permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this, permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED

    /**
     * Проверяет наличие разрешений на запись в файловое хранилище и к камере
     */
    fun Activity.checkStorageCamPermissions(): Boolean {
        return if (this.hasStorageCamPermission()) {
            true
        } else {
            val permissions = arrayOf(
                permission.READ_EXTERNAL_STORAGE,
                permission.WRITE_EXTERNAL_STORAGE,
                permission.CAMERA
            )
            ActivityCompat.requestPermissions(this, permissions, STORAGE_CAM_PERMISSION_REQUEST)
            false
        }
    }

        /**
         * Код запроса разрешения на запись в файловое хранилище и к камере
         */
        const val STORAGE_CAM_PERMISSION_REQUEST = 1801
}