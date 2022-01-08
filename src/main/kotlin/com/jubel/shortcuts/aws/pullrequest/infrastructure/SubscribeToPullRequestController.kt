package com.jubel.shortcuts.aws.pullrequest.infrastructure

import com.jubel.shortcuts.aws.pullrequest.application.SubscribeToPullRequest
import spark.Request
import spark.kotlin.put
import javax.inject.Inject

class SubscribeToPullRequestController @Inject constructor(
    private val subscribeToPullRequest: SubscribeToPullRequest
) {

    init{
        put("subscribe/pr/:pullRequestId"){
            subscribeToPullRequest(request)
        }
    }

    private fun subscribeToPullRequest(request: Request): Any{
        val pullRequestId = request.params(":pullRequestId")
        subscribeToPullRequest.run(pullRequestId)
        return "OK"
    }

}