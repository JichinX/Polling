package me.xujichang.lib.provider

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import java.util.concurrent.atomic.AtomicBoolean

/**
 *project：Polling
 *full_path：me.xujichang.lib.provider.AppLifecycleOwner
 *des:整个Activity的生命周期
 *<br>
 *@author xujichang
 *created by 10/30/20 17:52
 */

class AppLifecycleOwner private constructor() : LifecycleOwner {
    private val TAG = "AppLifecycleOwner"
    private val isDestroyed: AtomicBoolean = AtomicBoolean(false)
    private var destroyedFunc: (() -> Unit)? = null

    /**
     * 创建的Activity计数
     */
    private var mCreatedCounter = 0

    /**
     * Activity started的技术
     */
    private var mStartedCounter = 0

    /**
     *
     */
    private var mResumedCounter = 0

    /**
     *
     */
    private var mDestroyedCounter = 0

    private var mPauseSent = true
    private var mStopSent = true
    private var mDestroySent = true
    private var mHandler: Handler? = null
    private val mRegistry = LifecycleRegistry(this)
    private val mDelayedPauseRunnable = Runnable {
        dispatchPauseIfNeeded()
        dispatchStopIfNeeded()
    }

    private fun activityStarted() {
        ++mStartedCounter
        if (mStartedCounter == 1 && mStopSent) {
            mRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        }
    }

    private fun activityCreated() {
        ++mCreatedCounter
        isDestroyed.set(mCreatedCounter == 0)
        if (mCreatedCounter == 1 && mDestroySent) {
            mRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
            mDestroySent = false
        }
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
            mHandler!!.postDelayed(mDelayedPauseRunnable, TIMEOUT_MS)
        }
    }

    private fun activityStopped() {
        --mStartedCounter
        dispatchStopIfNeeded()
    }

    private fun activityDestroyed() {
        --mCreatedCounter
        dispatchDestroyIfNeeded()
    }

    private fun dispatchDestroyIfNeeded() {
        Log.i(TAG, "dispatchDestroyIfNeeded: ")
        isDestroyed.set(mCreatedCounter == 0)
        if (isDestroyed.get()) {
            destroyedFunc?.invoke()
        }
    }

    private fun dispatchStopIfNeeded() {
        Log.i(TAG, "dispatchStopIfNeeded: $mStartedCounter")
        if (mStartedCounter == 0 && mPauseSent) {
            mRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
            mStopSent = true
        }
    }


    private fun dispatchPauseIfNeeded() {
        Log.i(TAG, "dispatchPauseIfNeeded: $mResumedCounter")
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
            override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    activity.registerActivityLifecycleCallbacks(object :
                        EmptyActivityLifecycleCallbacks() {
                        override fun onActivityPostCreated(
                            activity: Activity,
                            savedInstanceState: Bundle?
                        ) {
                            activityCreated()
                        }

                        override fun onActivityPostStarted(activity: Activity) {
                            activityStarted()
                        }

                        override fun onActivityPostResumed(activity: Activity) {
                            activityResumed()
                        }
                    })
                }
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
                    ReportFragment.get(activity)
                        .setProcessListener(object : ReportFragment.ActivityInitializationListener {
                            override fun onCreate() {
                                activityCreated()
                            }

                            override fun onStart() {
                                activityStarted()
                            }

                            override fun onResume() {
                                activityResumed()
                            }
                        })
                }
            }

            override fun onActivityPaused(activity: Activity) {
                activityPaused()
            }

            override fun onActivityStopped(activity: Activity) {
                activityStopped()
            }

            override fun onActivityDestroyed(activity: Activity) {
                activityDestroyed()
            }
        })
    }

    override fun getLifecycle(): Lifecycle {
        return mRegistry
    }

    companion object {
        private val sInstance: AppLifecycleOwner by lazy {
            AppLifecycleOwner()
        }

        @VisibleForTesting
        val TIMEOUT_MS = 1000L
        fun get(): LifecycleOwner {
            return sInstance
        }

        fun init(context: Context) {
            sInstance.attach(context)
        }

        fun addAppDestroyedCallback(func: () -> Unit) {
            sInstance.patchDestroyedCallback(func)
        }

        fun isDestroyed(): Boolean =
            sInstance.isDestroyed.get()
    }

    private fun patchDestroyedCallback(func: () -> Unit) {
        destroyedFunc = func
    }
}