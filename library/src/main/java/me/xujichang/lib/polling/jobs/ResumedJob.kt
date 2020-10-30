package me.xujichang.lib.polling.jobs

import androidx.lifecycle.*

/**
 *project：Polling
 *full_path：me.xujichang.lib.polling.jobs.PageJob
 *des:根据页面的生命周期
 *<br>
 *@author xujichang
 *created by 10/30/20 17:04
 */
class ResumedJob(private val lifecycleOwner: LifecycleOwner, private val runFunc: () -> Unit) :
    BaseJob(),
    LifecycleEventObserver {
    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    override fun doJob() {
        runFunc.invoke()
    }

    override fun canUpdate(): Boolean {
        return lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_RESUME) {
            resumeJob()
        } else if (event == Lifecycle.Event.ON_PAUSE) {
            pauseJob()
        }
    }
}