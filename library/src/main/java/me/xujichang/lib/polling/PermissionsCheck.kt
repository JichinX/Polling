package me.xujichang.lib.polling

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

/**
 *project：Polling
 *full_path：me.xujichang.lib.polling.PermissionsCheck
 *des:
 *<br>
 *@author xujichang
 *created by 10/28/20 15:17
 */
class PermissionsCheck(resultCaller: ActivityResultCaller) {
    private var permissionCallback: ((ActivityResult) -> Unit)? = null
    private val isIgnoreBatteryOptimizationsRequest =
            resultCaller.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                permissionCallback?.invoke(it)
            }

    /**
     * 检查当前应用是否在白名单内
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun isIgnoreBatteryOptimizations(context: Context): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        return powerManager?.isIgnoringBatteryOptimizations(context.packageName)
                ?: false
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun requestIgnoreBatteryOptimizations(context: Context, permissionCallback: ((ActivityResult) -> Unit)) {
        try {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.data = Uri.parse("package:" + context.packageName);
            isIgnoreBatteryOptimizationsRequest.launch(intent)
        } catch (e: Exception) {
            e.printStackTrace();
        }
    }
}