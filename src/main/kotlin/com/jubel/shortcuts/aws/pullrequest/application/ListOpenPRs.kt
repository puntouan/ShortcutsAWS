package com.jubel.shortcuts.aws.pullrequest.application

import com.google.inject.Inject
import com.jubel.shortcuts.aws.pullrequest.domain.PullRequest
import com.jubel.shortcuts.aws.shared.domain.CodeCommitRepository
import com.jubel.shortcuts.aws.shared.domain.ConfigRepository
import kotlinx.coroutines.runBlocking

class ListOpenPRs @Inject constructor(
    private val configRepository: ConfigRepository,
    private val codeCommitRepository: CodeCommitRepository
) {

    fun run(): Map<String, List<PullRequest>>{
        val myRepos = configRepository.getMyRepoNames()

        val pullRequests = runBlocking {
            codeCommitRepository.getOpenPullRequestsForRepos(myRepos)
        }

        val authors = configRepository.getMyPartners()
        return authors.associateWith { author -> pullRequests.filter { it.author == author } }
    }

}