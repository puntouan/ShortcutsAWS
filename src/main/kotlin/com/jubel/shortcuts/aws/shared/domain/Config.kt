package com.jubel.shortcuts.aws.shared.domain

import com.jubel.shortcuts.aws.shared.domain.Subscriptions.Companion.INITIAL_STATUS
import kotlinx.serialization.Serializable

@Serializable
class Config (
    val repos: List<String>,
    val authors: List<String>,
    val notificationsEnabled: Boolean,
    val googleChatUrl: String,
    val subscriptions: Subscriptions
){
    fun getPullRequestSubscription(pullRequestId: String): PullRequestSubscription{
        return subscriptions.getPullRequestSubscription(pullRequestId)
    }

    fun getPullRequestIdsSubscriptions(): List<String>{
        return subscriptions.getPullRequestIdsSubscriptions()
    }

    fun addPullRequestSubscription(pullRequestId: String){
        subscriptions.addPullRequestSubscription(pullRequestId)
    }

    fun removePullRequestSubscription(pullRequestId: String){
        subscriptions.removePullRequestSubscription(pullRequestId)
    }
}

@Serializable
class Subscriptions(
    private val pullRequestSubscriptions: MutableList<PullRequestSubscription>
){
    companion object{
        const val INITIAL_STATUS: String = "INITIAL_STATUS"
    }

    fun getPullRequestSubscription(pullRequestId: String): PullRequestSubscription{
        return pullRequestSubscriptions.first { it.id ==  pullRequestId}
    }

    fun getPullRequestIdsSubscriptions(): List<String>{
        return pullRequestSubscriptions.map { it.id }
    }

    private fun existsPullRequestSubscription(pullRequestId: String): Boolean{
        return pullRequestSubscriptions.any { it.id == pullRequestId }
    }

    fun addPullRequestSubscription(pullRequestId: String){
        if (existsPullRequestSubscription(pullRequestId)){
            return
        }
        pullRequestSubscriptions.add(PullRequestSubscription(pullRequestId, INITIAL_STATUS))
    }

    fun removePullRequestSubscription(pullRequestId: String){
        if (!existsPullRequestSubscription(pullRequestId)){
            return
        }
        pullRequestSubscriptions.removeIf { it.id == pullRequestId }
    }
}

@Serializable
class PullRequestSubscription(
    val id: String,
    var status: String
){

    fun isInitialStatus(): Boolean{
        return status == INITIAL_STATUS
    }

}