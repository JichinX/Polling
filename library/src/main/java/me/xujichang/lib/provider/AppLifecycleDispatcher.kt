package me.xujichang.lib.provider

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ReportFragment.injectIfNeededIn
import java.util.concurrent.atomic.AtomicBoolean

/**
 *project：Polling
 *full_path：me.xujichang.lib.provider.LifecycleDispatcher
 *des:
 *<br>
 *@author xujichang
 *created by 11/2/20 14:19
 */
object AppLifecycleDispatcher {
    private val sInitialized = AtomicBoolean(false)

    fun init(context: Context) {
        if (sInitialized.getAndSet(true)) {
            return
        }
        (context.applicationContext as Application)
            .registerActivityLifecycleCallbacks(DispatcherActivityCallback())
    }

    @VisibleForTesting
    internal class DispatcherActivityCallback : EmptyActivityLifecycleCallbacks() {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            ReportFragment.injectIfNeededIn(activity)
        }

        override fun onActivityStopped(activity: Activity) {}
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    }
}