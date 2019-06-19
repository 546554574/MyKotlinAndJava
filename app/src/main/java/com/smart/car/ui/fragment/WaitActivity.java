package com.smart.car.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.blankj.rxbus.RxBus;
import com.smart.car.R;
import com.smart.car.base.AppConstant;
import com.smart.car.base.BaseActivity;
import com.smart.car.base.BaseFragment;
import com.smart.car.busmsg.SendMsg;
import com.smart.car.ui.model.SendMsgVo;
import com.smart.car.ui.presenter.CallActivityPresenter;
import com.smart.car.ui.view.CallView;

import butterknife.BindView;
import butterknife.OnClick;

public class WaitActivity extends BaseFragment<CallView, CallActivityPresenter> implements CallView {
    @BindView(R.id.iv_call)
    ImageView iv_call;

    @Override
    public int getLayout() {
        return R.layout.activity_wait;
    }

    @Override
    public CallActivityPresenter initPresenter() {
        return null;
    }


    public static WaitActivity fragment;

    public static WaitActivity newInstance() {
        if (fragment == null) {
            Bundle args = new Bundle();
            fragment = new WaitActivity();
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void init(Bundle savedInstanceState) {
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }
}
