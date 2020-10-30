package me.xujichang.lib.polling.callbacks

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle

/**
 *project：Polling
 *full_path：me.xujichang.lib.polling.callbacks.Empty
 *des:
 *<br>
 *@author xujichang
 *created by 10/30/20 17:45
 */

open class EmptyActivityLifecycleCallbacks :
    ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}