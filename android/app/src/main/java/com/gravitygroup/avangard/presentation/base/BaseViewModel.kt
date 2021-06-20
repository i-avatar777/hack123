package com.gravitygroup.avangard.presentation.base

import android.content.Context
import android.os.Bundle
import androidx.annotation.UiThread
import androidx.lifecycle.*
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.core.dispatchers.DispatchersProvider
import com.gravitygroup.avangard.core.network.base.RequestResult
import com.gravitygroup.avangard.core.network.errors.ApiError
import kotlinx.coroutines.*
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel<T : IViewModelState>(
    private val handleState: SavedStateHandle,
    initState: T
) : ViewModel(),
    KoinComponent,
    CoroutineScope {

    val dispatchers: DispatchersProvider by inject()
    private val context: Context by inject()

    private val parentJob = SupervisorJob()

    override val coroutineContext: CoroutineContext = dispatchers.default() + parentJob

    private val notifications = MutableLiveData<Event<Notify>>()

    private val navigation = MutableLiveData<Event<NavigationCommand>>()

    private val permissions = MutableLiveData<Event<List<String>>>()

    val state: MediatorLiveData<T> = MediatorLiveData<T>().apply {
        value = initState
    }

    val currentState
        get() = state.value!!

    init {
        Timber.d("BaseViewModel >> init $this")
    }

    override fun onCleared() {
        Timber.d("BaseViewModel >> onCleared $this")
        parentJob.cancelChildren()
    }

    fun saveState() {
        currentState.save(handleState)
    }

    @Suppress("UNCHECKED_CAST")
    fun restoreState() {
        val restoredState: T = currentState.restore(handleState) as T
        if (currentState == restoredState) return
        state.value = currentState.restore(handleState) as T
    }

    @UiThread
    inline fun updateState(update: (currentState: T) -> T) {
        val updatedState: T = update(currentState)
        state.value = updatedState
    }

    suspend fun updateStateInMain(update: (currentState: T) -> T) {
        runOnUi {
            updateState(update)
        }
    }

    fun observeState(owner: LifecycleOwner, onChanged: (newState: T) -> Unit) {
        state.observe(owner, Observer { onChanged(it) })
    }

    // контекст слушает и показывает
    fun observeNotifications(owner: LifecycleOwner, onNotify: (notification: Notify) -> Unit) {
        notifications.observe(owner, EventObserver { onNotify(it) })
    }

    fun observeNavigation(owner: LifecycleOwner, onNavigate: (command: NavigationCommand) -> Unit) {
        navigation.observe(owner, EventObserver { onNavigate(it) })
    }

    // navigation with commands
    open fun navigateBack() {
        navigate(NavigationCommand.Back)
    }

    // update state

    fun notify(content: Notify) {
        notifications.value = Event(content)
    }

    open fun navigate(command: NavigationCommand) {
        navigation.value = Event(command)
    }

    fun requestPermissions(requestedPermissions: List<String>) {
        permissions.value = Event(requestedPermissions)
    }

    fun observePermissions(owner: LifecycleOwner, handle: (List<String>) -> Unit) {
        permissions.observe(owner, EventObserver { handle(it) })
    }

    suspend fun runOnUi(block: suspend () -> Unit) =
        withContext(this.dispatchers.main()) {
            block()
        }

    suspend inline fun <T> RequestResult<T>.handleResult(noinline block: suspend (RequestResult.Success<T>) -> Unit) {
        runOnUi {
            when (val result = this@handleResult) {
                is RequestResult.Success -> block(result)
                is RequestResult.Error -> mapAndShowError(result.error)
            }
        }
    }

    suspend inline fun <T> RequestResult<T>.handleResultWithError(
        noinline resultBlock: suspend (RequestResult.Success<T>) -> Unit,
        noinline errorBlock: suspend (Throwable) -> Unit
    ) = runOnUi {
        when (val result = this@handleResultWithError) {
            is RequestResult.Success -> resultBlock(result)
            is RequestResult.Error -> errorBlock(result.error)
        }
    }

    open fun mapAndShowError(error: Throwable) {
        notify(
            Notify.Error(mapError(error))
        )
    }

    open fun mapError(error: Throwable): String =
        context.getString(
            when (error) {
                is ApiError.GeneralError -> R.string.error_api_default
                is ApiError.NoDataError -> R.string.error_api_default
                is ApiError.BreakDataError -> R.string.error_api_break_date
                is ApiError.TokenError -> R.string.error_api_token_error
                is ApiError.SmsSendError -> R.string.error_api_sms_send_error
                is ApiError.UnknownError -> R.string.error_api_default
                else -> R.string.error_api_default
            }
        )
}

sealed class Notify {

    abstract val message: String

    data class Text(override val message: String) : Notify()

    data class Action(
        override val message: String,
        val actionLabel: String,
        val actionHandler: (() -> Unit)
    ) : Notify()

    data class Error(
        override val message: String,
        val errLabel: String? = null,
        val errHandler: (() -> Unit)? = null
    ) : Notify()

    data class ErrorHide(
        override val message: String = ""
    ) : Notify()

    data class DialogAction(
        val title: String? = null,
        override val message: String,
        val positiveActionLabel: String,
        val positiveActionHandler: (() -> Unit),
        val negativeActionLabel: String? = null,
        val negativeActionHandler: (() -> Unit)? = null,
    ) : Notify()
}

class Event<out E>(private val content: E, private var consumeHandler: (() -> Unit)? = null) {

    var isConsumed = false

    fun consumeIfNotHandled(): E? {
        return if (isConsumed) null
        else {
            isConsumed = true
            consumeHandler?.invoke()
            consumeHandler = null
            content
        }
    }

    fun peekIfNotHandled(): E? {
        return if (isConsumed) null
        else {
            content
        }
    }

    fun peek(): E = content
}

class EventObserver<E>(private val onEventUnhandledContent: (E) -> Unit) : Observer<Event<E>> {

    override fun onChanged(event: Event<E>?) {
        event?.consumeIfNotHandled()?.let {
            onEventUnhandledContent(it)
        }
    }
}

sealed class NavigationCommand {

    data class To(
        val destination: Int,
        val args: Bundle? = null,
        val options: NavOptions? = null,
        val extras: Navigator.Extras? = null
    ) : NavigationCommand()

    data class Dir(
        val directions: NavDirections,
        val options: NavOptions? = null
    ) : NavigationCommand()

    data class DirForward(
        val directions: NavDirections,
        val forwardDirections: NavDirections,
        val options: NavOptions? = null
    ) : NavigationCommand()

    object StartLogin : NavigationCommand()

    object FinishLogin : NavigationCommand()

    object Yacht : NavigationCommand()

    object Logout : NavigationCommand()

    object Back : NavigationCommand()
}
