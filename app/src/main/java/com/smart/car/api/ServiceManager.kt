package com.smart.car.api


object ServiceManager {
    val Token = ""
    val BaseUrl = "http://192.168.1.125:8080"
    val GetConfigUrl = BaseUrl.plus("/config/update.conf")
    var CheckIdentityUrl: String = BaseUrl.plus("/app/verify/")
    val UpdateApp = "$BaseUrl/v1/app/my/check_version" //更新APP
    val RegistUrl = BaseUrl.plus("/app/registered")//注册
    val ControctCarUrl = BaseUrl.plus("/app/controct")//小车调度


}
