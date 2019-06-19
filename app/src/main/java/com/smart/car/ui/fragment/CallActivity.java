package com.smart.car.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.OnClick;

import com.blankj.rxbus.RxBus;
import com.smart.car.R;
import com.smart.car.base.AppConstant;
import com.smart.car.base.BaseActivity;
import com.smart.car.base.BaseFragment;
import com.smart.car.busmsg.SendMsg;
import com.smart.car.ui.model.SendMsgVo;
import com.smart.car.ui.presenter.CallActivityPresenter;
import com.smart.car.ui.view.CallView;

public class CallActivity extends BaseFragment<CallView, CallActivityPresenter> implements CallView {
    @BindView(R.id.iv_call)
    ImageView iv_call;
    public static CallActivity fragment;

    public static CallActivity newInstance() {
        if (fragment == null) {
            Bundle args = new Bundle();
            fragment = new CallActivity();
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public int getLayout() {
        return R.layout.activity_call;
    }

    @Override
    public CallActivityPresenter initPresenter() {
        return null;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        iv_call = getMRootView().findViewById(R.id.iv_call);
        iv_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMsgVo vo = new SendMsgVo();
                vo.setId(AppConstant.ID);
                vo.setMsgCode(161);
                vo.setStateCode(AppConstant.STATE);
                RxBus.getDefault().post(new SendMsg(vo));
            }
        });
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }
}
