package com.zydl.tong.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.OnClick;

import com.zydl.tong.R;
import com.zydl.tong.api.ServiceManager;
import com.zydl.tong.base.BaseActivity;
import com.zydl.tong.ui.presenter.RegistActivityPresenter;
import com.zydl.tong.ui.view.RegistView;

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

}
