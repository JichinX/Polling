package me.xujichang.lib.polling.jobs

import android.util.Log
import androidx.collection.LongSparseArray
import androidx.collection.forEach
import androidx.collection.set

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
    private val idsJobs = LongSparseArray<BaseJob>()
    fun update() {
        Log.i(TAG, "update: jobs size = ${jobs.size}")
        jobs.forEach {
            it.update()
        }
        Log.i(
            TAG, "update: tagJobs size = ${
                tagJobs.size
            }"
        )
        tagJobs.entries.forEach { entry ->
            entry.value.update()
        }
        Log.i(
            TAG, "update: idsJobs size = ${
                idsJobs.size()
            }"
        )
        idsJobs.forEach { _, job ->
            job.update()
        }
    }

    fun add(job: JobWrapper) {
        when (job) {
            is CleanJob -> {
                jobs.add(job.job)
            }
            is TagJob -> {
                tagJobs[job.tag] = job.job
            }
            is IdsJob -> {
                idsJobs[job.id] = job.job
            }
            else -> {
                //...
            }
        }
    }

    fun removeJob(job: JobWrapper) {
        when (job) {
            is CleanJob -> {
                jobs.remove(job.job)
            }
            is TagJob -> {
                tagJobs.remove(job.tag)
            }
            is IdsJob -> {
                idsJobs.remove(job.id)
            }
            else -> {
                //...
            }
        }
    }
}