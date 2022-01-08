package com.jubel.shortcuts.aws.pullrequest.infrastructure

import com.jubel.shortcuts.aws.pullrequest.application.Dashboard
import com.jubel.shortcuts.aws.pullrequest.application.ListOpenPRs
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import spark.Request
import spark.kotlin.get
import javax.inject.Inject

class DashboardController @Inject constructor(
    private val dashboard: Dashboard
) {

    init{
        get("dashboard"){
            response.apply {
                type("text/html; charset=UTF-8")
                val html = dashboard.run()
                body(html)
            }
        }
    }

}