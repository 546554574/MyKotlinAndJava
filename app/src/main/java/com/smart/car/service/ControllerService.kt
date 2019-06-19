package com.smart.car.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.blankj.rxbus.RxBus
import com.smart.car.base.MyUtil
import com.smart.car.busmsg.UpdateState
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception
import java.net.Socket
import com.smart.car.busmsg.SendMsg
import java.io.IOException
import java.util.*


/**
 * 小车调度数据连接服务
 */
class ControllerService : Service() {

    /**
     * 0x00 空闲
    0x01 呼叫空车
    0x02 重车保持
    0x03 派遣重车
    0x04 任务被抛弃
    0x05 等待空车调离
    0x06 被故障车占用
    0xff 请求错误无法执行
     */
    var state: Int = 0x00

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    var address = "192.168.1.117"
    //    var address = "192.168.1.141"
    var port = 7878
    var socket: Socket? = null
    var inStream: InputStream? = null
    var outStream: OutputStream? = null

    override fun onCreate() {
        super.onCreate()
        Thread(sendRunnable).start()
        Thread(receiverRunnable).start()
        RxBus.getDefault().subscribe(this, object : RxBus.Callback<SendMsg>() {
            override fun onEvent(t: SendMsg?) {
                outputBuffer = t!!.sendMsgVo!!.getSendBuffer()
            }
        })
        //发送心跳检测包
//        sendBeatData()
    }

    override fun onDestroy() {
        super.onDestroy()
        RxBus.getDefault().unregister(this)
    }

    var charBuff: ByteArray? = null
    var outputBuffer: ByteArray? = null
    /**
     * 发送数据线程
     * */
    var sendRunnable: Runnable = Runnable {
        while (true) {
            tryConnect()
            try {
                if (socket != null) {
                    if (outputBuffer != null && outputBuffer!!.isNotEmpty()) {
                        outStream!!.write(outputBuffer)
                        outStream!!.flush()
                        Thread.sleep(123)
                        outputBuffer = null
                    }
                } else {
                    continue
                }
            } catch (e: Exception) {
                /*发送失败说明socket断开了或者出现了其他错误*/
                socket = null
                tryConnect()
                e.printStackTrace()
            }
        }
    }

    /***
     * 接受数据线程
     */
    var receiverRunnable: Runnable = Runnable {
        while (true) {
            tryConnect()
            try {
                if (socket != null) {
                    charBuff = ByteArray(5)
                    var readLine = inStream!!.read(charBuff)
                    if (charBuff!!.isNotEmpty()) {
                        //拿到数据，进行解析
                        readBuffer()
                    }
                } else {
                    continue
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    var beginByteArray: ByteArray = byteArrayOf(0x01, 0xda.toByte())
    /**
     * 解析接受到的数据
     */
    private fun readBuffer() {
        if (charBuff!!.isNotEmpty()) {
            var index = 0
            //匹配开头
            for ((pos, char) in beginByteArray.withIndex()) {
                if (charBuff!![pos] != char) {
                    return
                }
            }
            index += 2
            state = charBuff!![index].toInt()
            charBuff = null
            RxBus.getDefault().post(UpdateState(state))
            closeSocket()
        }
    }

    var isConnect: Boolean = false
    private fun tryConnect() {
        try {
            isConnect = false
            if (socket != null) {
                if (!socket!!.isClosed) {
                    if (socket!!.isConnected) {
                        if (!socket!!.isOutputShutdown && !socket!!.isInputShutdown) {
                            isConnect = true
                        }
                    }
                }
            }
            if (isConnect) {
                return
            }

            if (!isConnect) {
                var s = socket
                socket = null
                Thread.sleep(1000)
                if (s != null) {
                    s.close()
                }
            }

            if (socket == null) {
                socket = Socket(address, port)
                inStream = socket!!.getInputStream()
                outStream = socket!!.getOutputStream()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var timer: Timer = Timer()
    var task: TimerTask? = null
    /*定时发送数据*/
    private fun sendBeatData() {
        if (timer == null) {
            timer = Timer()
        }
        if (task == null) {
            task = object : TimerTask() {
                override fun run() {
//                    MyUtil.sendRequestStateMsg()
                }
            }
        }
        timer.schedule(task, 500, 2000)
    }

    /**
     * 关闭socket
     */
    private fun closeSocket() {
        if (socket != null) {
            if (!socket!!.isClosed && socket!!.isConnected) {
                try {
                    socket!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            socket = null
        }
    }
}
