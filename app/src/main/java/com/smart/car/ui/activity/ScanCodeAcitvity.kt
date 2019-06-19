package com.smart.car.ui.activity

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import com.blankj.rxbus.RxBus
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.ReaderException
import com.google.zxing.Result
import com.google.zxing.common.DecoderResult
import com.google.zxing.common.HybridBinarizer
import com.smart.car.R
import com.smart.car.base.AppConstant
import com.smart.car.base.BaseActivity
import com.smart.car.base.MyUtil
import com.smart.car.busmsg.SendMsg
import com.smart.car.busmsg.UpdateState
import com.smart.car.ui.model.SendMsgVo
import com.smart.car.ui.presenter.ScanCodePresenter
import com.smart.car.ui.view.ScanCodeView
import com.smart.car.utils.*
import com.smart.util.rxtool.*
import com.smart.util.rxtool.view.RxToast
import com.smart.util.rxview.dialog.RxDialogSure
import kotlinx.android.synthetic.main.activity_basic.*
import kotlinx.android.synthetic.main.activity_scan_code.*
import java.io.IOException
import java.lang.Exception
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger
import kotlin.experimental.and

class ScanCodeAcitvity : BaseActivity<ScanCodeView, ScanCodePresenter>(), ScanCodeView {
    private var inactivityTimer: InactivityTimer? = null
    private var hasSurface: Boolean = false

    override fun getLayout(): Int {
        return R.layout.activity_scan_code
    }

    override fun initPresenter(): ScanCodePresenter {
        return ScanCodePresenter()
    }

    override fun loadMore() {
    }

    override fun refreData() {
    }

    override fun init(savedInstanceState: Bundle?) {
        left_ll.visibility = View.GONE
        right_tv.visibility = View.VISIBLE
        right_tv.text = "相册"
        right_tv.setOnClickListener {
            RxPhotoTool.openLocalImage(this)
        }
        //权限初始化
//        initPermission()
        //扫描动画初始化
        initScanerAnimation()
        //初始化 CameraManager
        CameraManager.init(this)
        hasSurface = false
        inactivityTimer = InactivityTimer(this)
    }

    var multiFormatReader = QRCodeReader()

    private fun initPermission() {
        //请求Camera权限 与 文件读写 权限
        RxPermissionsTool.with(this).addPermission(Manifest.permission.CAMERA).initPermission()
        //请求Camera权限 与 文件读写 权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }
    }

    private fun initScanerAnimation() {
        RxAnimationTool.ScaleUpDowm(capture_scan_line)
    }

    override fun getTitleStr(): String {
        return "扫码"
    }

    override fun initEventAndData() {
        cancelBtn.setOnClickListener {
            //todo:取消按钮监听事件
            RxActivityTool.AppExit(context)
        }
    }

    override fun onResume() {
        super.onResume()
        initPermission()
        val surfaceView = capture_preview
        val surfaceHolder = surfaceView.getHolder()
        if (hasSurface) {
            //Camera初始化
            initCamera(surfaceHolder)
        } else {
            surfaceHolder.addCallback(object : SurfaceHolder.Callback {
                override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

                }

                override fun surfaceCreated(holder: SurfaceHolder) {
                    if (!hasSurface) {
                        hasSurface = true
                        initCamera(holder)
                    }
                }

                override fun surfaceDestroyed(holder: SurfaceHolder) {
                    hasSurface = false

                }
            })
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        }
    }

    override fun onPause() {
        super.onPause()
        if (handler != null) {
            handler!!.quitSynchronously()
            handler!!.removeCallbacksAndMessages(null)
            handler = null
        }
        CameraManager.get().closeDriver()
    }

    override fun onDestroy() {
        inactivityTimer!!.shutdown()
        mScanerListener = null
        super.onDestroy()
    }

    /**
     * 扫描边界的宽度
     */
    private var mCropWidth = 0

    /**
     * 扫描边界的高度
     */
    private var mCropHeight = 0

    private fun initCamera(surfaceHolder: SurfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder)
            val point = CameraManager.get().cameraResolution
            val width = AtomicInteger(point.y)
            val height = AtomicInteger(point.x)
            val cropWidth = capture_crop_layout.getWidth() * width.get() / capture_containter.getWidth()
            val cropHeight = capture_crop_layout.getHeight() * height.get() / capture_containter.getHeight()
            setCropWidth(cropWidth)
            setCropHeight(cropHeight)
        } catch (ioe: IOException) {
            return
        } catch (ioe: RuntimeException) {
            return
        }

        if (handler == null) {
            handler = CaptureActivityHandler()
        }
    }

    fun setCropWidth(cropWidth: Int) {
        mCropWidth = cropWidth
        CameraManager.FRAME_WIDTH = mCropWidth

    }

    fun setCropHeight(cropHeight: Int) {
        this.mCropHeight = cropHeight
        CameraManager.FRAME_HEIGHT = mCropHeight
    }

    /**
     * 扫描处理
     */
    private var handler: CaptureActivityHandler? = null
    //==============================================================================================解析结果 及 后续处理 end

    internal inner class CaptureActivityHandler : Handler() {

        var decodeThread: DecodeThread? = null
        private var state: State? = null

        init {
            decodeThread = DecodeThread()
            decodeThread!!.start()
            state = State.SUCCESS
            CameraManager.get().startPreview()
            restartPreviewAndDecode()
        }

        override fun handleMessage(message: Message) {
            if (message.what == R.id.auto_focus) {
                if (state == State.PREVIEW) {
                    CameraManager.get().requestAutoFocus(this, R.id.auto_focus)
                }
            } else if (message.what == R.id.restart_preview) {
                restartPreviewAndDecode()
            } else if (message.what == R.id.decode_succeeded) {
                state = State.SUCCESS
                handleDecode(message.obj as DecoderResult)// 解析成功，回调
            } else if (message.what == R.id.decode_failed) {
                state = State.PREVIEW
                CameraManager.get().requestPreviewFrame(decodeThread!!.getHandler(), R.id.decode)
            }
        }

        fun quitSynchronously() {
            state = State.DONE
            decodeThread!!.interrupt()
            CameraManager.get().stopPreview()
            removeMessages(R.id.decode_succeeded)
            removeMessages(R.id.decode_failed)
            removeMessages(R.id.decode)
            removeMessages(R.id.auto_focus)
        }

        private fun restartPreviewAndDecode() {
            if (state == State.SUCCESS) {
                state = State.PREVIEW
                CameraManager.get().requestPreviewFrame(decodeThread!!.getHandler(), R.id.decode)
                CameraManager.get().requestAutoFocus(this, R.id.auto_focus)
            }
        }
    }

    /**
     * 扫描成功后是否震动
     */
    private val vibrate = true
    /**
     * 扫描结果监听
     */
    private var mScanerListener: OnRxScanerListener? = null

    fun handleDecode(result: DecoderResult) {
        inactivityTimer!!.onActivity()
        //扫描成功之后的振动与声音提示
        RxBeepTool.playBeep(this, vibrate)
        if (mScanerListener == null) {
//            RxToast.success(result1)
//            initDialogResult(result)
            try {
                AppConstant.ID = Integer.valueOf(RxDataTool.bytes2HexString(result.byteSegments[0]), 16)
                finish()
                MyUtil.sendRequestStateMsg()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            mScanerListener!!.onSuccess("From to Camera", result)
        }
    }

    /**
     * 扫描结果显示框
     */
    private var rxDialogSure: RxDialogSure? = null

    private fun initDialogResult(result: Result) {
        val type = result.barcodeFormat
        val realContent = result.text

        if (rxDialogSure == null) {
            //提示弹窗
            rxDialogSure = RxDialogSure(this)
        }

        if (BarcodeFormat.QR_CODE == type) {
            rxDialogSure!!.setTitle("二维码扫描结果")
        } else if (BarcodeFormat.EAN_13 == type) {
            rxDialogSure!!.setTitle("条形码扫描结果")
        } else {
            rxDialogSure!!.setTitle("扫描结果")
        }

        rxDialogSure!!.setContent(realContent)
        rxDialogSure!!.setSureListener(View.OnClickListener { rxDialogSure!!.cancel() })
        rxDialogSure!!.setOnCancelListener(DialogInterface.OnCancelListener {
            if (handler != null) {
                // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
                handler!!.sendEmptyMessage(R.id.restart_preview)
            }
        })

        if (!rxDialogSure!!.isShowing()) {
            rxDialogSure!!.show()
        }

        RxSPTool.putContent(this, RxConstants.SP_SCAN_CODE, (RxDataTool.stringToInt(RxSPTool.getContent(this, RxConstants.SP_SCAN_CODE)) + 1).toString())
    }


    internal inner class DecodeThread : Thread() {

        private val handlerInitLatch: CountDownLatch
        private var handler: Handler? = null

        init {
            handlerInitLatch = CountDownLatch(1)
        }

        fun getHandler(): Handler? {
            try {
                handlerInitLatch.await()
            } catch (ie: InterruptedException) {
                // continue?
            }

            return handler
        }

        override fun run() {
            Looper.prepare()
            handler = DecodeHandler()
            handlerInitLatch.countDown()
            Looper.loop()
        }
    }

    internal inner class DecodeHandler : Handler() {

        override fun handleMessage(message: Message) {
            if (message.what == R.id.decode) {
                decode(message.obj as ByteArray, message.arg1, message.arg2)
            } else if (message.what == R.id.quit) {
                Looper.myLooper()!!.quit()
            }
        }
    }

    private fun decode(data: ByteArray, width: Int, height: Int) {
        var width = width
        var height = height
        val start = System.currentTimeMillis()
        var rawResult: Result? = null

        //modify here
        val rotatedData = ByteArray(data.size)
        for (y in 0 until height) {
            for (x in 0 until width) {
                rotatedData[x * height + height - y - 1] = data[x + y * width]
            }
        }
        // Here we are swapping, that's the difference to #11
        val tmp = width
        width = height
        height = tmp

        val source = CameraManager.get().buildLuminanceSource(rotatedData, width, height)
        val bitmap = BinaryBitmap(HybridBinarizer(source))
        try {
            rawResult = multiFormatReader!!.decode(bitmap)
        } catch (e: ReaderException) {
            // continue
        } finally {
            multiFormatReader!!.reset()
        }

        if (rawResult != null) {
            val end = System.currentTimeMillis()
            Log.d(TAG, "Found barcode (" + (end - start) + " ms):\n" + rawResult.toString())
            val message = Message.obtain(handler, R.id.decode_succeeded, multiFormatReader.decoderResult)
            val bundle = Bundle()
            bundle.putParcelable("barcode_bitmap", source.renderCroppedGreyscaleBitmap())
            message.data = bundle
            //Log.d(TAG, "Sending decode succeeded message...");
            message.sendToTarget()
        } else {
            if (handler != null) {
                val message = Message.obtain(handler, R.id.decode_failed)
                message.sendToTarget()
            }
        }
    }

    //--------------------------------------打开本地图片识别二维码 start---------------------------------
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val resolver = contentResolver
            // 照片的原始资源地址
            val originalUri = data!!.data
            try {
                // 使用ContentProvider通过URI获取原始图片
                val photo = MediaStore.Images.Media.getBitmap(resolver, originalUri)

                // 开始对图像资源解码
                val rawResult = RxQrBarTool.decodeQrFromPhotoToByte(photo)
                if (rawResult != null) {
                    if (mScanerListener == null) {
//                        initDialogResult(rawResult!!)
                        val rawBytes = RxDataTool.bytes2HexString(rawResult.byteSegments[0])
                        try {
                            AppConstant.ID = Integer.valueOf(RxDataTool.bytes2HexString(rawResult.byteSegments[0]), 16)
                            finish()
                            MyUtil.sendRequestStateMsg()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        mScanerListener!!.onSuccess("From to Picture", rawResult)
                    }
                } else {
                    if (mScanerListener == null) {
                        RxToast.error("图片识别失败.")
                    } else {
                        mScanerListener!!.onFail("From to Picture", "图片识别失败")
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    // 无符号
    fun convertFourUnSignInt(byteArray: ByteArray): Int =
            (byteArray[1].toInt() and 0xFF) shl 8 or (byteArray[0].toInt() and 0xFF)

    //========================================打开本地图片识别二维码 end=================================
    private enum class State {
        //预览
        PREVIEW,
        //成功
        SUCCESS,
        //完成
        DONE
    }
}