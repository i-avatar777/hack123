package com.gravitygroup.avangard.core.service

import android.content.Context
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.gravitygroup.avangard.core.dispatchers.DispatchersProvider
import com.gravitygroup.avangard.interactors.settings.SettingsInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.coroutines.CoroutineContext

class MessagingService : FirebaseMessagingService(), KoinComponent, CoroutineScope {

    private val settingsInteractor: SettingsInteractor by inject()
    val dispatchers: DispatchersProvider by inject()
    private val context: Context by inject()

    private val parentJob = SupervisorJob()
    override val coroutineContext: CoroutineContext = dispatchers.default() + parentJob

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        handleMessage(remoteMessage)
    }

    override fun onNewToken(token: String) {
        launch {
            settingsInteractor.setNotificationToken(token)
        }
    }

    override fun onMessageSent(msg: String) {
        //TODO
        super.onMessageSent(msg)
    }

    private fun handleMessage(remoteMessage: RemoteMessage) {
        //TODO
    }
}
