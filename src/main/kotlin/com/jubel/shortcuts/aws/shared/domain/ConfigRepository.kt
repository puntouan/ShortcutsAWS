package com.jubel.shortcuts.aws.shared.domain

import com.google.inject.ImplementedBy
import com.jubel.shortcuts.aws.shared.infrastructure.FileConfigRepository

@ImplementedBy(FileConfigRepository::class)
interface ConfigRepository {

    fun getMyRepoNames(): List<String>
    fun getMyPartners(): List<String>
    fun getPullRequestSubscription(pullRequestId: String): PullRequestSubscription
    fun getPullRequestIdsSubscriptions(): List<String>
    fun saveConfig()
    fun getChatUrl(): String
    fun addPullRequestSubscription(pullRequestId: String)
    fun removePullRequestSubscription(pullRequestId: String)
    fun areNotificationsEnabled(): Boolean

    fun areNotificationsDisabled(): Boolean{
        return !areNotificationsEnabled()
    }

}