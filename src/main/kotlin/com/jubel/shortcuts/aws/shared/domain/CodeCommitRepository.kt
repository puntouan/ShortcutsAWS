package com.jubel.shortcuts.aws.shared.domain

import com.google.inject.ImplementedBy
import com.jubel.shortcuts.aws.pullrequest.domain.PullRequest
import com.jubel.shortcuts.aws.pullrequest.domain.PullRequestActivity
import com.jubel.shortcuts.aws.pullrequest.domain.PullRequestComment
import com.jubel.shortcuts.aws.shared.infrastructure.CliCodeCommitRepository

@ImplementedBy(CliCodeCommitRepository::class)
interface CodeCommitRepository {

    fun getAllRepositoryNames(): List<String>
    fun getOpenPullRequestIdsForRepo(repoName: String): List<String>
    suspend fun getOpenPullRequestIdsForRepos(repoNames: List<String>): List<String>

    fun getPullRequestById(id: String): PullRequest
    suspend fun getOpenPullRequestsForRepos(repoNames: List<String>): List<PullRequest>
    fun countEventsInPullRequest(pullRequestId: String): Int
    fun countCommentsInPullRequest(pullRequestId: String): Int
    fun countApprovalsInPullRequest(pullRequestId: String): Int
    fun getEventsInPullRequest(pullRequestId: String): List<PullRequestActivity>
    fun getCommentsInPullRequest(pullRequestId: String): List<PullRequestComment>

}

class ExpiredTokenException: RuntimeException()