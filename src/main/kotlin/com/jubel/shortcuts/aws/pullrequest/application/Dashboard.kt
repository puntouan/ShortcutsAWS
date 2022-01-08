package com.jubel.shortcuts.aws.pullrequest.application

import com.google.inject.Inject
import com.jubel.shortcuts.aws.pullrequest.domain.PullRequest
import com.jubel.shortcuts.aws.shared.domain.ConfigRepository
import java.time.format.DateTimeFormatter

class Dashboard @Inject constructor(
    private val configRepository: ConfigRepository,
    private val listOpenPRs: ListOpenPRs
) {

    companion object{
        val dashboard = Dashboard::class.java.classLoader.getResource("shortcuts.codecommit.pullrequests.html")!!.readText()
        val authorTable = Dashboard::class.java.classLoader.getResource("shortcuts.codecommit.pullrequests.author.html")!!.readText()
        val authorTableRow = Dashboard::class.java.classLoader.getResource("shortcuts.codecommit.pullrequests.author.row.html")!!.readText()
    }

    fun run(): String{
        val pullRequestsMap = listOpenPRs.run()

        return dashboard
            .replace("{url_count_approvals}","approvals/")
            .replace("{url_subscribe_to_pull_request}","subscribe/pr/")
            .replace("{url_unsubscribe_to_pull_request}","unsubscribe/pr/")
            .replace("{url_get_activity}","dashboard/activity/")
            .replace("{pull_request_authors}", pullRequestsMap.map {
            getAuthorTable(it.key, it.value)
        }.joinToString(""))
    }

    private fun getAuthorTable(author: String, pullRequests: List<PullRequest>): String{
        return authorTable
            .replace("{pull_request_author}", author)
            .replace("{pull_request_rows}", pullRequests.joinToString("") {
                getAuthorTableRow(it)
            })
    }

    private fun getAuthorTableRow(pullRequest: PullRequest): String{

        val isSubscribed = configRepository.getPullRequestIdsSubscriptions().contains(pullRequest.id)

        val subscriptionClass = if (isSubscribed) "table-success" else "table-secondary"
        val subscriptionStatus = if (isSubscribed) "ON" else "OFF"
        val displaySubscriptionButton = if (isSubscribed) "style=\"display:none;\"" else ""
        val displayUnsubscriptionButton = if (isSubscribed) "" else "style=\"display:none;\""

        return authorTableRow
            .replace("{pull_request_subscription_class}", subscriptionClass)
            .replace("{pull_request_creation_date}", pullRequest.creation.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .replace("{pull_request_branch_name}", pullRequest.branchName)
            .replace("{pull_request_url}", pullRequest.url)
            .replace("{pull_request_id}", pullRequest.id)
            .replace("{pull_request_repo_name}", pullRequest.repoName)
            .replace("{pull_request_last_activity}", pullRequest.lastActivity.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .replace("{pull_request_title}", pullRequest.title)
            .replace("{pull_request_subscription_status}", subscriptionStatus)
            .replace("{display_subscription_button}", displaySubscriptionButton)
            .replace("{display_unsubscription_button}", displayUnsubscriptionButton)
    }


}