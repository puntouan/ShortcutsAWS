package com.jubel.shortcuts.aws.pullrequest.domain

import com.jubel.shortcuts.aws.shared.domain.ZonedDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Serializable
class PullRequestActivity(
    val author: String,
    @Serializable(with = ZonedDateTimeSerializer::class)
    val date: ZonedDateTime,
    val type: String,
    val description: String
)

