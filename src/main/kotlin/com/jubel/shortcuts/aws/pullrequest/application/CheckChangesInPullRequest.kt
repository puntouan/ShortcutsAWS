package com.jubel.shortcuts.aws.pullrequest.application

import com.google.inject.Inject
import com.jubel.shortcuts.aws.notification.application.SendAutomaticUnsubscriptionOnPullRequestNotification
import com.jubel.shortcuts.aws.notification.application.SendChangeOnPullRequestNotification
import com.jubel.shortcuts.aws.pullrequest.domain.PullRequest
import com.jubel.shortcuts.aws.shared.domain.CodeCommitRepository
import com.jubel.shortcuts.aws.shared.domain.ConfigRepository

class CheckChangesInPullRequest @Inject constructor(
    private val configRepository: ConfigRepository,
    private val codeCommitRepository: CodeCommitRepository,
    private val sendChangeOnPullRequestNotification: SendChangeOnPullRequestNotification,
    private val sendAutomaticUnsubscriptionOnPullRequestNotification: SendAutomaticUnsubscriptionOnPullRequestNotification
) {

    fun run(id: String){

        println("Checking changes for PR: $id")
        val pullRequest = codeCommitRepository.getPullRequestById(id)
        if (pullRequest.isNotOpen()){
            configRepository.removePullRequestSubscription(id)
            sendAutomaticUnsubscriptionOnPullRequestNotification.run(pullRequest.id, pullRequest.repoName, pullRequest.author, pullRequest.url)
            return
        }
        handleOpenPullRequest(pullRequest)
    }

    private fun handleOpenPullRequest(pullRequest: PullRequest){
        val subscription = configRepository.getPullRequestSubscription(pullRequest.id)

        val currentEvents = codeCommitRepository.countEventsInPullRequest(pullRequest.id)
        val currentComments = codeCommitRepository.countCommentsInPullRequest(pullRequest.id)
        val currentStatus = "$currentEvents||$currentComments"

        if (subscription.status != currentStatus){
            if (!subscription.isInitialStatus()){
                sendChangeOnPullRequestNotification.run(pullRequest.id, pullRequest.repoName, pullRequest.author, pullRequest.title, pullRequest.url)
            }
            subscription.status = currentStatus
            configRepository.saveConfig()
            println("Updating status of PR: ${pullRequest.id} in config file")
        }
    }

}