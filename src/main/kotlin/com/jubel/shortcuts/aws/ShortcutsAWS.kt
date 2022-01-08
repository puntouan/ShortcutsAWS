package com.jubel.shortcuts.aws

import com.google.inject.Guice
import com.google.inject.Injector
import com.jubel.shortcuts.aws.notification.application.SendSimpleMessageNotification
import com.jubel.shortcuts.aws.shared.domain.ExpiredTokenException
import com.jubel.shortcuts.aws.shared.infrastructure.Controllers
import com.jubel.shortcuts.aws.shared.infrastructure.Message
import com.jubel.shortcuts.aws.timer.application.LaunchPeriodicTasks
import com.jubel.shortcuts.aws.timer.domain.MyTimer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import spark.Response
import spark.Spark
import spark.Spark.awaitInitialization
import spark.kotlin.after
import spark.kotlin.port

class ShortcutsAWS {

    suspend fun start(): Injector{

        port(3355)

        val injector = Guice.createInjector()
        Controllers().create(injector)

        setDefaultContentType()
        exceptionsHandlers(injector)

        awaitInitialization()

        val myTimer = injector.getInstance(MyTimer::class.java)
        val launchPeriodicTasks = injector.getInstance(LaunchPeriodicTasks::class.java)
        myTimer.runPeriodicAsync(60, launchPeriodicTasks::run)

        return injector

    }

    private fun setDefaultContentType() {
        after{
            if (response.type() == null){
                response.type("application/json")
            }
        }
    }

    private fun exceptionsHandlers(injector: Injector){
        Spark.exception(ExpiredTokenException::class.java) { _, _, response ->

            val message = "Expired token!! Update credentials"
            val sendSimpleMessageNotification = injector.getInstance(SendSimpleMessageNotification::class.java)
            sendSimpleMessageNotification.run(message)
            response.status(500)
            setExceptionContentTypeAndMessage(response, message)
        }

        Spark.exception(Exception::class.java) { exception, _, response ->
            exception.printStackTrace()
            val message = "UnexpectedException!! ${exception.javaClass.name} --> ${exception.message}"
            val sendSimpleMessageNotification = injector.getInstance(SendSimpleMessageNotification::class.java)
            sendSimpleMessageNotification.run(message)
            response.status(500)
            setExceptionContentTypeAndMessage(response, message)
        }
    }

    private fun setExceptionContentTypeAndMessage(response: Response, message: String?) {
        response.type("application/json")
        val messageObj = Message(message ?: "NO MESSAGE")
        response.body(Json.encodeToString(messageObj))
    }
}