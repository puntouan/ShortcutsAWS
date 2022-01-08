package com.jubel.shortcuts.aws.pullrequest.application

import com.google.inject.Inject
import com.jubel.shortcuts.aws.pullrequest.domain.PullRequestActivity
import com.jubel.shortcuts.aws.pullrequest.domain.PullRequestComment
import com.jubel.shortcuts.aws.shared.domain.CodeCommitRepository

class GetActivityForPullRequest @Inject constructor(
    private val codeCommitRepository: CodeCommitRepository
) {

    fun run(pullRequestId: String): List<PullRequestActivity>{

        val activity = codeCommitRepository.getEventsInPullRequest(pullRequestId)
        val comments = codeCommitRepository.getCommentsInPullRequest(pullRequestId)
        return sortAndFlat(activity, comments)


    }

    private fun sortAndFlat(activityList: List<PullRequestActivity>, comments: List<PullRequestComment>): List<PullRequestActivity>{

        val mutActivity = activityList.toMutableList()
        val mutComments = comments.toMutableList()
        mutActivity.sortBy { it.date }
        mutComments.sortBy { it.date }

        val rows = mutableListOf<PullRequestActivity>()

        while (mutActivity.isNotEmpty() || mutComments.isNotEmpty()){
            val activity = mutActivity.firstOrNull()
            val comment = mutComments.firstOrNull()
            if (activity != null && comment != null){
                if (activity.date.isBefore(comment.date)){
                    rows.add(activity)
                    mutActivity.remove(activity)
                }else{
                    rows.addAll(comment.toPullRequestActivity())
                    mutComments.remove(comment)
                }
            }else if (activity != null){
                rows.add(activity)
                mutActivity.remove(activity)
            }else{
                rows.addAll(comment!!.toPullRequestActivity())
                mutComments.remove(comment)
            }
        }

        return rows
    }

}