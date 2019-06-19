package com.smart.car.api

import android.content.Context
import android.os.Environment
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.FileCallback
import com.lzy.okgo.model.Progress
import com.lzy.okgo.request.base.Request
import java.io.File

/**
 * Description: 使用okgo下载文件 zip video music txt image
 * Created by wcystart on 2018/7/4.
 */
object DownLoadFileUtils {
    private var mBasePath: String? = null //本地文件存储的完整路径  /storage/emulated/0/book/恰似寒光遇骄阳.txt

    //拼接一个本地的完整的url 供下载文件时传入一个本地的路径
    private val mSDPath = Environment.getExternalStorageDirectory().path
    //分类别路径
    private var mClassifyPath: String? = null

    /**
     *
     * @param context 上下文
     * @param fileUrl 下载完整url
     * @param destFileDir  SD路径
     * @param destFileName  文件名
     */
    fun downloadFile(
            context: Context,
            fileUrl: String,
            destFileDir: String,
            destFileName: String
    ) {
        val mDestFileName = destFileName
        OkGo.get<File>(fileUrl).tag(context)
                .execute(object : FileCallback(destFileDir, mDestFileName) { //文件下载时指定下载的路径以及下载的文件的名称
                    override fun onStart(request: Request<File, out Request<*, *>>?) {
                        super.onStart(request)
//                        RxToast.showToast("开始下载")
                    }

                    override fun onSuccess(response: com.lzy.okgo.model.Response<File>) {
                        mBasePath = response.body().absolutePath
//                        RxToast.showToast("下载成功")
//                        RxAppTool.InstallAPK(context, mBasePath)
                        if (downListener != null) {
                            downListener!!.downSuccess(mBasePath!!)
                        }
                    }

                    override fun onFinish() {
                        super.onFinish()
                    }

                    override fun onError(response: com.lzy.okgo.model.Response<File>) {
                        super.onError(response)
                        if (downListener != null && response.message() != null) {
                            downListener!!.downError(response.message())
                        }
                    }

                    override fun downloadProgress(progress: Progress?) {
                        super.downloadProgress(progress)
                        val dLProgress = progress!!.fraction
//                        RxToast.showToast("进度：".plus(dLProgress))
                    }
                })
    }

    fun customLocalStoragePath(differentName: String): String {
        val basePath = File(mSDPath) // /storage/emulated/0
        mClassifyPath = "$mSDPath/$differentName/"  //如果传来的是 book 拼接就是 /storage/emulated/0/book/
        //如果传来的是game  那拼接就是 /storage/emulated/0/game/
        if (!basePath.exists()) {
            basePath.mkdirs()
            println("文件夹创建成功")
        }
        return mClassifyPath!!
    }


    //截取一个文件加载显示时传入的一个本地完整路径
    fun subFileFullName(fileName: String, fileUrl: String): String {
        return fileName + fileUrl.substring(fileUrl.lastIndexOf("."), fileUrl.length)
    }

    private var downListener: DownListener? = null
    fun setDownListener(listener: DownListener) {
        this.downListener = listener
    }

    interface DownListener {
        fun downSuccess(filePath: String)
        fun downError(errMsg: String)
    }
}
