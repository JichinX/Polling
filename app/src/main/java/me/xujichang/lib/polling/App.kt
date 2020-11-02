package me.xujichang.lib.polling

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.*
import me.xujichang.lib.polling.jobs.CleanJob
import me.xujichang.lib.polling.jobs.JobPool
import me.xujichang.lib.polling.jobs.JobWithLifecycle
import me.xujichang.lib.polling.jobs.TagJob
import me.xujichang.lib.provider.AppLifecycleOwner

/**
 *project：Polling
 *full_path：me.xujichang.lib.polling.App
 *des:
 *<br>
 *@author xujichang
 *created by 10/30/20 17:10
 */
class App : Application() {
    private val TAG = "app"

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate: ")
        JobPool.add(
            TagJob(
                ProcessLifecycleOwner.get(),
                JobWithLifecycle(
                    ProcessLifecycleOwner.get(),
                    interval = 3,
                    workState = Lifecycle.State.RESUMED
                ) {
                    //...
                },
                "App-1",
            )
        )
        JobPool.add(
            CleanJob(
                AppLifecycleOwner.get(),
                JobWithLifecycle(
                    AppLifecycleOwner.get(),
                    workState = Lifecycle.State.CREATED,
                    updateInterceptor = {
                        !AppLifecycleOwner.isDestroyed()
                    }) {
                    //...
                }
            )
        )
        startService(
            Intent(
                this, PollingService::class.java
            )
        )
    }
}