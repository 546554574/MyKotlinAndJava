package com.smart.car.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.smart.car.R;
import com.smart.car.base.BaseActivity;
import com.smart.car.base.SharedPreferencesUtils;
import com.smart.car.ui.fragment.MissionActivity;
import com.smart.car.ui.presenter.LoginActivityPresenter;
import com.smart.car.ui.view.LoginView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity<LoginView, LoginActivityPresenter> implements LoginView{
    @BindView(R.id.et_account)
    EditText et_account;
    @BindView(R.id.et_pwd)
    EditText et_pwd;
    @BindView(R.id.btn_login)
    Button btn_login;
    @BindView(R.id.tv_findpwd)
    TextView tv_findpwd;
    @BindView(R.id.iv_check)
    ImageView iv_check;

    @Override
    public int getLayout() {
        return R.layout.activity_login;
    }

    @NotNull
    @Override
    public LoginActivityPresenter initPresenter() {
        return new LoginActivityPresenter();
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

    @OnClick({R.id.ll_remember,R.id.btn_login,R.id.tv_findpwd})
    public void onViewClicked(View view){
        switch (view.getId()){
            case R.id.ll_remember:
                iv_check.setSelected(!iv_check.isSelected());
                SharedPreferencesUtils.setParam(this,"remember",iv_check.isSelected());
                break;
            case R.id.btn_login:
                mPresenter.sendLogin(et_account.getText().toString().trim(),et_pwd.getText().toString().trim());
                startActivity(new Intent(this,MissionActivity.class));
                break;
            case R.id.tv_findpwd:
                break;
        }
    }
}
