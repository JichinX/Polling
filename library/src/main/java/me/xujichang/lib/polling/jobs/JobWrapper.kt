package me.xujichang.lib.polling.jobs

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 *project：Polling
 *full_path：me.xujichang.lib.polling.jobs.JobWrapper
 *des:
 *<br>
 *@author xujichang
 *created by 11/2/20 16:15
 */
open class JobWrapper(lifecycleOwner: LifecycleOwner, val job: BaseJob) :
    LifecycleEventObserver {
    init {
        lifecycleOwner.lifecycle.addObserver(getObserver())
    }

    private fun getObserver(): LifecycleObserver = this
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            JobPool.removeJob(this)
        }
    }
}

class IdsJob(lifecycleOwner: LifecycleOwner, job: BaseJob, val id: Long) :
    JobWrapper(lifecycleOwner, job)

class CleanJob(lifecycleOwner: LifecycleOwner, job: BaseJob) :
    JobWrapper(lifecycleOwner, job)


class TagJob(lifecycleOwner: LifecycleOwner, job: BaseJob, val tag: String) :
    JobWrapper(lifecycleOwner, job)
