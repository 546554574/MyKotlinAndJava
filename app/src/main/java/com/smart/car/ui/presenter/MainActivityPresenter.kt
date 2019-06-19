package com.smart.car.ui.presenter

import com.google.gson.JsonObject
import com.smart.car.api.ServiceManager
import com.smart.car.base.BasePresenterImpl
import com.smart.car.helper.rxjavahelper.RxObserver
import com.smart.car.helper.rxjavahelper.RxResultHelper
import com.smart.car.helper.rxjavahelper.RxSchedulersHelper
import com.smart.car.ui.model.TestVo
import com.smart.car.ui.view.MainView
import com.smart.util.rxtool.view.RxToast
import io.reactivex.disposables.Disposable
import org.json.JSONObject
import java.lang.Exception

class MainActivityPresenter : BasePresenterImpl<MainView>() {

    fun checkIdentity() {
        ServiceManager.checkIdentity()
                .compose(RxSchedulersHelper.io_main())
                .subscribe(object : RxObserver<String>() {
                    override fun onNextMy(t: String) {
                        try {
                            var jsonObject = JSONObject(t)
                            if (jsonObject.has("code")) {
                                val code = jsonObject.getInt("code")
                                when (code) {
                                    100 -> {
                                        //已注册
                                        view!!.checkIdentity(true)
                                    }
                                    200 -> {
                                        //未注册
                                        view!!.checkIdentity(false)
                                    }
                                    else -> {
                                        //未注册
                                        view!!.checkIdentity(false)
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onErrorMy(errorMessage: String) {
                    }

                })
    }
}
