package com.zydl.tong.widget.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import com.zydl.tong.R
import com.zydl.tong.ui.model.UpdateAppVo
import kotlinx.android.synthetic.main.layout_dialog_update_app.*


class UpdateAppDialog : Dialog {
    internal var context: Context? = null
    var isMustUpdate: Boolean = false
    var updateVo: UpdateAppVo? = null
    var mainView: View? = null

    var onUpdateAppClickListener: OnUpdateAppClickListener? = null

    constructor(context: Context, updateVo: UpdateAppVo?, isMustUpdate: Boolean) : super(
        context,
        R.style.dialog_style
    ) {
        this.context = context
        this.isMustUpdate = isMustUpdate
        this.updateVo = updateVo
        initView(context)
    }

    constructor(context: Context) : super(context, R.style.dialog_style) {
        this.context = context
        initView(context)
    }

    constructor(context: Context, themeResId: Int) : super(context, R.style.dialog_style) {
        this.context = context
        initView(context)
    }

    protected constructor(
        context: Context,
        cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener
    ) : super(context, cancelable, cancelListener) {
        this.context = context
        initView(context)
    }

    private fun initView(context: Context) {
        mainView = View.inflate(context, R.layout.layout_dialog_update_app, null)
        setContentView(mainView!!)
        version_num_tv!!.text = updateVo!!.data!!.num
        content_tv!!.text = updateVo!!.data!!.content
        if (isMustUpdate) {
            now_update_tv!!.visibility = View.GONE
        } else {
            now_update_tv!!.visibility = View.VISIBLE
        }
        no_update_tv!!.setOnClickListener { dismiss() }
        now_update_tv!!.setOnClickListener {
            dismiss()
            //通知通知栏显示更新
            if (onUpdateAppClickListener != null) {
                onUpdateAppClickListener!!.onUpdate()
            }
        }
    }

    interface OnUpdateAppClickListener {
        fun onUpdate()
    }
}
