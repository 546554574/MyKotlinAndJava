package com.smart.car.ui.model

class SendMsgVo {

    var id = 0
    /**
     * 消息码
     * 0xa0/160 状态检测
    0xa1/161 呼叫空车
    0xa2/162 派遣重车
    0xa3/163 释放车辆
    0xa9/169 清空状态
     */
    var msgCode = 0

    /**
     * 状态码
     *  0x00 空闲
    0x01 呼叫空车
    0x02 重车保持
    0x03 派遣重车
    0x04 任务被抛弃
    0x05 等待空车调离
    0x06 被故障车占用
     */
    var stateCode = 0

    /**
     * 目的地
     */
    var destination = 0

    /**
     * 状态监测命令为true，呼叫派遣之类的命令为false
     */
    var isCheckState = false

    fun getSendBuffer(): ByteArray {
        var sendBuffer = byteArrayOf(
                0x01
                , 0xEA.toByte()
                , id.toByte()
                , msgCode.toByte()
                , stateCode.toByte()
                , destination.toByte()
                , 0x01, 0xEA.toByte()
        )
        return sendBuffer
    }

    override fun toString(): String {
        return "id：".plus(id).plus("--消息码：".plus(msgCode).plus("--状态码：").plus(stateCode).plus("--目的地：".plus(destination)))
    }
}