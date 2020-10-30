package me.xujichang.lib.polling

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private val permissionsCheck by lazy {
        PermissionsCheck(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //检查白名单权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!permissionsCheck.isIgnoreBatteryOptimizations(this)) {
                permissionsCheck.requestIgnoreBatteryOptimizations(this) {
                    //再次检测
                    if (permissionsCheck.isIgnoreBatteryOptimizations(this)) {
                        startPoll()
                    } else {
                        toast("当前应用电池优化未禁止")
                    }
                }
            } else {
                startPoll()
            }
        } else {
            startPoll()
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun startPoll() {
        Log.i(TAG, "startPoll: ")
        startService(
            Intent(
                this, PollingService::class.java
            )
        )
    }
}