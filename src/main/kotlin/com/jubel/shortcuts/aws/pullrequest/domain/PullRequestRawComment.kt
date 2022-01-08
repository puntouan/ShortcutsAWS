package com.jubel.shortcuts.aws.pullrequest.domain

import com.jubel.shortcuts.aws.shared.domain.ZonedDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Serializable
class PullRequestRawComment(
    val commentId: String,
    val inReplyTo: String?,
    val author: String,
    @Serializable(with = ZonedDateTimeSerializer::class)
    val date: ZonedDateTime,
    val description: String
){

    fun toPullRequestComment(): PullRequestComment{
        return PullRequestComment(commentId, author, date, description)
    }
}

fun List<PullRequestRawComment>.toStructuredComments(): List<PullRequestComment> {

    val leftover = this.sortedBy { it.date }.toMutableList()

    val structuredComments = leftover.filter { it.inReplyTo == null }.map { it.toPullRequestComment() }
    var toDelete = structuredComments.map { it.commentId }
    leftover.removeIf { toDelete.contains(it.commentId) }

    while (leftover.isNotEmpty()){
        toDelete = mutableListOf()
        leftover.forEach { raw ->
            val parent = structuredComments.getParentFor(raw.inReplyTo!!)
            if (parent != null){
                parent.addChild(raw.toPullRequestComment())
                toDelete.add(raw.commentId)
            }
        }
        leftover.removeIf { toDelete.contains(it.commentId) }
    }
    return structuredComments

}


class PullRequestComment(
    val commentId: String,
    private val author: String,
    @Serializable(with = ZonedDateTimeSerializer::class)
    val date: ZonedDateTime,
    private val description: String,
    val children: MutableList<PullRequestComment> = mutableListOf()
){

    fun addChild(comment: PullRequestComment){
        children.add(comment)
    }

    fun toPullRequestActivity(): List<PullRequestActivity>{
        return toPullRequestActivity(0)
    }

    private fun toPullRequestActivity(level: Int): List<PullRequestActivity>{

        val activity = mutableListOf<PullRequestActivity>()

        val nestingMark = (0 until level).map { "|-->" }.joinToString("")
        activity.add(
            PullRequestActivity(author, date, "${nestingMark}COMMENT", description)
        )
        children.forEach {
            activity.addAll(it.toPullRequestActivity(level + 1))
        }

        return activity
    }
}

fun List<PullRequestComment>.getParentFor(id: String): PullRequestComment? {
    val res = this.firstOrNull{it.commentId == id}
    if (res != null){
        return res
    }
    this.forEach {
        val parent = it.children.getParentFor(id)
        if (parent != null){
            return parent
        }
    }
    return null
}