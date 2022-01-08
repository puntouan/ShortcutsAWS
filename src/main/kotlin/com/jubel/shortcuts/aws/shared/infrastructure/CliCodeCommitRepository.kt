package com.jubel.shortcuts.aws.shared.infrastructure

import com.jubel.shortcuts.aws.pullrequest.domain.*
import com.jubel.shortcuts.aws.shared.domain.CodeCommitRepository
import com.jubel.shortcuts.aws.shared.infrastructure.domain.Command
import kotlinx.coroutines.*
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.time.ZonedDateTime

class CliCodeCommitRepository : CodeCommitRepository {


    private fun <T> runAsync(syncFun: () -> T): Deferred<T>{
        return CoroutineScope(Dispatchers.IO).async {
            syncFun()
        }
    }

    override fun getAllRepositoryNames(): List<String>{
        val listRepositoriesCmd = Command("aws codecommit list-repositories")
        val json = listRepositoriesCmd.executeAndGetJson()

        return json.jsonObject["repositories"]!!.jsonArray.map {
            it.jsonObject["repositoryName"]!!.jsonPrimitive.content
        }
    }


    override fun getOpenPullRequestIdsForRepo(repoName: String): List<String>{
        val listPullRequestIdsCmd = Command("""
            aws codecommit list-pull-requests --repository-name $repoName --pull-request-status OPEN
        """.trimIndent())

        val json = listPullRequestIdsCmd.executeAndGetJson()
        return json.jsonObject["pullRequestIds"]!!.jsonArray.map {
            it.jsonPrimitive.content
        }
    }

    override suspend fun getOpenPullRequestIdsForRepos(repoNames: List<String>): List<String> {
        return repoNames.map {
            runAsync {
                getOpenPullRequestIdsForRepo(it)
            }
        }.awaitAll().flatten()
    }


    override fun getPullRequestById(id: String): PullRequest {

        val json = Command("aws codecommit get-pull-request --pull-request-id $id").executeAndGetJson()
        val prJsonObject = json.jsonObject["pullRequest"]!!.jsonObject
        val title = prJsonObject["title"]!!.jsonPrimitive.content
        val lastActivity = ZonedDateTime.parse(prJsonObject["lastActivityDate"]!!.jsonPrimitive.content)
        val creation = ZonedDateTime.parse(prJsonObject["creationDate"]!!.jsonPrimitive.content)
        val status = prJsonObject["pullRequestStatus"]!!.jsonPrimitive.content
        val author = prJsonObject["authorArn"]!!.jsonPrimitive.content.split("/").last()
        val targetJsonElement = prJsonObject["pullRequestTargets"]!!.jsonArray.first().jsonObject
        val repoName = targetJsonElement["repositoryName"]!!.jsonPrimitive.content
        val branchName = targetJsonElement["sourceReference"]!!.jsonPrimitive.content.removePrefix("refs/heads/")

        return PullRequest(id, title, lastActivity, creation, status, author, repoName, branchName)
    }

    override suspend fun getOpenPullRequestsForRepos(repoNames: List<String>): List<PullRequest>{
        val pullRequestIds = getOpenPullRequestIdsForRepos(repoNames)
        return pullRequestIds.map {
            runAsync { getPullRequestById(it) }
        }.awaitAll()
    }

    override fun countEventsInPullRequest(pullRequestId: String): Int{
        val describePullRequestEventsCmd = Command("""
            aws codecommit describe-pull-request-events --pull-request-id $pullRequestId
        """.trimIndent())

        val json = describePullRequestEventsCmd.executeAndGetJson()
        return json.jsonObject["pullRequestEvents"]!!.jsonArray.size
    }

    override fun countCommentsInPullRequest(pullRequestId: String): Int{
        val getCommentsForPullRequestCmd = Command("""
            aws codecommit get-comments-for-pull-request --pull-request-id $pullRequestId
        """.trimIndent())

        val json = getCommentsForPullRequestCmd.executeAndGetJson()
        return json.jsonObject["commentsForPullRequestData"]!!.jsonArray.size
    }

    override fun countApprovalsInPullRequest(pullRequestId: String): Int {
        val describePullRequestEventsCmd = Command("""
            aws codecommit describe-pull-request-events --pull-request-id $pullRequestId
        """.trimIndent())

        val json = describePullRequestEventsCmd.executeAndGetJson()

        return json.jsonObject["pullRequestEvents"]!!.jsonArray.filter {
            it.toString().contains("APPROVE")
        }.size
    }

    override fun getEventsInPullRequest(pullRequestId: String): List<PullRequestActivity> {

        val describePullRequestEventsCmd = Command("""
            aws codecommit describe-pull-request-events --pull-request-id $pullRequestId
        """.trimIndent())

        val json = describePullRequestEventsCmd.executeAndGetJson()

        return json.jsonObject["pullRequestEvents"]!!.jsonArray
            .map { it.jsonObject }
            .map {jsonObject ->
            val date = ZonedDateTime.parse(jsonObject["eventDate"]!!.jsonPrimitive.content)
            val type = jsonObject["pullRequestEventType"]!!.jsonPrimitive.content
            val actorArn = jsonObject["actorArn"]!!.jsonPrimitive.content
            val author = actorArn.substring(actorArn.lastIndexOf("/") + 1)
            val description = if (jsonObject["approvalStateChangedEventMetadata"] != null)
                    jsonObject["approvalStateChangedEventMetadata"]!!.jsonObject["approvalStatus"]!!.jsonPrimitive.content
                else ""
            PullRequestActivity(author, date, type, description)
        }

    }

    override fun getCommentsInPullRequest(pullRequestId: String): List<PullRequestComment> {

        val describePullRequestEventsCmd = Command("""
            aws codecommit get-comments-for-pull-request --pull-request-id $pullRequestId
        """.trimIndent())

        val json = describePullRequestEventsCmd.executeAndGetJson()

        val rawComments = json.jsonObject["commentsForPullRequestData"]!!.jsonArray
            .flatMap{ it.jsonObject["comments"]!!.jsonArray }
            .map { it.jsonObject }
            .map {jsonObject ->
                val commentId = jsonObject["commentId"]!!.jsonPrimitive.content
                val inReplyTo = jsonObject["inReplyTo"]?.jsonPrimitive?.content
                val authorArn = jsonObject["authorArn"]!!.jsonPrimitive.content
                val author = authorArn.substring(authorArn.lastIndexOf("/") + 1)
                val date = ZonedDateTime.parse(jsonObject["creationDate"]!!.jsonPrimitive.content)
                val description = jsonObject["content"]!!.jsonPrimitive.content
                PullRequestRawComment(commentId, inReplyTo, author, date, description)
            }

        return rawComments.toStructuredComments()
    }
}