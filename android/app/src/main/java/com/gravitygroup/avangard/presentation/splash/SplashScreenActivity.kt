package com.gravitygroup.avangard.presentation.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.gravitygroup.avangard.BuildConfig
import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.core.ext.viewBinding
import com.gravitygroup.avangard.databinding.ActivitySplashBinding
import com.gravitygroup.avangard.presentation.RootActivity

class SplashScreenActivity : AppCompatActivity() {

    private val vb by viewBinding(ActivitySplashBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vb.root)
        scheduleSplashScreen()
    }

    override fun onResume() {
        super.onResume()
        vb.apply {
            tvVersionApp.text = resources.getString(R.string.version_prefix,  BuildConfig.VERSION_NAME)
        }
    }

    private fun scheduleSplashScreen() {
        Handler().postDelayed(
            {
                // После Splash Screen перенаправляем на нужную Activity
                startActivity(Intent(this@SplashScreenActivity, RootActivity::class.java))
                finish()
            },
            SPLASH_SCREEN_MILLISECONDS_DURATION
        )
    }

    companion object {

        private const val SPLASH_SCREEN_MILLISECONDS_DURATION = 1500L
    }
}