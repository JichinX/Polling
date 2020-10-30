package me.xujichang.lib.polling

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import me.xujichang.lib.polling.jobs.JobPool
import me.xujichang.lib.polling.jobs.ResumedJob
import me.xujichang.lib.polling.jobs.TagJob

/**
 *project：Polling
 *full_path：me.xujichang.lib.polling.PollingActivity
 *des:
 *<br>
 *@author xujichang
 *created by 10/30/20 17:20
 */
class PollingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_polling)
        JobPool.addTagJob(TagJob("Polling", ResumedJob(this) {
            Log.i("ResumedJob", "Activity ... ")
        }))
    }
}