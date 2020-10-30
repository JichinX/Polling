package me.xujichang.lib.polling

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import me.xujichang.lib.polling.jobs.JobPool
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit

/**
 *project：Polling
 *full_path：me.xujichang.lib.polling.PollingService
 *des:
 *<br>
 *@author xujichang
 *created by 10/28/20 16:01
 */
class PollingService : Service() {
    private val TAG = "PollingService"
    private var scheduledFuture: ScheduledFuture<*>? = null
    private val mExecutor by lazy {
        Executors.newSingleThreadScheduledExecutor { Thread(it, "Thread#Polling") }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (null == scheduledFuture) {
            scheduledFuture = mExecutor.scheduleWithFixedDelay({
                JobPool.update()
            }, 1000, 1000, TimeUnit.MILLISECONDS)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        scheduledFuture?.run {
            if (!isCancelled) {
                cancel(true)
            }
        }
    }
}