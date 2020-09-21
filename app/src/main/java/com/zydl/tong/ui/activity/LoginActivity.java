package com.zydl.tong.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.zydl.tong.R;
import com.zydl.tong.base.BaseActivity;
import com.zydl.tong.ui.presenter.LoginActivityPresenter;
import com.zydl.tong.ui.view.LoginView;

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

}
