package com.jubel.shortcuts.aws.notification.application

import com.google.inject.Inject
import com.jubel.shortcuts.aws.notification.domain.Notifier

class SendChangeOnPullRequestNotification @Inject constructor(
    private val notifier: Notifier
) {

    fun run(id: String, repo: String, author: String, url: String){

        val message = """
            Ha habido un cambio en la siguiente pull request a la que estás suscrito:
             - Id: $id
             - Repo: $repo
             - Author: $author
             - Url: $url
        """.trimIndent()

        println("Notificando a Google Chat")
        notifier.sendMessage(message)
    }

}