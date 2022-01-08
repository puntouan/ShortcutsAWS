package com.jubel.shortcuts.aws.pullrequest.infrastructure

import com.jubel.shortcuts.aws.pullrequest.application.DashboardActivity
import spark.kotlin.get
import javax.inject.Inject

class DashboardActivityController @Inject constructor(
    private val dashboardActivity: DashboardActivity
) {

    init{
        get("dashboard/activity/:pullRequestId"){
            response.apply {
                val pullRequestId = request.params(":pullRequestId")
                val html = dashboardActivity.run(pullRequestId)
                type("text/html; charset=UTF-8")
                body(html)
            }
        }
    }

}