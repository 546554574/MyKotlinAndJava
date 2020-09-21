package com.zydl.tong.widget

import java.util.ArrayList

/**
 * Created by zhengliang on 2016/10/15 0015.
 * 客户端使用类,记录一系列的不同移动轨迹
 */

class AnimatorPath {

    var offset: Float = 0f //坐标偏移量

    constructor(f: Float) {
        offset = f
    }

    //一系列的轨迹记录动作
    private val mPoints = ArrayList<PathPoint>()
    /**
     *
     * @return  返回移动动作集合
     */
    val points: Collection<PathPoint>
        get() = mPoints

    /**
     * 移动位置到:
     * @param x
     * @param y
     */
    fun moveTo(x: Float, y: Float) {
        mPoints.add(PathPoint.moveTo(x + offset, y + offset))
    }

    /**
     * 直线移动
     * @param x
     * @param y
     */
    fun lineTo(x: Float, y: Float) {
        mPoints.add(PathPoint.lineTo(x+ offset, y+ offset))
    }

    /**
     * 二阶贝塞尔曲线移动
     * @param c0X
     * @param c0Y
     * @param x
     * @param y
     */
    fun secondBesselCurveTo(c0X: Float, c0Y: Float, x: Float, y: Float) {
        mPoints.add(PathPoint.secondBesselCurveTo(c0X+ offset, c0Y+ offset, x+ offset, y+ offset))
    }

    /**
     * 三阶贝塞尔曲线移动
     * @param c0X
     * @param c0Y
     * @param c1X
     * @param c1Y
     * @param x
     * @param y
     */
    fun thirdBesselCurveTo(c0X: Float, c0Y: Float, c1X: Float, c1Y: Float, x: Float, y: Float) {
        mPoints.add(PathPoint.thirdBesselCurveTo(c0X+ offset, c0Y+ offset, c1X+ offset, c1Y+ offset, x+ offset, y+ offset))
    }

    fun clear() {
        mPoints.clear()
    }
}
