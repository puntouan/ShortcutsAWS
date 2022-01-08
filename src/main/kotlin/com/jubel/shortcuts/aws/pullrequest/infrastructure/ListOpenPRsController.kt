package com.jubel.shortcuts.aws.pullrequest.infrastructure

import com.jubel.shortcuts.aws.pullrequest.application.ListOpenPRs
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import spark.Request
import spark.kotlin.get
import javax.inject.Inject

class ListOpenPRsController @Inject constructor(
    private val listOpenPRs: ListOpenPRs
) {

    init{
        get("prs"){
            listOpenPRs(request)
        }
    }

    private fun listOpenPRs(request: Request): Any{
        val map = listOpenPRs.run()
        return Json.encodeToString(map)
    }

}