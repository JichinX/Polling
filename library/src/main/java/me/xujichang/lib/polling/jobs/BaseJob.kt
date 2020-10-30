package me.xujichang.lib.polling.jobs

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

/**
 * 默认任务间隔1s
 */
const val DEFAULT_INTERVAL = 1

/**
 *project：Polling
 *full_path：me.xujichang.lib.polling.jobs.BaseJob
 *des:任务基类，定义任务的基本属性
 *<br>
 *@author xujichang
 *created by 10/30/20 16:22
 */
abstract class BaseJob(interval: Int = DEFAULT_INTERVAL) {
    private val maxTimes = interval
    private val counter = AtomicInteger(0)
    private val jobStatus = AtomicReference<JobStatus>(JobStatus.INITIAL)
    fun update() {
        if (jobStatus.get() != JobStatus.INITIAL) {
            return
        }
        jobStatus.set(JobStatus.RUNNING)
        //周期计数器+1 && 符合任务执行条件
        if (counter.incrementAndGet() >= maxTimes && canUpdate()) {
            doJob()
        }
        jobStatus.set(JobStatus.INITIAL)
    }

    //暂停任务
    fun pauseJob() {
        jobStatus.set(JobStatus.PAUSE)
    }

    //恢复任务
    fun resumeJob() {
        jobStatus.set(JobStatus.INITIAL)
    }

    /**
     * 执行任务
     */
    abstract fun doJob()

    /**
     * @return 是否符合任务执行的条件
     */
    abstract fun canUpdate(): Boolean
}