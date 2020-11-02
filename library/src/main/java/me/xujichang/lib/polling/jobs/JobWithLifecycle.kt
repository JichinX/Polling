package me.xujichang.lib.polling.jobs

import android.util.Log
import androidx.lifecycle.*

/**
 *project：Polling
 *full_path：me.xujichang.lib.polling.jobs.PageJob
 *des:根据页面的生命周期
 *<br>
 *@author xujichang
 *created by 10/30/20 17:04
 */
class JobWithLifecycle(
    private val lifecycleOwner: LifecycleOwner,
    private val workState: Lifecycle.State = Lifecycle.State.RESUMED,
    private val updateInterceptor: (() -> Boolean) = { true },
    private val runFunc: () -> Unit,
) :
    BaseJob(),
    LifecycleEventObserver {
    private val TAG = "ResumedJob"

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    override fun doJob() {
        Log.i(TAG, "doJob: $lifecycleOwner $workState ${lifecycleOwner.lifecycle.currentState}")
        runFunc.invoke()
    }

    override fun canUpdate(): Boolean {
        Log.i(TAG, "canUpdate: $workState ${lifecycleOwner.lifecycle.currentState}")
        return lifecycleOwner.lifecycle.currentState.isAtLeast(workState) && updateInterceptor.invoke()
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (canUpdate()) {
            resumeJob()
        } else {
            pauseJob()
        }
    }
}