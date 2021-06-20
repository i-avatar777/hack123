package com.gravitygroup.avangard.presentation.auth

import android.os.CountDownTimer
import android.view.inputmethod.EditorInfo
import androidx.core.view.doOnLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.navArgs
import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.core.ext.*
import com.gravitygroup.avangard.core.utils.setSafeOnClickListener
import com.gravitygroup.avangard.databinding.FragmentSmsVerificationBinding
import com.gravitygroup.avangard.presentation.auth.AuthFragment.Companion.callToSupport
import com.gravitygroup.avangard.presentation.base.BaseFragment
import com.gravitygroup.avangard.presentation.base.IViewModelState
import com.gravitygroup.avangard.presentation.base.StateBinding
import com.gravitygroup.avangard.presentation.utils.delegates.RenderProp
import com.gravitygroup.avangard.view.KeyboardVisibilityConsumer
import com.gravitygroup.avangard.view.loading.LoadingState.Loading
import com.gravitygroup.avangard.view.loading.LoadingState.None
import org.koin.android.viewmodel.ext.android.viewModel

class SmsVerificationFragment : BaseFragment<SmsVerificationVm>(R.layout.fragment_sms_verification),
    KeyboardVisibilityConsumer {

    private val vb by viewBinding(FragmentSmsVerificationBinding::bind)

    private val args: SmsVerificationFragmentArgs by navArgs()

    private val smsCodeUIModel by lazy { args.specs }

    override val viewModel: SmsVerificationVm by viewModel()
    override val stateBinding by lazy { SmsVerifyStateBinding() }

    private var downTimer: CountDownTimer? = null

    override fun setupViews() {
        checkSmsVerifyButtonEnable()
        vb?.apply {
            codePinEntry.doOnTextChanged { text, _, _, _ ->
                if(!text.isNullOrBlank()) {
                    viewModel.onStartChange()
                }
                checkSmsVerifyButtonEnable()
            }
            codePinEntry.setOnEditorActionListener { _, actionId, _ ->
                if(actionId == EditorInfo.IME_ACTION_DONE && vb?.submitButton?.isEnabled == true) {
                    onSubmit()
                }
                false
            }
            codePinEntry.showKeyboard()

            submitButton.setSafeOnClickListener {
                onSubmit()
            }

            requestSmsButton.setSafeOnClickListener {
                codePinEntry.setText(STRING_DEFAULT)
                viewModel.onRequestSms(smsCodeUIModel.login, smsCodeUIModel.password)
            }

            tvSupportPhone.setSafeOnClickListener { requireContext().callToSupport(tvSupportPhone.text.toString()) }
        }
    }

    private fun onSubmit() {
        viewModel.verifySmsCode(login = smsCodeUIModel.login, smsCode = vb?.codePinEntry?.text?.toString() ?: "")
    }

    override fun onKeyboardVisibleChanged(visible: Boolean) {
    }

    override fun onDestroyView() {
        requireActivity().hideSoftKeyboard()
        downTimer?.cancel()
        downTimer = null
        super.onDestroyView()
    }

    private fun checkSmsVerifyButtonEnable() {
        vb?.apply {
            submitButton.isEnabled =
                codePinEntry.text.toString().length == QUANTITY_DIGITS_SMS_CODE
        }
    }

    private fun checkSmsTimeout(smsTimeout: Long) {
        val currentMillis = System.currentTimeMillis()
        val isRequestSmsEnabled = currentMillis > smsTimeout

        vb?.apply {
            tvSmsPenalty.isInvisible = isRequestSmsEnabled
            requestSmsButton.isVisible = isRequestSmsEnabled
            checkSmsVerifyButtonEnable()

            if (!isRequestSmsEnabled) {
                downTimer?.cancel()
                downTimer = initDownTimer(smsTimeout - currentMillis,
                    {
                        tvSmsPenalty.text =
                            getString(R.string.sms_code_expiration_timeout).format(it/1000)
                    },
                    {
                        tvSmsPenalty.isInvisible = true
                        requestSmsButton.isVisible = true
                        checkSmsVerifyButtonEnable()
                        viewModel.onStartChange() // сбросим ошибку, если есть
                    }
                )
            }
        }
    }

    companion object {

        private const val QUANTITY_DIGITS_SMS_CODE = 4
        private const val STRING_DEFAULT = ""
    }

    inner class SmsVerifyStateBinding : StateBinding() {

        private var isLoading by RenderProp(true) { isLoading ->
            activity
                ?.rootView
                ?.doOnLayout {
                    vb?.loading?.render(
                        if (isLoading) {
                            Loading
                        } else {
                            None
                        }
                    )
                }
        }

        private var errorMessage by RenderProp("") {
            vb?.apply {
                if(it.isNotEmpty()) {
                    codePinEntry.setText(STRING_DEFAULT)
                }
                tvError.text = it
                rlErrorView.isVisible = it.isNotEmpty()
            }
        }

        private var smsTimeout by RenderProp(0L) {
            checkSmsTimeout(it)
        }

        override fun bind(data: IViewModelState) {
            data as SmsVerificationState
            isLoading = data.isLoading
            errorMessage = data.errorMessage
            smsTimeout = data.smsTimeout
        }
    }
}