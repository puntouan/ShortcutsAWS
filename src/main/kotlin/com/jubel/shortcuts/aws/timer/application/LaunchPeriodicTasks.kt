package com.jubel.shortcuts.aws.timer.application

import com.google.inject.Inject
import com.jubel.shortcuts.aws.pullrequest.application.CheckChangesInPullRequest
import com.jubel.shortcuts.aws.shared.domain.ConfigRepository

class LaunchPeriodicTasks @Inject constructor(
    private val configRepository: ConfigRepository,
    private val checkChangesInPullRequest: CheckChangesInPullRequest
) {


    fun run(){

        val pullRequestIds = configRepository.getPullRequestIdsSubscriptions()

        pullRequestIds.forEach {
            if (isStillActive(it)){
                checkChangesInPullRequest.run(it)
            }
        }

    }

    private fun isStillActive(pullRequestId: String): Boolean{
        return configRepository.getPullRequestIdsSubscriptions().contains(pullRequestId)
    }

}