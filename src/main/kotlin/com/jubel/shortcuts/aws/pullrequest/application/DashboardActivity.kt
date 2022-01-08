package com.jubel.shortcuts.aws.pullrequest.application

import com.google.inject.Inject
import com.jubel.shortcuts.aws.pullrequest.domain.PullRequestActivity
import java.time.format.DateTimeFormatter

class DashboardActivity @Inject constructor(
    private val getActivityForPullRequest: GetActivityForPullRequest
) {

    companion object{
        val activityTableRow = DashboardActivity::class.java.classLoader.getResource("shortcuts.codecommit.pullrequests.author.row.activity.row.html")!!.readText()
    }

    fun run(pullRequestId: String): String{
        return getActivityForPullRequest.run(pullRequestId).joinToString("") { getActivityTableRow(it) }
    }

     private fun getActivityTableRow(activity: PullRequestActivity): String{
        return activityTableRow
            .replace("{pull_request_activity_who}", activity.author)
            .replace("{pull_request_activity_when}", activity.date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .replace("{pull_request_activity_what}", activity.type)
            .replace("{pull_request_activity_whatElse}", activity.description)
    }

}