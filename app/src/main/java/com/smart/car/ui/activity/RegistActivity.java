package com.smart.car.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.OnClick;

import com.google.gson.JsonObject;
import com.smart.car.R;
import com.smart.car.api.ServiceManager;
import com.smart.car.base.BaseActivity;
import com.smart.car.helper.rxjavahelper.RxObserver;
import com.smart.car.helper.rxjavahelper.RxSchedulersHelper;
import com.smart.car.ui.presenter.RegistActivityPresenter;
import com.smart.car.ui.view.RegistView;
import com.smart.util.rxtool.view.RxToast;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

public class RegistActivity extends BaseActivity<RegistView, RegistActivityPresenter> implements RegistView {
    @BindView(R.id.et_account)
    EditText et_account;
    @BindView(R.id.et_pwd)
    EditText et_pwd;
    @BindView(R.id.btn_login)
    Button btn_login;

    @Override
    public int getLayout() {
        return R.layout.activity_regist;
    }

    @NotNull
    @Override
    public RegistActivityPresenter initPresenter() {
        return new RegistActivityPresenter();
    }

    @Override
    public void loadMore() {

    }

    @Override
    public void refreData() {

    }

    @Override
    public void init(@Nullable Bundle savedInstanceState) {

    }

    @NotNull
    @Override
    public String getTitleStr() {
        return "";
    }

    @Override
    public void initEventAndData() {

    }

    @OnClick({R.id.btn_login, R.id.tv_findpwd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                ServiceManager.INSTANCE
                        .regist(et_account.getText().toString().trim(), et_pwd.getText().toString().trim())
                        .compose(RxSchedulersHelper.INSTANCE.<String>io_main())
                        .subscribe(new RxObserver<String>() {
                            @Override
                            public void onErrorMy(@NotNull String errorMessage) {
                                RxToast.error(errorMessage);
                            }

                            @Override
                            public void onNextMy(String s) {
                                checkIdentity();
                            }
                        });
                break;
        }
    }

    private void checkIdentity() {
        ServiceManager.INSTANCE.checkIdentity()
                .compose(RxSchedulersHelper.INSTANCE.<String>io_main())
                .subscribe(new RxObserver<String>() {
                    @Override
                    public void onErrorMy(@NotNull String errorMessage) {

                    }

                    @Override
                    public void onNextMy(String s) {
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            if (jsonObject.has("code")) {
                                int code = jsonObject.getInt("code");
                                if (code == 100) {
                                    startActivity(new Intent(RegistActivity.this, ScanCodeAcitvity.class));
                                    RegistActivity.this.finish();
                                } else {
                                    return;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
