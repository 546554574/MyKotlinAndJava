package com.zydl.tong.ui.activity

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.View
import com.blankj.rxbus.RxBus
import com.zydl.tong.R
import com.zydl.tong.api.ServiceManager
import com.zydl.tong.base.AppConstant
import com.zydl.tong.base.BaseActivity
import com.zydl.tong.base.MyUtil
import com.zydl.tong.busmsg.GoToMissionFragmentMsg
import com.zydl.tong.busmsg.SendMsg
import com.zydl.tong.busmsg.UpdateState
import com.zydl.tong.ui.model.GroupAndPlaceVo
import com.zydl.tong.ui.presenter.MainActivityPresenter
import com.zydl.tong.ui.view.MainView
import com.zydl.tong.utils.BDLocationUtils
import org.json.JSONObject


class MainActivity : BaseActivity<MainView, MainActivityPresenter>(), MainView {


    override fun initPresenter(): MainActivityPresenter {
        return MainActivityPresenter()
    }

    override fun loadMore() {

    }

    override fun refreData() {
//        mPresenter!!.getVideoList()
    }

    override fun onBackIv() {
    }

    override fun getLayout(): Int {
        return R.layout.activity_main
    }

    override fun getTitleStr(): String {
        return resources.getString(R.string.app_name)
    }
    override fun initEventAndData() {

    }

    override fun onDestroy() {
        super.onDestroy()
        RxBus.getDefault().unregister(this)
    }

    override fun init(savedInstanceState: Bundle?) {
    }


}
