package com.smart.car.widget

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.smart.car.R
import com.smart.util.rxtool.RxDeviceTool

class MapView : View {
    var mapWidth = 0f //地图的宽
    var mapHeight = 0f //地图的高
    var scale = 0f  //地图和屏幕的尺寸比例
    var mPaddingLeft = 20f //距屏幕左端
    var mPaddingTopFirst = 40f  //默认距屏幕上端
    var mPaddingTop = 40f  //距上一条线的距离

    private var mMainPaint = Paint() //主线路的画笔
    private var mPointPaint = Paint()//位置点的画笔
    private var mTextPaint = Paint() //文字的画笔

    private var mLineColor = Color.BLUE
    private var mPointColor = Color.BLUE
    private var mTextColor = Color.BLUE
    private var mTextSize = 12f

    constructor(context: Context) : super(context) {
        initView(context)
    }

    @SuppressLint("ResourceType")
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        var typeArray = context.obtainStyledAttributes(attributeSet, R.styleable.MapView)
        mapWidth = typeArray.getFloat(R.styleable.MapView_map_width, 0f)
        mapHeight = typeArray.getFloat(R.styleable.MapView_map_height, 0f)
        mLineColor = typeArray.getColor(R.styleable.MapView_map_line_color, Color.BLUE)
        mPointColor = typeArray.getColor(R.styleable.MapView_map_point_color, Color.BLUE)
        mTextColor = typeArray.getColor(R.styleable.MapView_map_text_color, Color.BLUE)
        mTextSize = typeArray.getDimension(R.styleable.MapView_map_text_size, 24f)
        if (mapWidth <= 0 || mapHeight <= 0) {
            throw NoSetMapSizeError("请设置地图的长宽")
        }
        initPaint()
        typeArray.recycle()
        initView(context)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //以高度确定比例
        scale = RxDeviceTool.getScreenWidth(context) / mapWidth
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun initView(context: Context) {
    }

    fun scaleSize(size: Float): Float {
        return size * scale
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
//        var mPath = Path()
//        mPath.reset()
//        mPath.moveTo(100f, 500f)
//        mPath.lineTo(600f, 800f)
//        mPath.lineTo(800f, 800f)
//        mPath.close()
//        mPath.moveTo(100f, 500f)
//        mPath.quadTo(300f, 100f, 600f, 500f)
//        canvas!!.drawPath(mPath, mMainPaint)


        var mTop = mPaddingTopFirst
        if (printCount > 0) {
            canvas!!.drawText(
                printCount.toString(),
                scaleSize(mapWidth) - 20,
                mTop * printCount - 20f,
                mTextPaint
            )
        }
        canvas!!.drawLine(scaleSize(mapWidth) / 2, mTop, mPaddingLeft, mTop, mMainPaint)
        canvas!!.drawCircle(scaleSize(mapWidth) / 2, mTop, 10f, mPointPaint)
//        canvas.drawText("Test", scaleSize(mapWidth) / 2 - 10, mPaddingTop + 10, mTextPaint)
        canvas!!.drawLine(scaleSize(mapWidth) / 2, mTop, scaleSize(mapWidth), mTop, mMainPaint)

        mTop += mPaddingTop
        canvas!!.drawLine(scaleSize(mapWidth) / 2, mTop, mPaddingLeft, mTop, mMainPaint)
        canvas!!.drawCircle(scaleSize(mapWidth) / 2, mTop, 10f, mPointPaint)
//        canvas.drawText("Test", scaleSize(mapWidth) / 2 - 10, mPaddingTop + 10, mTextPaint)
        canvas!!.drawLine(scaleSize(mapWidth) / 2, mTop, scaleSize(mapWidth), mTop, mMainPaint)

        mTop += mPaddingTop
        canvas!!.drawLine(scaleSize(mapWidth) / 2, mTop, mPaddingLeft, mTop, mMainPaint)
        canvas!!.drawCircle(scaleSize(mapWidth) / 2, mTop, 10f, mPointPaint)
//        canvas.drawText("Test", scaleSize(mapWidth) / 2 - 10, mPaddingTop + 10, mTextPaint)
        canvas!!.drawLine(scaleSize(mapWidth) / 2, mTop, scaleSize(mapWidth), mTop, mMainPaint)

        mTop += mPaddingTop
        canvas!!.drawLine(scaleSize(mapWidth) / 2, mTop, mPaddingLeft, mTop, mMainPaint)
        canvas!!.drawCircle(scaleSize(mapWidth) / 2, mTop, 10f, mPointPaint)
//        canvas.drawText("Test", scaleSize(mapWidth) / 2 - 10, mPaddingTop + 10, mTextPaint)
        canvas!!.drawLine(scaleSize(mapWidth) / 2, mTop, scaleSize(mapWidth), mTop, mMainPaint)

        //绘制竖线
        canvas.drawLine(scaleSize(mapWidth) / 2, mPaddingTopFirst, scaleSize(mapWidth) / 2, mTop, mMainPaint)

//        startTranslateX(this)
    }

    var printCount: Int = 1
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_MOVE, MotionEvent.ACTION_DOWN -> {
                val x = event.x
                val y = event.y
                if (x in scaleSize(mapWidth) / 2 - 10..scaleSize(mapWidth) / 2 + 10) {
                    for (count in 1 until 5) {
                        if (y in count * mPaddingTop - 20..count * mPaddingTop + 20) {
                            printCount = count
                            break
                        }
                    }
                    invalidate()
                    return true
                }
            }

            MotionEvent.ACTION_UP -> {
//                printCount = -1
//                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    /**
     * 初始化画笔
     * init the paints
     */
    private fun initPaint() {

        mMainPaint.style = Paint.Style.FILL
        mMainPaint.strokeWidth = 5f
        mMainPaint.color = mLineColor
        mMainPaint.isAntiAlias = true
        mMainPaint.strokeCap = Paint.Cap.ROUND

        mPointPaint.style = Paint.Style.FILL
        mPointPaint.color = mPointColor

        mTextPaint.style = Paint.Style.FILL
        mTextPaint.color = mTextColor
        mTextPaint.textSize = mTextSize

    }

    fun startTranslateX(view: View){
        val currentX: Float= view.translationX
        val translateXAnimator = ObjectAnimator.ofFloat(view,"translationX",currentX,-500f,currentX,500f,currentX)
        translateXAnimator.duration = 6000
        translateXAnimator.start()
    }
    inner class NoSetMapSizeError(message: String?) : Exception(message) {

    }

}