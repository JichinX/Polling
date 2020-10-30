package me.xujichang.lib.polling.jobs

import android.util.Log

/**
 *project：Polling
 *full_path：me.xujichang.lib.polling.jobs.JobPool
 *des:任务池，所有的任务都在此进行更新
 *<br>
 *@author xujichang
 *created by 10/30/20 16:28
 */
object JobPool {
    private val TAG = "JobPool"
    private val jobs = mutableListOf<BaseJob>()
    private val tagJobs = mutableMapOf<String, BaseJob>()
    fun update() {
        Log.i(TAG, "update: jobs size = ${jobs.size}")
        Log.i(
            TAG, "update: tagJobs size = ${
                tagJobs.size
            }"
        )
        jobs.forEach {
            it.update()
        }
        tagJobs.entries.forEach { entry ->
            entry.value.update()
        }
    }

    fun addJob(job: BaseJob) {
        jobs.add(job)
    }

    fun addTagJob(tagJob: TagJob) {
        tagJobs[tagJob.tag] = tagJob.job
    }
}