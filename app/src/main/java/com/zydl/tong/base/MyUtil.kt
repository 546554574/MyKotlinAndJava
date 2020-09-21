package com.zydl.tong.base

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.blankj.rxbus.RxBus
import com.vondear.rxtool.RxFileTool
import com.zydl.tong.api.ServiceManager
import com.zydl.tong.busmsg.SendMsg
import com.zydl.tong.ui.model.SendMsgVo
import com.zydl.tong.ui.model.UpdateAppVo

object MyUtil {

    /**
     * 截图照片存放路径
     *
     * @return
     */
    val basePath: String
        get() = RxFileTool.getSDCardPath() + "smartCar/"

    /**
     * 跳转到支付宝并打开百度
     */
    fun toAliPayScan(context: Context) {
        try {
            //利用Intent打开支付宝
            //支付宝跳过开启动画打开扫码和付款码的urlscheme分别是：
            //alipayqr://platformapi/startapp?saId=10000007
            //alipayqr://platformapi/startapp?saId=20000056
            val uri = Uri.parse("alipayqr://platformapi/startapp?appId=20000067&url=http://marketpay.ccb.com/online/mobile/paychoose.html?cmdtyOrdrNo=20191115105054&pyOrdrNo=105005373999480050817294739076&ordrEnqrFcnCd=1&MAC=6d5fb74375741dee2c459bd0ec4360da")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        } catch (e: Exception) {

        }

    }


    /**
     * 计算两点之间距离
     *
     * @return 米
     */
    fun getDistance(startLat: Double, startLon: Double, endLat: Double, endLon: Double): Double? {
        if (startLat <= 0 || startLon <= 0 || endLat <= 0 || endLon <= 0) {
            return -1.0
        }
        val lat1 = Math.PI / 180 * startLat
        val lat2 = Math.PI / 180 * endLat

        val lon1 = Math.PI / 180 * startLon
        val lon2 = Math.PI / 180 * endLon

        //地球半径
        val R = 6378.137

        //两点间距离 km，如果想要米的话，结果*1000
        val d = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1)) * R
        //        if (d < 1)
        //            return (Double) (d * 1000);
        //        else
        return java.lang.Double.valueOf(String.format("%.2f", d * 1000))
    }
}
