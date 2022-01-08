package com.jubel.shortcuts.aws.notification.infrastructure

import com.google.inject.Guice
import com.jubel.shortcuts.aws.notification.application.SendSimpleMessageNotification
import com.jubel.shortcuts.aws.pullrequest.application.GetActivityForPullRequest
import com.jubel.shortcuts.aws.pullrequest.application.ListOpenPRs
import com.jubel.shortcuts.aws.pullrequest.infrastructure.ListOpenPRsController
import com.jubel.shortcuts.aws.shared.domain.CodeCommitRepository
import com.jubel.shortcuts.aws.shared.domain.ConfigRepository
import com.jubel.shortcuts.aws.shared.domain.ExpiredTokenException
import com.jubel.shortcuts.aws.shared.infrastructure.FileConfigRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

internal class GoogleChatNotifierShould{


    //@Test
    fun miTest(){

        val configRepository = FileConfigRepository()
        val chatNotifier = GoogleChatNotifier(configRepository)

        chatNotifier.sendMessage("Mi pruebahfjfghjfghjfgj")

        println("Fin!!")

    }

    //@Test
    fun listOpenPRsTest(){

        val injector = Guice.createInjector()
        val configRepository = injector.getInstance(ConfigRepository::class.java)
        val codeCommitRepository = injector.getInstance(CodeCommitRepository::class.java)

        val listOpenPRs = ListOpenPRs(configRepository, codeCommitRepository)

        try{
            listOpenPRs.run()
        }catch (ex: ExpiredTokenException){
            val sendSimpleMessageNotification = injector.getInstance(SendSimpleMessageNotification::class.java)
            sendSimpleMessageNotification.run("Expired token!! Update credentials")
        }

    }

    //@Test
    fun getActivityForPullRequest(){
        val injector = Guice.createInjector()
        val codeCommitRepository = injector.getInstance(CodeCommitRepository::class.java)

        val getActivityForPullRequest = GetActivityForPullRequest(codeCommitRepository)

        val res = getActivityForPullRequest.run("4031")

        println(" --> ${Json.encodeToString(res)}")
    }

}