package com.smart.car.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import com.smart.car.base.MyUtil
import java.util.*

class CheckStateService : Service() {

    inner class MBinder : Binder() {
        fun getService(): CheckStateService {
            return this@CheckStateService
        }
    }

    var mBinder = MBinder()
    override fun onBind(intent: Intent): IBinder {
        sendBeatData()
        return mBinder
    }

    var isPause: Boolean = false
    var handler: Handler = Handler()
    var taskRunnable = object : Runnable {
        override fun run() {
            if (!isPause) {
                MyUtil.sendRequestStateMsg()
            }
            handler.postDelayed(this, 2000)
        }
    }

    /*定时发送数据*/
    private fun sendBeatData() {
        handler.post(taskRunnable)
    }

    fun reStart() {
        isPause = false
    }

    fun pause() {
        isPause = true
    }
}
