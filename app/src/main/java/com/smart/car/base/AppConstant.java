package com.smart.car.base;

import com.smart.car.ui.model.GroupAndPlaceVo;

public class AppConstant {
    public static String UPDATE_ACTION = "ACTION_MY_APP_UPDATE";
    public static GroupAndPlaceVo groupandplaceVo;
    public static int ID = 0;//工位ID
    public static final double MAX_DISTANCE = 1000;
    public static double FAC_LAT = 114.009837;//厂区经度
    public static double FAC_LON = 35.452671;//厂区纬度
    public static int STATE = 0;//当前工位状态

    /**
     * 0x00 空闲
     * 0x01 呼叫空车
     * 0x02 重车保持
     * 0x03 派遣重车
     * 0x04 任务被抛弃
     * 0x05 等待空车调离
     * 0x06 被故障车占用
     * 0xff 错误
     */
    public final static int STATE_0 = 0;
    public final static int STATE_1 = 1;
    public final static int STATE_2 = 2;
    public final static int STATE_3 = 3;
    public final static int STATE_4 = 4;
    public final static int STATE_5 = 5;
    public final static int STATE_6 = 6;
    public final static int STATE_FF = -1;
}
