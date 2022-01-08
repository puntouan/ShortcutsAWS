package com.jubel.shortcuts.aws.pullrequest.application

import com.google.inject.Inject
import com.jubel.shortcuts.aws.shared.domain.ConfigRepository

class SubscribeToPullRequest @Inject constructor(
    private val configRepository: ConfigRepository
)  {

    fun run(pullRequestId: String){
        configRepository.addPullRequestSubscription(pullRequestId)
    }

}