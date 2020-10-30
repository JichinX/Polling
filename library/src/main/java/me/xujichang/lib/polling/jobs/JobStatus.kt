package me.xujichang.lib.polling.jobs

/**
 *project：Polling
 *full_path：me.xujichang.lib.polling.jobs.JobStatus
 *des:任务状态
 *<br>
 *@author xujichang
 *created by 10/30/20 16:50
 */
sealed class JobStatus {
    object PAUSE : JobStatus()
    object STOP : JobStatus()
    object RUNNING : JobStatus()
    object FINISHED : JobStatus()
    object INITIAL : JobStatus()
}
