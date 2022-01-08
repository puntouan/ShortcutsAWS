package com.jubel.shortcuts.aws.shared.infrastructure

import com.google.inject.Injector
import com.jubel.shortcuts.aws.pullrequest.infrastructure.*

class Controllers {

    fun create(injector: Injector){

        injector.getInstance(ListOpenPRsController::class.java)
        injector.getInstance(SubscribeToPullRequestController::class.java)
        injector.getInstance(UnsubscribeToPullRequestController::class.java)
        injector.getInstance(DashboardController::class.java)
        injector.getInstance(CountApprovalsForPullRequestController::class.java)
        injector.getInstance(GetActivityForPullRequestController::class.java)
        injector.getInstance(DashboardActivityController::class.java)

    }

}