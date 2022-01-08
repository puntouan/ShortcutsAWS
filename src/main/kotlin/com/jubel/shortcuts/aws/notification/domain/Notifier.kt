package com.jubel.shortcuts.aws.notification.domain

import com.google.inject.ImplementedBy
import com.jubel.shortcuts.aws.notification.infrastructure.GoogleChatNotifier

@ImplementedBy(GoogleChatNotifier::class)
interface Notifier {

    fun sendMessage(message: String)

}