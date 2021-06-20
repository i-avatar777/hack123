package com.gravitygroup.avangard

import android.content.Context
import android.util.Log
import com.akexorcist.localizationactivity.ui.LocalizationApplication
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.gravitygroup.avangard.di.appModules
import com.yandex.mapkit.MapKitFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber
import java.util.*

class App : LocalizationApplication() {

    override fun getDefaultLanguage(base: Context) = Locale("ru", "RU")

    override fun onCreate() {
        super.onCreate()
        initKoin()
        initLogger()
        //initCrashlytics()
        MapKitFactory.setApiKey(BuildConfig.MAP_KEY)
        MapKitFactory.initialize(this)
    }

    private fun initKoin() {
        startKoin {
            androidContext(applicationContext)
            modules(appModules)
        }
    }

    private fun initLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }
    }

    private fun initCrashlytics() {
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
    }

    private inner class CrashReportingTree : timber.log.Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, throwable: Throwable?) {
            if (priority == Log.ERROR || priority == Log.DEBUG) {
                FirebaseCrashlytics.getInstance().log(message)
                if (throwable != null) {
                    FirebaseCrashlytics.getInstance().recordException(throwable)
                }
            } else return
        }
    }
}
