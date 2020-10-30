package me.xujichang.lib.polling

import android.app.Application
import android.util.Log
import androidx.lifecycle.ProcessLifecycleOwner
import me.xujichang.lib.polling.jobs.JobPool
import me.xujichang.lib.polling.jobs.ResumedJob
import me.xujichang.lib.polling.jobs.TagJob

/**
 *project：Polling
 *full_path：me.xujichang.lib.polling.App
 *des:
 *<br>
 *@author xujichang
 *created by 10/30/20 17:10
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppLifecycleOwner.init(this)
        JobPool.addTagJob(TagJob("App-1", (ResumedJob(ProcessLifecycleOwner.get()) {
            Log.i("ResumedJob", "App-1 :ProcessLifecycleOwner ...")
        })))
        JobPool.addTagJob(TagJob("App-2", (ResumedJob(AppLifecycleOwner.get()) {
            Log.i("ResumedJob", "App-2 :ProcessLifecycleOwner ...")
        })))
    }
}