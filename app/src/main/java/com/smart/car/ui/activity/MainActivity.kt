package com.smart.car.ui.activity

import android.Manifest
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.blankj.rxbus.RxBus
import com.smart.car.R
import com.smart.car.api.DownLoadFileUtils
import com.smart.car.api.DownLoadFileUtils.DownListener
import com.smart.car.api.ServiceManager
import com.smart.car.base.AppConstant
import com.smart.car.base.BaseActivity
import com.smart.car.base.BaseFragment
import com.smart.car.base.MyUtil
import com.smart.car.busmsg.GoToMissionFragmentMsg
import com.smart.car.busmsg.SendMsg
import com.smart.car.busmsg.UpdateState
import com.smart.car.helper.rxjavahelper.RxObserver
import com.smart.car.helper.rxjavahelper.RxSchedulersHelper
import com.smart.car.service.CheckStateService
import com.smart.car.ui.fragment.CallActivity
import com.smart.car.ui.fragment.MissionActivity
import com.smart.car.ui.fragment.MissionOrClearActivity
import com.smart.car.ui.fragment.WaitActivity
import com.smart.car.ui.model.GroupAndPlaceVo
import com.smart.car.ui.presenter.MainActivityPresenter
import com.smart.car.ui.view.MainView
import com.smart.car.utils.BDLocationUtils
import com.smart.util.rxtool.*
import com.smart.util.rxtool.view.RxToast
import com.smart.util.rxview.dialog.RxDialogSure
import kotlinx.android.synthetic.main.activity_basic.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.util.*
import java.util.concurrent.ThreadPoolExecutor


class MainActivity : BaseActivity<MainView, MainActivityPresenter>(), MainView {


    override fun initPresenter(): MainActivityPresenter {
        return MainActivityPresenter()
    }

    override fun loadMore() {

    }

    override fun refreData() {
//        mPresenter!!.getVideoList()
    }

    var isLocation = false
    var isDebug = true
    override fun init(savedInstanceState: Bundle?) {
        left_ll.visibility = View.GONE
        if (isDebug) {
            initNext()
        } else {
            val bdLocationUtils = BDLocationUtils(context)
            bdLocationUtils.doLocation()
            bdLocationUtils.setOnLocationClick {
                if (!isLocation) {
                    isLocation = true
                    val distance = MyUtil.getDistance(it.latitude, it.longitude, AppConstant.FAC_LAT, AppConstant.FAC_LON)
                    if (distance!! > AppConstant.MAX_DISTANCE) {
                        //出了产区范围，提示不能打开软件
                        var rxDialogSure: RxDialogSure = RxDialogSure(this)
                        rxDialogSure.setTitle("提示")
                        rxDialogSure.setContent("请进入厂区范围再使用本软件")
                        rxDialogSure.setSureListener {
                            RxActivityTool.AppExit(this)
                        }
                        rxDialogSure.show()
                    } else {
                        initNext()
                    }
                }
            }
        }
    }

    private fun initNext() {
        mPresenter.checkIdentity()
        //下载配置文件
        DownLoadFileUtils.setDownListener(object : DownListener {
            override fun downSuccess(filePath: String) {
                try {
                    AppConstant.groupandplaceVo = readConfig(filePath)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun downError(errMsg: String) {
            }

        })
        DownLoadFileUtils.downloadFile(context, ServiceManager.GetConfigUrl, MyUtil.basePath, "update.conf")
        //启动TCP服务
        //        startService(Intent(this, ControllerService::class.java))
        //校验登录
        //接收到的数据
        /**
         * 0x00 空闲
         * 0x01 呼叫空车
         * 0x02 重车保持
         * 0x03 派遣重车
         * 0x04 任务被抛弃
         * 0x05 等待空车调离
         * 0x06 被故障车占用
         */
        RxBus.getDefault().subscribe(this, object : RxBus.Callback<UpdateState>() {
            override fun onEvent(updateState: UpdateState) {
                AppConstant.STATE = updateState.state
                when (updateState.state) {
                    AppConstant.STATE_0 ->
                        //显示呼叫页
                        if (currentFragment != CallActivity.newInstance()) {
                            changeFragment(CallActivity.newInstance())
                            if (checkStateService != null) {
                                checkStateService!!.pause()
                            }
                            title_tv.text = "呼叫"
                            Log.e("TAG", "呼叫0...")
                        }
                    AppConstant.STATE_1 ->
                        //显示等待页
                        if (currentFragment != WaitActivity.newInstance()) {
                            changeFragment(WaitActivity.newInstance())
                            if (checkStateService != null) {
                                checkStateService!!.reStart()
                            }
                            title_tv.text = "等待"
                            Log.e("TAG", "等待1...")
                        }
                    AppConstant.STATE_2 ->
                        //显示派遣页
                        if (currentFragment != MissionOrClearActivity.newInstance()) {
                            changeFragment(MissionOrClearActivity.newInstance())
                            if (checkStateService != null) {
                                checkStateService!!.pause()
                            }
                            title_tv.text = "派遣"
                            Log.e("TAG", "派遣2...")
                        }
                    AppConstant.STATE_3 ->
                        //显示等待页
                        if (currentFragment != WaitActivity.newInstance()) {
                            changeFragment(WaitActivity.newInstance())
                            if (checkStateService != null) {
                                checkStateService!!.reStart()
                            }
                            title_tv.text = "等待"
                            Log.e("TAG", "等待3...")
                        }
                    AppConstant.STATE_4 -> {
                        //显示等待页
//                        if (currentFragment != WaitActivity.newInstance()) {
//                            changeFragment(WaitActivity.newInstance())
//                            title_tv.text = "等待"
//                }
                        handler.sendEmptyMessage(AppConstant.STATE_4)
                        Log.e("TAG", "等待4...")
                    }
                    AppConstant.STATE_5 -> {
                        //显示等待页
                        if (currentFragment != WaitActivity.newInstance()) {
                            changeFragment(WaitActivity.newInstance())
                            if (checkStateService != null) {
                                checkStateService!!.reStart()
                            }
                            title_tv.text = "等待"
                            Log.e("TAG", "等待5...")
                        }
                    }
                    AppConstant.STATE_6 ->
                        //显示等待页
                        if (currentFragment != WaitActivity.newInstance()) {
                            changeFragment(WaitActivity.newInstance())
                            if (checkStateService != null) {
                                checkStateService!!.reStart()
                            }
                            title_tv.text = "等待"
                            Log.e("TAG", "等待6...")
                        }
                    AppConstant.STATE_FF -> {
                        //错误
                        handler.sendEmptyMessage(AppConstant.STATE_FF)
                        Log.e("TAG", "错误...")
                    }
                }
//                //发送状态请求
//                try {
//                    Thread.sleep(1000)
//                    MyUtil.sendRequestStateMsg()
//                } catch (e: InterruptedException) {
//                    e.printStackTrace()
//                }
            }
        })
        RxBus.getDefault().subscribe(this,
                object : RxBus.Callback<SendMsg>() {
                    override fun onEvent(sendMsg: SendMsg?) {
                        if (sendMsg?.sendMsgVo != null) {
                            ServiceManager.controctCar(sendMsg.sendMsgVo!!)
                                    .subscribe(object : RxObserver<String>() {
                                        override fun onErrorMy(errorMessage: String) {
                                            RxToast.error(errorMessage)
                                        }

                                        override fun onNextMy(s: String) {
                                            try {
                                                val jsonObject = JSONObject(s)
                                                if (jsonObject.has("state")) {
                                                    val state = jsonObject.getInt("state")
                                                    if (sendMsg.sendMsgVo!!.isCheckState) {
                                                        RxBus.getDefault().post(UpdateState(state))
                                                    } else {
                                                        AppConstant.STATE = state
                                                        //收到命令，进行状态监测
                                                        MyUtil.sendRequestStateMsg()
                                                    }
                                                }
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }

                                        }
                                    })
                        }
                    }
                })

        RxBus.getDefault().subscribe(this,
                object : RxBus.Callback<GoToMissionFragmentMsg>() {
                    override fun onEvent(t: GoToMissionFragmentMsg?) {
                        checkStateService!!.pause()
                        changeFragment(MissionActivity.newInstance())
                    }
                })

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    //读取配置文件
    private fun readConfig(filePath: String): GroupAndPlaceVo {
        var workStationVo: GroupAndPlaceVo = GroupAndPlaceVo()
        val readFile2String = RxFileTool.readFile2String(filePath, "UTF-8")
        workStationVo = GsonBinder.toObj<GroupAndPlaceVo>(readFile2String, GroupAndPlaceVo::class.java)
        return workStationVo
    }

    override fun onBackIv() {
        RxActivityTool.skipActivity(context, ScanCodeAcitvity::class.java)
    }

    override fun getLayout(): Int {
        return R.layout.activity_main
    }

    override fun getTitleStr(): String {
        return resources.getString(R.string.app_name)
    }

    var currentFragment: Fragment? = null
    var fragmentMananger: FragmentManager = supportFragmentManager
    var checkStateService: CheckStateService? = null
    override fun initEventAndData() {

    }

    var handler: Handler = Handler {
        when (it.what) {
            AppConstant.STATE_4 -> {
                //收到4再发一次状态监测
                RxToast.info(context,"暂无可用小车")
            }
            AppConstant.STATE_FF -> {
                RxToast.error(context,"错误")
            }
        }
        MyUtil.sendRequestStateMsg()
        return@Handler true
    }

    override fun onDestroy() {
        super.onDestroy()
        RxBus.getDefault().unregister(this)
    }

    fun changeFragment(fragment: Fragment) {
        if (currentFragment == fragment) {
            return
        }
        val beginTransaction = fragmentMananger.beginTransaction()
        if (fragment.isAdded) {
            beginTransaction.show(fragment)
            if (currentFragment != null) {
                beginTransaction.hide(currentFragment!!)
            }
        } else {
            beginTransaction.add(R.id.fl, fragment)
            if (currentFragment != null) {
                beginTransaction.hide(currentFragment!!)
            }
        }
        beginTransaction.commit()
        currentFragment = fragment
    }

    override fun onResume() {
        super.onResume()
        if (checkStateService != null) {
            checkStateService!!.reStart()
        }
    }

    override fun onRestart() {
        super.onRestart()
        if (checkStateService != null) {
            checkStateService!!.reStart()
        }
    }

    override fun onPause() {
        super.onPause()
        if (checkStateService != null) {
            checkStateService!!.pause()
        }
    }

    /**
     * 是否已经注册
     */
    override fun checkIdentity(b: Boolean) {
        if (b) {
            RxActivityTool.skipActivity(this, ScanCodeAcitvity::class.java)
        } else {
            RxActivityTool.skipActivity(this, RegistActivity::class.java)
        }
        left_ll.visibility = View.VISIBLE
        bindService(Intent(this, CheckStateService::class.java), object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {
            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                checkStateService = (service as CheckStateService.MBinder).getService()
            }

        }, Service.BIND_AUTO_CREATE)
    }

}
