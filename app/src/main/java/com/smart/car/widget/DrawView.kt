package com.smart.car.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.util.*


class DrawView : View {

    constructor(context: Context) : super(context) {
        initContext(context)
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        initContext(context)
    }

    private fun initContext(context: Context) {
        initPaint()
    }

    var mLinePaint: Paint? = null
    private fun initPaint() {
        mLinePaint = Paint()
        mLinePaint!!.isAntiAlias = true
        mLinePaint!!.strokeWidth = 5f
        mLinePaint!!.style = Paint.Style.STROKE
        mLinePaint!!.color = Color.GREEN
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                touchDown(event)
            }
            MotionEvent.ACTION_MOVE -> {
                touchMove(event)
            }
        }
        invalidate()
        return true
    }

    private fun touchUp() {
        invalidate()
    }

    private var mX: Float = 0.toFloat()
    private var mY: Float = 0.toFloat()
    var animotorPath: AnimatorPath = AnimatorPath(-60f)
    var path: Path = Path()
    fun touchDown(event: MotionEvent) {
        paths.add(path)
        var x = event.x
        var y = event.y
        mX = x
        mY = y
        path.moveTo(mX, mY)

        animotorPath.moveTo(mX, mY)
    }

    private fun touchMove(event: MotionEvent) {
        var x = event.x
        var y = event.y
        val previousX = mX
        val previousY = mY
        val dx = Math.abs(x - previousX)
        val dy = Math.abs(y - previousY)

        //两点之间的距离大于等于3时，生成贝塞尔绘制曲线
        if (dx >= 3 || dy >= 3) {
            //设置贝塞尔曲线的操作点为起点和终点的一半
            val cX = (x + previousX) / 2
            val cY = (y + previousY) / 2

            //二次贝塞尔，实现平滑曲线；previousX, previousY为操作点，cX, cY为终点
            path.quadTo(previousX, previousY, cX, cY)
            animotorPath.secondBesselCurveTo(previousX, previousY, cX, cY)

            //第二次执行时，第一次结束调用的坐标值将作为第二次调用的初始坐标值
            mX = x
            mY = y
        }
    }

    var paths: LinkedList<Path> = LinkedList<Path>()
    var pathDel: LinkedList<Path> = LinkedList<Path>()
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        for (path in paths) {
            canvas!!.drawPath(path, mLinePaint)
        }
    }

    fun clearView() {
        for (path in paths) {
            pathDel.add(path)
        }
        paths.clear()
        path.reset()
        animotorPath.clear()
        invalidate()
    }

    fun reDraw() {
        paths.addAll(pathDel)
        invalidate()
    }
}
