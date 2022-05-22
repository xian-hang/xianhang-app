package com.example.xianhang.network

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

const val MESSAGE = "message"
class SendWorker(ctx: Context, params: WorkerParameters): Worker(ctx, params) {
    override fun doWork(): Result {
        return try {
            val message = inputData.getString(MESSAGE)
            webSocket.send(message!!)
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}