package com.jubel.shortcuts.aws.shared.infrastructure

import com.google.inject.Singleton
import com.jubel.shortcuts.aws.shared.domain.Config
import com.jubel.shortcuts.aws.shared.domain.ConfigRepository
import com.jubel.shortcuts.aws.shared.domain.PullRequestSubscription
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

@Singleton
class FileConfigRepository: ConfigRepository {

    private val fileName = "${System.getProperty("user.home")}/.shortcutsAws/config.json"
    private var config: Config? = null

    private fun getConfig(): Config{
        if (config == null){
            loadConfig()
        }
        return config!!
    }

    private fun loadConfig(){
        val content = File(fileName).readText()
        config =  Json.decodeFromString(content)
    }

    override fun saveConfig(){
        val content = Json.encodeToString(config)
        File(fileName).writeText(content)
    }

    override fun getChatUrl(): String {
        return getConfig().googleChatUrl
    }

    override fun getMyRepoNames(): List<String> {
        return getConfig().repos
    }

    override fun getMyPartners(): List<String> {
        return getConfig().authors
    }

    override fun getPullRequestSubscription(pullRequestId: String): PullRequestSubscription {
        return getConfig().getPullRequestSubscription(pullRequestId)
    }

    override fun getPullRequestIdsSubscriptions(): List<String> {
        return getConfig().getPullRequestIdsSubscriptions()
    }

    override fun addPullRequestSubscription(pullRequestId: String){
        getConfig().addPullRequestSubscription(pullRequestId)
        saveConfig()
    }

    override fun removePullRequestSubscription(pullRequestId: String) {
        getConfig().removePullRequestSubscription(pullRequestId)
        saveConfig()
    }

    override fun areNotificationsEnabled(): Boolean {
        return getConfig().notificationsEnabled
    }
}