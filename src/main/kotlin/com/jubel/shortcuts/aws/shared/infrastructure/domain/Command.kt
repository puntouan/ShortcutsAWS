package com.jubel.shortcuts.aws.shared.infrastructure.domain

import com.jubel.shortcuts.aws.shared.domain.ExpiredTokenException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import java.io.BufferedReader

class Command(str: String) {

    companion object{
        private const val MAX_ATTEMPTS = 5
    }

    private val parts = str.split(" ").toTypedArray()

    private fun execute(): String{

        var attempts = 0
        var response = ""
        var error = ""

        while (response.isBlank() && attempts <= MAX_ATTEMPTS){
            attempts++
            val process = Runtime.getRuntime().exec(parts)
            error = process.errorStream.bufferedReader().use(BufferedReader::readText)
            if (error.contains("ExpiredTokenException")){
                throw ExpiredTokenException()
            }
            response = process.inputStream.bufferedReader().use(BufferedReader::readText)
        }
        if (attempts > MAX_ATTEMPTS){
            println("Number of attempts exceeded. Last error: $error")
        }

        return response
    }

    fun executeAndGetJson(): JsonElement = Json.parseToJsonElement(execute())

}