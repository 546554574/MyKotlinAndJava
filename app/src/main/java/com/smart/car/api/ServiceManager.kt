package com.smart.car.api

import com.google.gson.Gson
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.HttpParams
import com.lzy.okrx2.adapter.ObservableBody
import com.smart.car.base.AppConstant
import com.smart.car.base.MyApp
import com.smart.car.helper.JsonConvert
import com.smart.car.helper.ResponseData
import com.smart.car.ui.model.SendMsgVo
import com.smart.car.ui.model.TestVo
import com.smart.car.ui.model.UpdateAppVo
import com.smart.util.rxtool.*
import io.reactivex.Observable
import kotlin.coroutines.experimental.coroutineContext

object ServiceManager {
    val Token = ""
    val BaseUrl = "http://192.168.1.125:8080"
    val GetConfigUrl = BaseUrl.plus("/config/update.conf")
    var CheckIdentityUrl: String = BaseUrl.plus("/app/verify/")
    val UpdateApp = "$BaseUrl/v1/app/my/check_version" //更新APP
    val RegistUrl = BaseUrl.plus("/app/registered")//注册
    val ControctCarUrl = BaseUrl.plus("/app/controct")//小车调度

    /**
     * 身份校验，是否注册
     */
    fun checkIdentity(): Observable<String> {
        var url = CheckIdentityUrl + RxDeviceTool.getDeviceIdIMEI(MyApp.instance)
        return OkGo.get<String>(url)
                .converter(
                        object : JsonConvert<String>() {
                        })
                .adapt(ObservableBody())
    }

    /**
     * 软件更新
     */
    fun updateApp(): Observable<ResponseData<UpdateAppVo>> {
        return OkGo.get<ResponseData<UpdateAppVo>>(UpdateApp)
                .params("client", "android")
                .converter(JsonConvert())
                .adapt(ObservableBody())
    }

    /**
     * 注册
     */
    fun regist(name: String, phone: String): Observable<String> {
        return OkGo.post<String>(RegistUrl)
                .params("name", name)
                .params("phone", phone)
                .params("key", RxDeviceTool.getDeviceIdIMEI(MyApp.instance))
                .converter(object : JsonConvert<String>() {})
                .adapt(ObservableBody())
    }

    /**
     * 发送小车调度命令
     */
    fun controctCar(sendMsgVo: SendMsgVo): Observable<String> {
        var httpParms: HttpParams = HttpParams()
        httpParms.put("id", AppConstant.ID)
        httpParms.put("command", sendMsgVo.msgCode)
        httpParms.put("state", AppConstant.STATE)
        httpParms.put("end", sendMsgVo.destination)
        return OkGo
                .post<String>(ControctCarUrl)
                .params(httpParms).converter(object : JsonConvert<String>() {}).adapt(ObservableBody())
    }

}
