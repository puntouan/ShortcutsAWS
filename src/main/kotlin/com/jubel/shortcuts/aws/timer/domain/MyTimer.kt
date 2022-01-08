package com.jubel.shortcuts.aws.timer.domain

import com.google.inject.Inject
import com.jubel.shortcuts.aws.notification.application.SendSimpleMessageNotification
import com.jubel.shortcuts.aws.shared.domain.ExpiredTokenException
import kotlinx.coroutines.*

class MyTimer @Inject constructor(
    private val sendSimpleMessageNotification: SendSimpleMessageNotification
) {

    suspend fun runPeriodicAsync(secondsPeriod: Long, syncFun: () -> Unit){
        withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
            try{
                if (secondsPeriod > 0){
                    while (true){
                        try{
                            syncFun()
                        }catch (exception:Exception){
                            handleException(exception)
                        }
                        delay(secondsPeriod * 1000)
                    }
                }else{
                    syncFun()
                }
            }catch (exception: Exception){
                handleException(exception)
            }
        }
    }

    private fun handleException(exception: Exception){
        when(exception){
            is ExpiredTokenException -> sendSimpleMessageNotification.run("Expired token!! Update credentials")
            else -> {
                val message = "UnexpectedException!! ${exception.javaClass.name} --> ${exception.message}"
                sendSimpleMessageNotification.run(message)
            }
        }

    }

}