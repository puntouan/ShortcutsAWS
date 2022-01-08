package com.jubel.shortcuts.aws.pullrequest.infrastructure

import com.jubel.shortcuts.aws.pullrequest.application.GetActivityForPullRequest
import com.jubel.shortcuts.aws.pullrequest.application.ListOpenPRs
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import spark.Request
import spark.kotlin.get
import javax.inject.Inject

class GetActivityForPullRequestController @Inject constructor(
    private val getActivityForPullRequest: GetActivityForPullRequest
) {

    init{
        get("pr/activity/:pullRequestId"){
            getActivityForPullRequest(request)
        }
    }

    private fun getActivityForPullRequest(request: Request): Any{
        val pullRequestId = request.params(":pullRequestId")
        return Json.encodeToString(getActivityForPullRequest.run(pullRequestId))
    }

}