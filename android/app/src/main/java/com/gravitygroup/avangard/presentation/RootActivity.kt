package com.gravitygroup.avangard.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.GravityCompat
import androidx.core.view.updatePadding
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.core.ext.doOnApplyWindowInsets
import com.gravitygroup.avangard.core.ext.setEdgeToEdge
import com.gravitygroup.avangard.core.ext.viewBinding
import com.gravitygroup.avangard.core.utils.PermissionUtils.STORAGE_CAM_PERMISSION_REQUEST
import com.gravitygroup.avangard.databinding.ActivityRootBinding
import com.gravitygroup.avangard.presentation.base.BaseActivity
import com.gravitygroup.avangard.presentation.base.IViewModelState
import com.gravitygroup.avangard.presentation.base.Notify
import com.gravitygroup.avangard.presentation.utils.SystemBackPressed
import com.gravitygroup.avangard.utils.popup
import com.gravitygroup.avangard.view.KeyboardVisibilityConsumer
import org.koin.android.viewmodel.ext.android.viewModel

class RootActivity : BaseActivity<RootVm>() {

    private val vb by viewBinding(ActivityRootBinding::inflate)

    override val viewModel: RootVm by viewModel()

    private val displayHeight by lazy { resources.displayMetrics.heightPixels }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Avangard)
        window.setEdgeToEdge()
        super.onCreate(savedInstanceState)
        initInsetsListener()
        setContentView(vb.root)
        initNavigation()
        initDrawer()
        viewModel.onInit()
    }

    override fun renderNotification(notify: Notify) {
        if (notify is Notify.DialogAction) {
            popup(notify.message)
                .positive(notify.positiveActionLabel, notify.positiveActionHandler)
                .apply {
                    if (notify.title != null) {
                        title(notify.title)
                    }
                    if (notify.negativeActionLabel != null) {
                        negative(notify.negativeActionLabel, notify.negativeActionHandler)
                    }
                }
                .alert()
            return
        }

        val snackbar = Snackbar.make(vb.rootLayout, notify.message, Snackbar.LENGTH_LONG)

        when (notify) {
            is Notify.Text -> {
            }
            is Notify.Action -> {
                snackbar.setAction(notify.actionLabel) {
                    notify.actionHandler.invoke()
                }
            }
            is Notify.Error -> {
                with(snackbar) {
                    setBackgroundTint(getColor(R.color.design_default_color_error))
                    setTextColor(getColor(android.R.color.white))
                    setActionTextColor(getColor(android.R.color.white))
                    setAction(notify.errLabel) {
                        notify.errHandler?.invoke()
                    }
                }
            }
        }

        snackbar.show()
    }

    open fun showDrawer(viewLifecycleOwner: LifecycleOwner, anchor: View, context: Context) {
        vb.rootLayout.openDrawer(GravityCompat.START)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        return when (item.itemId) {
            android.R.id.home -> {
                vb.rootLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in supportFragmentManager.fragments) {
            for (child in fragment.childFragmentManager.fragments) {
                child.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        if (requestCode == STORAGE_CAM_PERMISSION_REQUEST) {
            for (fragment in supportFragmentManager.fragments) {
                for (child in fragment.childFragmentManager.fragments) {
                    child.onRequestPermissionsResult(requestCode, permissions, grantResults)
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun subscribeOnState(state: IViewModelState) {
    }

    override fun onBackPressed() {
        for (fragment in supportFragmentManager.fragments) {
            for (child in fragment.childFragmentManager.fragments) {
                (child as? SystemBackPressed)?.also {
                    (child as? SystemBackPressed)?.onBackPressed()
                }
            }
        }
        super.onBackPressed()
    }

    private fun initInsetsListener() {
        vb.root.doOnApplyWindowInsets { view, insets ->
            val top = insets.systemWindowInsetTop
            val bottom = insets.systemWindowInsetBottom

            val header = vb.navigationView.getHeaderView(0)
            header.updatePadding(
                top = top
            )

            val isKeyboardShow = bottom >= 0.25 * displayHeight
            onKeyboardVisibleChanged(isKeyboardShow)

            insets
        }
    }

    private fun onKeyboardVisibleChanged(visible: Boolean) {
        for (fragment in supportFragmentManager.fragments) {
            for (child in fragment.childFragmentManager.fragments) {
                if (child is KeyboardVisibilityConsumer) {
                    (child as KeyboardVisibilityConsumer).onKeyboardVisibleChanged(visible)
                }
            }
        }
    }

    private fun initDrawer() {
        vb.rootLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    private fun initNavigation() {
        vb.navigationView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            vb.rootLayout.close()
        }
    }
}
