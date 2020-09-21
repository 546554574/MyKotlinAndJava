package com.zydl.tong.base

import android.app.Application
import android.content.Context
import android.os.Looper
import android.util.Log
import androidx.multidex.BuildConfig
import androidx.multidex.MultiDex
import com.qmuiteam.qmui.arch.QMUISwipeBackActivityManager
import com.scwang.smartrefresh.header.MaterialHeader
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.tencent.bugly.Bugly
import com.tencent.bugly.beta.Beta
import com.tencent.bugly.beta.upgrade.UpgradeListener
import com.tencent.bugly.beta.upgrade.UpgradeStateListener
import com.tencent.bugly.crashreport.CrashReport
import com.vondear.rxtool.RxTool
import com.zydl.tong.api.ServiceManager
import com.zydl.tong.dealException.Cockroach
import com.zydl.tong.dealException.Cockroach.*
import com.zydl.tong.dealException.ExceptionHandler


class MyApp : Application() {

    override fun onCreate() {
        instance = this
        //自定义初始化
        RxTool.init(this)
        //建行SDK
//        CCBWXPayAPI.getInstance().init(this, AppConstant.APP_ID);
        Bugly.init(applicationContext, AppConstant.BUGLY_KEY, false)
        Beta.autoDownloadOnWifi = true
        Beta.upgradeListener = UpgradeListener { ret, strategy, isManual, isSilence -> }
        Beta.upgradeStateListener = object : UpgradeStateListener {
            override fun onUpgradeFailed(b: Boolean) {}
            override fun onUpgradeSuccess(b: Boolean) {}
            override fun onUpgradeNoVersion(b: Boolean) {}
            override fun onUpgrading(b: Boolean) {}
            override fun onDownloadCompleted(b: Boolean) {}
        }
        if (!BuildConfig.DEBUG) {
            install(instance, object : ExceptionHandler() {
                override protected fun onUncaughtExceptionHappened(thread: Thread, throwable: Throwable?) {
                    Log.e("AndroidRuntime", "--->onUncaughtExceptionHappened:$thread<---", throwable)
                }

                override protected fun onBandageExceptionHappened(throwable: Throwable) {
                    throwable.printStackTrace() //打印警告级别log，该throwable可能是最开始的bug导致的，无需关心
                }

                override protected fun onEnterSafeMode() {}
                override protected fun onMayBeBlackScreen(e: Throwable?) {
                    val thread = Looper.getMainLooper().thread
                    Log.e("AndroidRuntime", "--->onUncaughtExceptionHappened:$thread<---", e)
                }
            })
        }

        CrashReport.initCrashReport(applicationContext, "2b7a11c535", false)
        //百度地图

        QMUISwipeBackActivityManager.init(this)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    companion object {

        @get:Synchronized
        var instance: MyApp? = null

        init {
            //设置全局的Header构建器
            SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
                //                layout.setPrimaryColorsId(R.color.page_color, R.color.white);//全局设置主题颜色
                MaterialHeader(context)//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
            //设置全局的Footer构建器
            SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
                //指定为经典Footer，默认是 BallPulseFooter
                ClassicsFooter(context).setDrawableSize(20f)
            }
        }
    }
}
