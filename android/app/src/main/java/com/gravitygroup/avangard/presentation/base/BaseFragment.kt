package com.gravitygroup.avangard.presentation.base

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.navigation.NavController
import com.gravitygroup.avangard.core.ext.doOnApplyWindowInsets
import com.gravitygroup.avangard.core.ext.hideKeyboard
import com.gravitygroup.avangard.presentation.RootActivity
import timber.log.Timber

abstract class BaseFragment<T : BaseViewModel<out IViewModelState>>(
    @LayoutRes private val contentLayoutId: Int
) : Fragment(contentLayoutId) {

    open val stateBinding: StateBinding? = null

    abstract val viewModel: T

    open val navHostFragmentId: Int? = null
    var navController: NavController? = null

    val root: RootActivity
        get() = activity as RootActivity

    lateinit var permissionsLauncher: ActivityResultLauncher<Array<out String>>
    lateinit var settingsLauncher: ActivityResultLauncher<Intent>

    abstract fun setupViews()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("BaseFragment >> onCreate $this")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("BaseFragment >> onViewCreated $this")

        viewModel.restoreState()
        viewModel.observeState(viewLifecycleOwner) { stateBinding?.bind(it) }
        viewModel.observeNotifications(viewLifecycleOwner) { root.renderNotification(it) }
        viewModel.observeNavigation(viewLifecycleOwner) { root.viewModel.navigate(it) }
        viewModel.observePermissions(viewLifecycleOwner) { subscribeOnRequestedPermissions(it) }
    }

    override fun onDetach() {
        super.onDetach()
        hideKeyboard()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Timber.d("BaseFragment >> onViewStateRestored $this >> savedState: $savedInstanceState")
        setupViews()
        setupInsets()
        stateBinding?.rebind()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Timber.d("BaseFragment >> onSaveInstanceState $this")
        viewModel.saveState()
        stateBinding?.saveUi(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideKeyboard()
        Timber.d("BaseFragment >> onDestroyView $this")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("BaseFragment >> onDestroy $this")
    }

    // other managing
    fun showToastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    protected open fun setupInsets() {
        view?.doOnApplyWindowInsets { view, insets ->
            view.updatePadding(
                top = insets.systemWindowInsetTop,
                bottom = insets.systemWindowInsetBottom
            )
            insets
        }
    }

    private fun subscribeOnRequestedPermissions(list: List<String>) {
        permissionsLauncher.launch(list.toTypedArray())
    }

    protected fun setupMainToolbar(toolbar: Toolbar) {
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                else -> super.onOptionsItemSelected(item)
            }
        }
    }

    open fun onFragmentResult(key: String, result: Bundle) {}

    protected fun registerFragmentResult(
            requestKey: String,
            fragmentManager: FragmentManager = childFragmentManager
    ) {
        fragmentManager
                .setFragmentResultListener(
                        requestKey,
                        this,
                        FragmentResultListener { requestKey, result ->
                            onFragmentResult(requestKey, result)
                        }
                )
    }

}
