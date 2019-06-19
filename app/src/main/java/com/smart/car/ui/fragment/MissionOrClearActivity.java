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
import com.smart.car.busmsg.GoToMissionFragmentMsg;
import com.smart.car.busmsg.SendMsg;
import com.smart.car.busmsg.UpdateState;
import com.smart.car.ui.model.SendMsgVo;
import com.smart.car.ui.presenter.CallActivityPresenter;
import com.smart.car.ui.view.CallView;
import com.smart.car.widget.dialog.ClearDialog;

public class MissionOrClearActivity extends BaseFragment<CallView, CallActivityPresenter> implements CallView {
    @BindView(R.id.iv_mission)
    ImageView iv_mission;
    @BindView(R.id.iv_clear)
    ImageView iv_clear;

    @Override
    public int getLayout() {
        return R.layout.activity_misorclear;
    }

    @Override
    public CallActivityPresenter initPresenter() {
        return null;
    }

    public static MissionOrClearActivity fragment;

    public static MissionOrClearActivity newInstance() {
        if (fragment == null) {
            Bundle args = new Bundle();
            fragment = new MissionOrClearActivity();
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        iv_mission = getMRootView().findViewById(R.id.iv_mission);
        iv_clear = getMRootView().findViewById(R.id.iv_clear);
        iv_mission.setOnClickListener(clickListener);
        iv_clear.setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_mission:
                    RxBus.getDefault().post(new GoToMissionFragmentMsg());
                    break;
                case R.id.iv_clear:
                    final ClearDialog dialogSureCancel = new ClearDialog(getContext());
                    dialogSureCancel.getLogoView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogSureCancel.cancel();
                        }
                    });
                    dialogSureCancel.getSureView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SendMsgVo vo = new SendMsgVo();
                            vo.setId(AppConstant.ID);
                            vo.setMsgCode(163);
                            vo.setStateCode(AppConstant.STATE);
                            RxBus.getDefault().post(new SendMsg(vo));
                            dialogSureCancel.dismiss();
                        }
                    });
                    dialogSureCancel.getCancelView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogSureCancel.cancel();
                        }
                    });
                    dialogSureCancel.show();
                    break;
            }
        }
    };

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }
}
