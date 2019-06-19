package com.smart.car.service

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.*
import android.support.annotation.RequiresApi
import android.widget.RemoteViews
import com.smart.car.R
import com.smart.car.base.MyApp
import com.smart.car.base.MyUtil
import com.smart.car.receiver.UpdateReceiver
import com.smart.util.rxtool.RxAppTool
import com.smart.util.rxtool.RxFileTool
import com.smart.util.rxtool.RxIntentTool
import com.smart.util.rxtool.RxPermissionsTool
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.DecimalFormat

/**
 * 状态栏通知服务
 * Created by WL-鬼 on 2017/5/13.
 */

class UpdateService : Service() {

    private var appName: String? = null// 应用名字
    private var appUrl: String? = null// 应用升级地址
    //    private File updateDir;// 文件目录
    private var updateFile: File? = null// 升级文件

    private var updateNotificationManager: NotificationManager? = null // 通知栏
    private var updateNotification: Notification? = null
    private var updatePendingIntent: PendingIntent? = null// 在下载的时候

    private var totalSize: Long = 0      //APK总大小
    private var downloadSize: Long = 0  // 下载的大小
    private var count = 0       //下载百分比


    private var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            RxAppTool.installApp(MyApp.instance, updateFile)
        }
    }


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStart(intent: Intent, startId: Int) {
        super.onStart(intent, startId)

        appName = intent.getStringExtra("appname")
        appUrl = intent.getStringExtra("updateurl")


        updateNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        updateNotification = if (Build.VERSION.SDK_INT >= 26) {
            createNotificationChannelMy(updateNotificationManager)
            Notification.Builder(MyApp.instance, NOTIFICATION_CHANNEL).build()
        } else {
            Notification()
        }
        updateNotification!!.icon = R.mipmap.ic_launcher//通知图标
        updateNotification!!.tickerText = "正在下载" + appName!!//通知信息描述
        updateNotification!!.`when` = System.currentTimeMillis()
        updateNotification!!.contentView = RemoteViews(
            packageName,
            R.layout.download_notification
        )
        updateNotification!!.contentView.setTextViewText(
            R.id.download_notice_name_tv, appName!! + " 正在下载"
        )
        UpdateThread().execute()

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun createNotificationChannelMy(notificationManager: NotificationManager?) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL,
            RxAppTool.getAppPackageName(MyApp.instance),
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.setSound(null, null)
        notificationManager!!.createNotificationChannel(channel)
    }

    /**
     * 这里使用一个内部类去继承AsyncTask
     * 实现异步操作
     */
    internal inner class UpdateThread : AsyncTask<String, Int, String>() {

        override fun doInBackground(vararg params: String?): String? {
            return downloadUpdateFile(appUrl)
        }
    }

    /**
     * 下载更新程序文件
     *
     * @param appUrl 下载地址
     * @return
     */
    private fun downloadUpdateFile(appUrl: String?): String? {

        val mOkHttpClient = OkHttpClient()

        val mRequest = Request.Builder().url(appUrl!!).build()

        val mCall = mOkHttpClient.newCall(mRequest)

        mCall.enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                downloadResult(DOWNLOAD_FAIL)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (response.code() == 200) {
                    var `is`: InputStream? = null
                    val buf = ByteArray(4096)
                    var len = 0
                    var fos: FileOutputStream? = null
                    try {
                        totalSize = response.body()!!.contentLength()
                        downloadSize = 0
                        if (memoryAvailable(totalSize)) {
                            `is` = response.body()!!.byteStream()
                            fos = FileOutputStream(updateFile!!, true)
                            while (`is`.read() and `is`!!.read(buf) != -1) {
                                len = `is`!!.read()
                                downloadSize += len.toLong()
                                fos.write(buf, 0, len)
                                if (count == 0 || (downloadSize * 100 / totalSize).toInt() >= count) {
                                    count += 5
                                    //文本进度（百分比）
                                    updateNotification!!.contentView
                                        .setTextViewText(
                                            R.id.download_notice_speed_tv,
                                            getMsgSpeed(downloadSize, totalSize)
                                        )
                                    //进度条
                                    updateNotification!!.contentView.setProgressBar(
                                        R.id.pbProgress,
                                        totalSize.toInt(), downloadSize.toInt(), false
                                    )
                                    updateNotificationManager!!.notify(0, updateNotification)
                                }
                            }
                            fos.flush()
                            if (totalSize >= downloadSize) {
                                //进度条
                                updateNotification!!.contentView.setProgressBar(
                                    R.id.pbProgress,
                                    totalSize.toInt(), totalSize.toInt(), false
                                )
                                downloadResult(DOWNLOAD_COMPLETE)
                            } else {
                                downloadResult(DOWNLOAD_FAIL)
                            }
                        } else {
                            downloadResult(DOWNLOAD_NOMEMORY)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        downloadResult(DOWNLOAD_FAIL)
                    } finally {
                        try {
                            `is`?.close()
                            fos?.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    }
                } else {
                    downloadResult(DOWNLOAD_FAIL)
                }
            }
        })
        return null
    }

    /**
     * 下载结果
     *
     * @param integer
     */
    private fun downloadResult(integer: String) {
        when (integer) {
            DOWNLOAD_COMPLETE -> {
                //权限
                RxPermissionsTool.with(MyApp.instance).addPermission(Manifest.permission.REQUEST_INSTALL_PACKAGES)
                    .initPermission()
                updatePendingIntent = PendingIntent.getActivity(
                    this@UpdateService,
                    0,
                    RxIntentTool.getInstallAppIntent(MyApp.instance, updateFile!!.path),
                    0
                )
                updateNotification!!.contentIntent = updatePendingIntent
                updateNotification!!.tickerText = appName!! + " 下载完成"//通知信息描述
                /**
                 * 这里做为保留，是选择显示之前有进度条的下载完成提示还是选择另外的显示样式，可根据自己定义
                 */
                updateNotification!!.contentView.setTextViewText(
                    R.id.download_notice_name_tv, appName!! + " 下载完成"
                )
                //                updateNotification.contentView.setTextViewText(
                //                        R.id.download_notice_speed_tv,
                //                        getString(R.string.update_notice_install));
                updateNotification!!.contentView = UpdateReceiver.getRemoteViews(MyApp.instance!!, "下载完成，点击安装")
                updateNotification!!.`when` = System.currentTimeMillis()
                updateNotification!!.defaults = Notification.DEFAULT_SOUND
                updateNotification!!.flags = updateNotification!!.flags or Notification.FLAG_AUTO_CANCEL
                updateNotificationManager!!.notify(0, updateNotification)
                //启动安装程序
                //                startActivity(installIntent);
                handler.sendEmptyMessageDelayed(0, 500)
                stopSelf()
            }

            DOWNLOAD_NOMEMORY -> {
                //如果内存有问题
                updateNotification!!.tickerText = appName!! + "下载失败"
                updateNotification!!.`when` = System.currentTimeMillis()
                updateNotification!!.contentView.setTextViewText(
                    R.id.download_notice_speed_tv,
                    getString(R.string.update_notice_nomemory)
                )
                updateNotification!!.flags = updateNotification!!.flags or Notification.FLAG_AUTO_CANCEL
                updateNotification!!.defaults = Notification.FLAG_ONLY_ALERT_ONCE
                updateNotificationManager!!.notify(0, updateNotification)
                stopSelf()
            }

            DOWNLOAD_FAIL -> {
                //下载失败
                updateNotification!!.tickerText = appName!! + "下载失败"
                updateNotification!!.`when` = System.currentTimeMillis()
                updateNotification!!.contentView.setTextViewText(
                    R.id.download_notice_speed_tv,
                    getString(R.string.update_notice_error)
                )
                updateNotification!!.flags = updateNotification!!.flags or Notification.FLAG_AUTO_CANCEL
                updateNotification!!.defaults = Notification.DEFAULT_SOUND
                updateNotificationManager!!.notify(0, updateNotification)
                stopSelf()
            }
        }
    }

    /**
     * 可用内存大小
     *
     * @param fileSize
     * @return
     */
    private fun memoryAvailable(fileSize: Long): Boolean {
        var fileSize = fileSize
        fileSize += (1024 shl 10).toLong()
        return if (RxFileTool.sdCardIsAvailable()) {
            if (RxFileTool.getSDCardAvailaleSize() <= fileSize) {
                false
            } else {
                createFile(true)
                true
            }
        } else {
            if (RxFileTool.getSDCardAvailaleSize() <= fileSize) {
                false
            } else {
                createFile(false)
                true
            }
        }
    }

    /**
     * 创建file文件
     *
     * @param sd_available sdcard是否可用
     */
    private fun createFile(sd_available: Boolean) {
        RxFileTool.createFileByDeleteOldFile(MyUtil.basePath + appName + ".apk")
        updateFile = RxFileTool.getFileByPath(MyUtil.basePath + appName + ".apk")
    }

    companion object {


        private const val SIZE_BT = 1024f // BT字节参考量
        private const val SIZE_KB = SIZE_BT * 1024.0f // KB字节参考量
        private const val SIZE_MB = SIZE_KB * 1024.0f// MB字节参考量

        private const val DOWNLOAD_COMPLETE = "1"// 完成
        private const val DOWNLOAD_NOMEMORY = "-1"// 内存异常
        private const val DOWNLOAD_FAIL = "-2"// 失败

        private const val NOTIFICATION_CHANNEL = "com.zydl.pay"

        /**
         * 获取下载进度
         *
         * @param downSize
         * @param allSize
         * @return
         */
        fun getMsgSpeed(downSize: Long, allSize: Long): String {
            val sBuf = StringBuffer()
            sBuf.append(getSize(downSize))
            sBuf.append("/")
            sBuf.append(getSize(allSize))
            sBuf.append(" ")
            sBuf.append(getPercentSize(downSize, allSize))
            return sBuf.toString()
        }

        /**
         * 获取大小
         *
         * @param size
         * @return
         */
        private fun getSize(size: Long): String {
            if (size >= 0 && size < SIZE_BT) {
                return (Math.round((size * 10).toFloat()) / 10.0).toString() + "B"
            } else if (size >= SIZE_BT && size < SIZE_KB) {
                return (Math.round(size / SIZE_BT * 10) / 10.0).toString() + "KB"
            } else if (size >= SIZE_KB && size < SIZE_MB) {
                return (Math.round(size / SIZE_KB * 10) / 10.0).toString() + "MB"
            }
            return ""
        }

        /**
         * 获取到当前的下载百分比
         *
         * @param downSize 下载大小
         * @param allSize  总共大小
         * @return
         */
        private fun getPercentSize(downSize: Long, allSize: Long): String {
            val percent = if (allSize == 0L)
                "0.0"
            else
                DecimalFormat("0.0")
                    .format(downSize.toDouble() / allSize.toDouble() * 100)
            return "($percent%)"
        }
    }
}
