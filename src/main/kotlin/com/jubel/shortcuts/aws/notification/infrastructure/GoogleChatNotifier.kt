package com.jubel.shortcuts.aws.notification.infrastructure

import com.google.inject.Inject
import com.jubel.shortcuts.aws.notification.domain.Notifier
import com.jubel.shortcuts.aws.shared.domain.ConfigRepository
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.ContentType.*
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import io.ktor.client.request.post as post

class GoogleChatNotifier @Inject constructor(
    private val configRepository: ConfigRepository
): Notifier {


    private val client = HttpClient(CIO){
        install(JsonFeature)
    }

    override fun sendMessage(message: String) {

        if (configRepository.areNotificationsDisabled()){
            return
        }

        val url = configRepository.getChatUrl()


        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                client.post<String>(url){
                    contentType(Application.Json)
                    body = GoogleChatMessage(message)
                }
            }.join()
        }

    }

}

@Serializable
data class GoogleChatMessage(
    val text: String
)