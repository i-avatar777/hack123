package com.gravitygroup.avangard.presentation.yacht

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.gravitygroup.avangard.BuildConfig
import com.gravitygroup.avangard.NestedYachtDirections
import com.gravitygroup.avangard.R
import com.gravitygroup.avangard.interactors.yacht.YachtFilterInteractor
import com.gravitygroup.avangard.interactors.yacht.data.YachtUIModel
import com.gravitygroup.avangard.presentation.base.BaseViewModel
import com.gravitygroup.avangard.presentation.base.IViewModelState
import com.gravitygroup.avangard.presentation.base.NavigationCommand
import com.gravitygroup.avangard.presentation.base.Notify
import com.gravitygroup.avangard.presentation.yacht.YachtFilterVm.SocketMessage.Captain
import com.gravitygroup.avangard.presentation.yacht.YachtFilterVm.SocketMessage.Client
import com.gravitygroup.avangard.presentation.yacht.YachtFilterVm.SocketMessage.Usual
import com.gravitygroup.avangard.presentation.yacht.YachtFilterVm.YachtListState
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import io.socket.engineio.client.transports.Polling
import io.socket.engineio.client.transports.WebSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class YachtFilterVm(
    private val context: Context,
    private val interactor: YachtFilterInteractor,
    handleState: SavedStateHandle
) : BaseViewModel<YachtListState>(handleState, YachtListState()) {

    private lateinit var mSocket: io.socket.client.Socket
    private val _socketMessage = MutableLiveData<SocketMessage>()
    val socketMessageObserve: LiveData<SocketMessage> = _socketMessage

    fun requestYachtList(
        placeCount: Int,
        typeRent: Int,
        priceFrom: Int,
        priceTo: Int
    ) {
        updateState {
            it.copy(
                isLoading = true,
            )
        }
        launch(Dispatchers.Main) {
            val list = interactor.requestList(placeCount, typeRent, priceFrom, priceTo)
            updateState {
                it.copy(
                    isLoading = false,
                    yachtData = list
                )
            }
        }
    }

    fun done(yachtListSpecs: YachtListSpecs) {
        navigate(
            NavigationCommand.Dir(
                NestedYachtDirections.actionToYachtList(yachtListSpecs)
            )
        )
    }

    fun navigateToClient() {
        navigate(
            NavigationCommand.Dir(
                NestedYachtDirections.actionToClientYacht()
            )
        )
    }

    fun navigateToFilter() {
        navigate(
            NavigationCommand.Dir(
                NestedYachtDirections.actionToYachtFilter()
            )
        )
    }

    fun navigateToCaptain() {
        navigate(
            NavigationCommand.Dir(
                NestedYachtDirections.actionToCaptainCabinet()
            )
        )
    }


    fun navigateToYachtOrder() {
        navigate(
            NavigationCommand.Dir(
                NestedYachtDirections.actionToCaptainCabinet()
            )
        )
    }

    fun initSocket() {
        try {
            val opts: IO.Options = IO.Options()
            opts.transports = arrayOf(WebSocket.NAME, Polling.NAME)
            mSocket = IO.socket(SOCKET_URL, opts)
            if (BuildConfig.APP_TYPE == "client") {
                mSocket.on(Socket.EVENT_CONNECT, onConnectClient)
            } else {
                mSocket.on(Socket.EVENT_CONNECT, onConnectCaptain)
                mSocket.on(SEND_MESSAGE_KEY, onSubscribedCaptain)
            }

            mSocket.on(io.socket.client.Socket.EVENT_CONNECT_ERROR, onConnectError)
            mSocket.connect()

            Timber.tag(TAG).d("success ${mSocket.toString()}")
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.tag(TAG).d("fail Failed to connect")
        }
    }

    fun sendYachtRequest() {
        mSocket.emit(SEND_MESSAGE_KEY, ROOM_ID, USER_ID)
        notify(Notify.ErrorHide(context.getString(R.string.yacht_client_send_order)))
    }

    private val onConnectError =
        Emitter.Listener { array ->
            if (array.isNotEmpty()) {
                val errorString = array[0].toString()
                Timber.tag(TAG).e("SocketConnectError: $errorString")
                _socketMessage.postValue(SocketMessage.Error(errorString))
            }
        }

    private val onConnectCaptain = Emitter.Listener {
        mSocket.emit(NEW_USER_KEY, ROOM_ID, USER_ID)
    }

    private val onSubscribedCaptain = Emitter.Listener {
        _socketMessage.postValue(Captain)
//        notify(Notify.ErrorHide(context.getString(R.string.yacht_captain_incoming_order)))
    }

    private val onConnectClient = Emitter.Listener {
        _socketMessage.postValue(Client)
//        notify(Notify.ErrorHide(context.getString(R.string.yacht_client_allow_order)))
    }

    companion object {

        private const val ROOM_ID = 1

        private const val USER_ID = 1

        private const val SEND_MESSAGE_KEY = "room-send-message"

        private const val NEW_USER_KEY = "room-new-user"

        private const val SOCKET_URL = "http://chat.qualitylive.su/"

        private const val TAG = "YachtFilterViewModel"
    }

    sealed class SocketMessage {
        data class Error(val error: String) : SocketMessage()

        object Captain : SocketMessage()

        object Client : SocketMessage()

        object Usual : SocketMessage()
    }

    data class YachtListState(
        val isLoading: Boolean = false,
        val yachtData: List<YachtUIModel> = emptyList(),
        val socketMessage: SocketMessage = Usual
    ) : IViewModelState {

        override fun save(outState: SavedStateHandle) {
            super.save(outState)
        }

        override fun restore(savedState: SavedStateHandle): IViewModelState {
            return super.restore(savedState)
        }
    }
}