package com.gravitygroup.avangard.presentation.auth

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.CountDownTimer
import android.widget.Toast
import androidx.core.view.*
import androidx.core.widget.doOnTextChanged
import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.core.ext.*
import com.gravitygroup.avangard.core.utils.setSafeOnClickListener
import com.gravitygroup.avangard.databinding.FragmentAuthBinding
import com.gravitygroup.avangard.presentation.base.BaseFragment
import com.gravitygroup.avangard.presentation.base.IViewModelState
import com.gravitygroup.avangard.presentation.base.StateBinding
import com.gravitygroup.avangard.presentation.utils.delegates.RenderProp
import com.gravitygroup.avangard.view.KeyboardVisibilityConsumer
import com.gravitygroup.avangard.view.loading.LoadingState
import org.koin.android.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class AuthFragment : BaseFragment<AuthVm>(R.layout.fragment_auth), KeyboardVisibilityConsumer {

    override val viewModel: AuthVm by sharedViewModel()
    private val vb by viewBinding(FragmentAuthBinding::bind)
    override val stateBinding by lazy { AuthStateBinding() }

    private var downTimer: CountDownTimer? = null

    override fun setupViews() {
        vb?.apply {
            etPassword.doOnTextChanged { _, _, _, _ ->
                viewModel.onFieldEdit()
                checkAuthButtonEnable()
            }
            etLogin.doOnTextChanged { _, _, _, _ ->
                viewModel.onFieldEdit()
                checkAuthButtonEnable()
            }
            tvSupportPhone.setSafeOnClickListener { requireContext().callToSupport(tvSupportPhone.text.toString()) }
            submitButton.setSafeOnClickListener {
                hideKeyboard()
                viewModel.auth(etLogin.text.toString(), etPassword.text.toString())
            }
            checkAuthButtonEnable()
        }

        viewModel.onSetupView()
    }

    override fun setupInsets() {
        vb?.root?.doOnApplyWindowInsets { view, insets ->
            val bottom = insets.systemWindowInsetBottom
            view.updatePadding(
                bottom = bottom
            )
            vb?.scrollableView?.updatePadding(
                top = insets.systemWindowInsetTop
            )
            vb?.scrollableView?.post {
                vb?.scrollableView?.scrollY = if(bottom > 0) vb?.scrollableView?.maxScrollAmount ?: 0 else 0
            }
            insets
        }
    }

    override fun onKeyboardVisibleChanged(visible: Boolean) {
        vb?.apply {
            llSupport.isVisible = visible.not()
        }
    }

    override fun onDestroyView() {
        downTimer?.cancel()
        downTimer = null
        super.onDestroyView()
    }

    private fun checkAuthButtonEnable() {
        vb?.apply {
            submitButton.isEnabled =
                !(etLogin.text.isNullOrEmpty() || etPassword.text.isNullOrEmpty()) && !tvSmsPenalty.isVisible
        }
    }

    private fun checkSmsTimeout(smsTimeout: Long) {
        val currentMillis = System.currentTimeMillis()
        val isRequestSmsEnabled = currentMillis > smsTimeout

        vb?.apply {
            tvSmsPenalty.isVisible = !isRequestSmsEnabled
            checkAuthButtonEnable()

            if (!isRequestSmsEnabled) {
                downTimer?.cancel()
                downTimer = initDownTimer(smsTimeout - currentMillis,
                    {
                        tvSmsPenalty.text =
                            getString(R.string.sms_code_expiration_timeout).format(it/1000)
                    },
                    {
                        tvSmsPenalty.isVisible = false
                        checkAuthButtonEnable()
                    }
                )
            }
        }
    }

    inner class AuthStateBinding : StateBinding() {

        private var isLoading by RenderProp(true) { isLoading ->
            requireActivity()
                .rootView
                .doOnLayout { // TODO: проверить на утечку
                    vb?.loading?.render(
                        if (isLoading) {
                            LoadingState.Loading
                        } else {
                            LoadingState.None
                        }
                    )
                }
        }

        private var errorMessage by RenderProp("") {
            vb?.apply {
                tvError.text = it
                rlErrorView.visible(it.isNotEmpty())

                if (it.isNotEmpty()) tilLogin.error = " " else tilLogin.error = it
                if (it.isNotEmpty()) tilPassword.error = " " else tilPassword.error = it
            }
        }

        private var smsTimeout by RenderProp(0L) {
            checkSmsTimeout(it)
        }

        override fun bind(data: IViewModelState) {
            data as AuthState
            isLoading = data.isLoading
            errorMessage = data.errorMessage
            smsTimeout = data.smsTimeout
        }
    }

    companion object {

        fun Context.callToSupport(phoneNumber: String) {
            val callIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("tel:$phoneNumber")
            )

            try {
                startActivity(callIntent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, getString(R.string.order_can_not_call_by_phone),Toast.LENGTH_SHORT).show();
                Timber.e("callToSupport fail start $e")
            }
        }
    }
}