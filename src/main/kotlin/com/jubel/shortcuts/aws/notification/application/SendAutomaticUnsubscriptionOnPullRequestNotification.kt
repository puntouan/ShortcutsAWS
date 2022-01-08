package com.jubel.shortcuts.aws.notification.application

import com.google.inject.Inject
import com.jubel.shortcuts.aws.notification.domain.Notifier

class SendAutomaticUnsubscriptionOnPullRequestNotification @Inject constructor(
    private val notifier: Notifier
) {

    fun run(id: String, repo: String, author: String, url: String){

        val message = """
            Se ha eliminado la suscripción sobre la siguiente Pull Request porque ha sido cerrada:
             - Id: $id
             - Repo: $repo
             - Author: $author
             - Url: $url
        """.trimIndent()

        println("Notificando eliminación de suscripción a Google Chat")
        notifier.sendMessage(message)
    }

}