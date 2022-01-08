package com.jubel.shortcuts.aws.notification.application

import com.google.inject.Inject
import com.jubel.shortcuts.aws.notification.domain.Notifier

class SendSimpleMessageNotification  @Inject constructor(
    private val notifier: Notifier
) {

    fun run(message: String){
        notifier.sendMessage(message)
    }

}