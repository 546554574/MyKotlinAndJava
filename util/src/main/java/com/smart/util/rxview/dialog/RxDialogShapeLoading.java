package com.zydl.util.rxview.dialog;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.zydl.util.R;
import com.zydl.util.rxtool.view.RxToast;
import com.zydl.util.rxview.dialog.shapeloadingview.RxShapeLoadingView;


/**
 * @author Vondear
 * @date 2017/3/16
 */

public class RxDialogShapeLoading extends RxDialog {

    private RxShapeLoadingView mLoadingView;
    private View mDialogContentView;

    public RxDialogShapeLoading(Context context, int themeResId) {
        super(context, themeResId);
        initView(context);
    }

    public RxDialogShapeLoading(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView(context);
    }

    public RxDialogShapeLoading(Context context) {
        super(context);
        initView(context);
    }

    public RxDialogShapeLoading(Activity context) {
        super(context);
        initView(context);
    }

    public RxDialogShapeLoading(Context context, float alpha, int gravity) {
        super(context, alpha, gravity);
        initView(context);
    }

    private void initView(Context context) {
        mDialogContentView = LayoutInflater.from(context).inflate(R.layout.dialog_shape_loading_view, null);
        mLoadingView = mDialogContentView.findViewById(R.id.loadView);
        setContentView(mDialogContentView);
    }

    public void cancel(RxCancelType code, String str) {
        cancel();
        switch (code) {
            case normal:
                RxToast.normal(str);
                break;
            case error:
                RxToast.error(str);
                break;
            case success:
                RxToast.success(str);
                break;
            case info:
                RxToast.info(str);
                break;
            default:
                RxToast.normal(str);
                break;
        }
    }

    public void cancel(String str) {
        cancel();
        RxToast.normal(str);
    }

    public void setLoadingText(CharSequence charSequence) {
        mLoadingView.setLoadingText(charSequence);
    }

    public RxShapeLoadingView getLoadingView() {
        return mLoadingView;
    }

    public View getDialogContentView() {
        return mDialogContentView;
    }

    public enum RxCancelType {normal, error, success, info}
}
