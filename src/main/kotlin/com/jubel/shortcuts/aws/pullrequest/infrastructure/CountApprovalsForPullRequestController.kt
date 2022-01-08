package com.jubel.shortcuts.aws.pullrequest.infrastructure

import com.jubel.shortcuts.aws.pullrequest.application.CountApprovalsForPullRequest
import com.jubel.shortcuts.aws.pullrequest.application.SubscribeToPullRequest
import spark.Request
import spark.kotlin.get
import spark.kotlin.put
import javax.inject.Inject

class CountApprovalsForPullRequestController @Inject constructor(
    private val countApprovalsForPullRequest: CountApprovalsForPullRequest
) {

    init{
        get("approvals/:pullRequestId"){
            countApprovals(request)
        }
    }

    private fun countApprovals(request: Request): Any{
        val pullRequestId = request.params(":pullRequestId")
        return countApprovalsForPullRequest.run(pullRequestId).toString()
    }

}