package com.jubel.shortcuts.aws.pullrequest.infrastructure

import com.jubel.shortcuts.aws.pullrequest.application.UnsubscribeToPullRequest
import spark.Request
import spark.kotlin.put
import javax.inject.Inject

class UnsubscribeToPullRequestController @Inject constructor(
    private val unsubscribeToPullRequest: UnsubscribeToPullRequest
) {

    init{
        put("unsubscribe/pr/:pullRequestId"){
            unsubscribeToPullRequest(request)
        }
    }

    private fun unsubscribeToPullRequest(request: Request): Any{
        val pullRequestId = request.params(":pullRequestId")
        unsubscribeToPullRequest.run(pullRequestId)
        return "OK"
    }

}