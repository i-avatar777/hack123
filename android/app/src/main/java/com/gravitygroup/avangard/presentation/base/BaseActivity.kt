package com.gravitygroup.avangard.presentation.base

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.gravitygroup.avangard.R

abstract class BaseActivity<T : BaseViewModel<out IViewModelState>> : LocalizationActivity() {

    abstract val viewModel: T
    lateinit var navController: NavController
    lateinit var permissionsLauncher: ActivityResultLauncher<Array<out String>>
    lateinit var settingsLauncher: ActivityResultLauncher<Intent>

    abstract fun subscribeOnState(state: IViewModelState)

    abstract fun renderNotification(notify: Notify)

    override fun setContentView(view: View?) {
        super.setContentView(view)

        permissionsLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(), ::handleResultPermissions
        )

        viewModel.observeState(this) { subscribeOnState(it) }
        viewModel.observeNotifications(this) { renderNotification(it) }
        viewModel.observeNavigation(this) { subscribeOnNavigation(it) }
        viewModel.observePermissions(this) { subscribeOnRequestedPermissions(it) }

        navController = findNavController(R.id.root_nav_host)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        viewModel.saveState()
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        viewModel.restoreState()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    open fun subscribeOnNavigation(command: NavigationCommand) {
        when (command) {
            is NavigationCommand.Back -> navController.popBackStack()
            is NavigationCommand.To -> {
                navController.navigate(
                    command.destination,
                    command.args,
                    command.options,
                    command.extras
                )
            }
            is NavigationCommand.Dir -> {
                navController.navigate(
                    command.directions,
                    command.options
                )
            }
            is NavigationCommand.DirForward -> {
                navController.navigate(
                    command.directions
                )
                subscribeOnNavigation(
                    NavigationCommand.Dir(
                        command.forwardDirections,
                        command.options
                    )
                )
            }
            is NavigationCommand.FinishLogin -> {
                navController.graph.startDestination = R.id.nested_main
                navController.navigate(R.id.action_global_to_main)
            }
            is NavigationCommand.StartLogin -> {
                navController.graph.startDestination = R.id.nested_auth
                navController.navigate(R.id.action_global_to_auth)
            }
            is NavigationCommand.Yacht -> {
                navController.graph.startDestination = R.id.nested_yacht
                navController.navigate(R.id.action_to_yacht_filter)
            }
        }
    }

    private fun subscribeOnRequestedPermissions(list: List<String>) {
        permissionsLauncher.launch(list.toTypedArray())
    }

    private fun handleResultPermissions(result: Map<String, Boolean>) {
        val permissionToGrantAndRationaleMap = result.mapValues { (permission, isGranted) ->
            if (isGranted) true to true
            else false to ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                permission
            )
        }

        val grantRationaleValues = permissionToGrantAndRationaleMap.values
        val isAllGranted = !grantRationaleValues.map { it.first }.contains(false)
        val isAnyPermissionWithRestrictedRationale =
            grantRationaleValues.map { it.second }.contains(false)

        val permissionsResult = PermissionsResult(
            permissionToGrantAndRationaleMap,
            isAllGranted,
            !isAnyPermissionWithRestrictedRationale
        )

        onPermissionsResult(permissionsResult)
    }

    open fun onPermissionsResult(result: PermissionsResult) {
    }
}
