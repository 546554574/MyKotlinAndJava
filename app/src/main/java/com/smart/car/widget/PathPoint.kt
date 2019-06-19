package com.smart.car.widget

/**
 * Created by zhengliang on 2016/10/15 0015.
 * 记录view移动动作的坐标点
 */

class PathPoint {
    /**
     * View移动到的最终位置
     */
    var mX: Float = 0.toFloat()
    var mY: Float = 0.toFloat()
    /**
     * 控制点
     */
    var mContorl0X: Float = 0.toFloat()
    var mContorl0Y: Float = 0.toFloat()
    var mContorl1X: Float = 0.toFloat()
    var mContorl1Y: Float = 0.toFloat()
    //操作符
    var mOperation: Int = 0

    /**
     * Line/Move都通过该构造函数来创建
     */
    constructor(mOperation: Int, mX: Float, mY: Float) {
        this.mX = mX
        this.mY = mY
        this.mOperation = mOperation
    }

    /**
     * 二阶贝塞尔曲线
     * @param mX
     * @param mY
     * @param mContorl0X
     * @param mContorl0Y
     */
    constructor(mContorl0X: Float, mContorl0Y: Float, mX: Float, mY: Float) {
        this.mX = mX
        this.mY = mY
        this.mContorl0X = mContorl0X
        this.mContorl0Y = mContorl0Y
        this.mOperation = SECOND_CURVE
    }

    /**
     * 三阶贝塞尔曲线
     * @param mContorl0x
     * @param mContorl0Y
     * @param mContorl1x
     * @param mContorl1Y
     * @param mX
     * @param mY
     */
    constructor(mContorl0x: Float, mContorl0Y: Float, mContorl1x: Float, mContorl1Y: Float, mX: Float, mY: Float) {
        this.mX = mX
        this.mY = mY
        this.mContorl0X = mContorl0x
        this.mContorl0Y = mContorl0Y
        this.mContorl1X = mContorl1x
        this.mContorl1Y = mContorl1Y
        this.mOperation = THIRD_CURVE
    }

    companion object {
        /**
         * 起始点操作
         */
        val MOVE = 0
        /**
         * 直线路径操作
         */
        val LINE = 1
        /**
         * 二阶贝塞尔曲线操作
         */
        val SECOND_CURVE = 2
        /**
         * 三阶贝塞尔曲线操作
         */
        val THIRD_CURVE = 3

        /**
         * 为了方便使用都用静态的方法来返回路径点
         */
        fun moveTo(x: Float, y: Float): PathPoint {
            return PathPoint(MOVE, x, y)
        }

        fun lineTo(x: Float, y: Float): PathPoint {
            return PathPoint(LINE, x, y)
        }

        fun secondBesselCurveTo(c0X: Float, c0Y: Float, x: Float, y: Float): PathPoint {
            return PathPoint(c0X, c0Y, x, y)
        }

        fun thirdBesselCurveTo(c0X: Float, c0Y: Float, c1X: Float, c1Y: Float, x: Float, y: Float): PathPoint {
            return PathPoint(c0X, c0Y, c1X, c1Y, x, y)
        }
    }
}
