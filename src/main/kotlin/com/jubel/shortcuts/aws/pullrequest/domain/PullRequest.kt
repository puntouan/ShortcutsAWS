package com.jubel.shortcuts.aws.pullrequest.domain

import com.jubel.shortcuts.aws.shared.domain.ZonedDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Serializable
class PullRequest private constructor(
    val id: String,
    val title: String,
    @Serializable(with = ZonedDateTimeSerializer::class)
    val lastActivity: ZonedDateTime,
    @Serializable(with = ZonedDateTimeSerializer::class)
    val creation: ZonedDateTime,
    val status: String,
    val author: String,
    val repoName: String,
    val branchName: String,
    val url: String
){
    constructor(
        id: String,
        title: String,
        lastActivity: ZonedDateTime,
        creation: ZonedDateTime,
        status: String,
        author: String,
        repoName: String,
        branchName: String,
    ) : this(
        id, title, lastActivity, creation,status, author, repoName, branchName,
        "https://us-west-2.console.aws.amazon.com/codesuite/codecommit/repositories/${repoName}/pull-requests/${id}/details"
    )

    fun isOpen(): Boolean{
        return status.equals("OPEN", true)
    }

    fun isNotOpen(): Boolean{
        return !isOpen()
    }
}
