package com.jubel.shortcuts.aws.pullrequest.application

import com.google.inject.Inject
import com.jubel.shortcuts.aws.pullrequest.domain.PullRequest
import com.jubel.shortcuts.aws.shared.domain.CodeCommitRepository
import com.jubel.shortcuts.aws.shared.domain.ConfigRepository
import kotlinx.coroutines.runBlocking

class CountApprovalsForPullRequest @Inject constructor(
    private val codeCommitRepository: CodeCommitRepository
) {

    fun run(pullRequestId: String): Int{

        return codeCommitRepository.countApprovalsInPullRequest(pullRequestId)

    }

}