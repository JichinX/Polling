package me.xujichang.lib.polling

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build.VERSION
import android.os.Bundle
import android.os.Handler
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import me.xujichang.lib.polling.callbacks.EmptyActivityLifecycleCallbacks

/**
 *project：Polling
 *full_path：me.xujichang.lib.polling.AppLifecycleOwner
 *des:
 *<br>
 *@author xujichang
 *created by 10/30/20 17:52
 */

class AppLifecycleOwner private constructor() : LifecycleOwner {

    private var mStartedCounter = 0
    private var mResumedCounter = 0
    private var mPauseSent = true
    private var mStopSent = true
    private var mHandler: Handler? = null
    private val mRegistry = LifecycleRegistry(this)
    private val mDelayedPauseRunnable = Runnable {
        dispatchPauseIfNeeded()
    }

    fun activityResumed() {
        ++mResumedCounter
        if (mResumedCounter == 1) {
            if (mPauseSent) {
                mRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
                mPauseSent = false
            } else {
                mHandler!!.removeCallbacks(mDelayedPauseRunnable)
            }
        }
    }

    fun activityPaused() {
        --mResumedCounter
        if (mResumedCounter == 0) {
            mHandler!!.postDelayed(mDelayedPauseRunnable, 700L)
        }
    }

    fun dispatchPauseIfNeeded() {
        if (mResumedCounter == 0) {
            mPauseSent = true
            mRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        }
    }

    private fun attach(context: Context) {
        mHandler = Handler()
        mRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        val app = context.applicationContext as Application
        app.registerActivityLifecycleCallbacks(object :
            EmptyActivityLifecycleCallbacks() {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                activityResumed()
            }

            override fun onActivityDestroyed(activity: Activity) {
                activityPaused()
            }
        })
    }

    override fun getLifecycle(): Lifecycle {
        return mRegistry
    }

    companion object {
        private val sInstance: AppLifecycleOwner = AppLifecycleOwner()

        @VisibleForTesting
        val TIMEOUT_MS = 700L
        fun get(): LifecycleOwner {
            return sInstance
        }

        fun init(context: Context) {
            sInstance.attach(context)
        }
    }
}