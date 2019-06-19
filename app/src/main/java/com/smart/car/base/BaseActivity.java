package com.smart.car.base;

import android.Manifest;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;

import com.blankj.rxbus.RxBus;
import com.smart.car.R;
import com.smart.car.ui.activity.RegistActivity;
import com.smart.car.ui.activity.ScanCodeAcitvity;
import com.smart.util.rxtool.RxActivityTool;
import com.smart.util.rxtool.RxPermissionsTool;
import com.smart.util.rxtool.view.RxToast;
import com.smart.util.rxview.progressing.style.ThreeBounce;
import com.zhy.changeskin.SkinManager;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity<V, T extends BasePresenterImpl<V>> extends AppCompatActivity implements BaseView {

    public Context context;
    public T mPresenter;
    private Unbinder mBinder;
    /**
     * 传来的initViewID对应的View
     */
    private View viewActivity;
    private long exitTime = 0;
    private RelativeLayout loading_layout;

    /**
     * 判断是否是主页面
     *
     * @return
     */
    private boolean isLoginOut;
    private RelativeLayout basic_view;
    private LinearLayout left_ll;
    private TextView right_tv;
    private ImageView right_iv;
    private RelativeLayout toolbar;
    private ProgressBar progress;
    private TextView title_tv;

    public boolean isLoginOut() {
        return getClass().getSimpleName().equals(RegistActivity.class.getSimpleName()) || getClass().getSimpleName().equals(ScanCodeAcitvity.class.getSimpleName());
    }

    public void showProDialog() {
        try {
            loading_layout.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void hideProDialog() {
        try {
            loading_layout.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        RxActivityTool.addActivity(this);
        SkinManager.getInstance().register(this);
        View view = View.inflate(context, R.layout.activity_basic, null);
        setContentView(view);
        this.viewActivity = View.inflate(context, getLayout(), null);
        basic_view = findViewById(R.id.basic_view);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        basic_view.addView(viewActivity, layoutParams);
        loading_layout = findViewById(R.id.loading_layout);
        left_ll = findViewById(R.id.left_ll);
        right_iv = findViewById(R.id.right_iv);
        right_tv = findViewById(R.id.right_tv);
        progress = findViewById(R.id.progress);
        title_tv = findViewById(R.id.title_tv);
        toolbar = findViewById(R.id.toolbar);
        /**
         * 设置loading样式
         */
        setLoadingStyle();
        mBinder = ButterKnife.bind(this);
        //initPresenter()是抽象方法，让view初始化自己的presenter
        mPresenter = initPresenter();
        //presenter和view的绑定
        if (mPresenter != null) {
            mPresenter.attachView((V) this);
        }
        //标题  ：布局文件中引入lyout_title
        if (!TextUtils.isEmpty(getTitleStr())) {
            if (left_ll != null) {
                left_ll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onTopLeftListener();
                    }
                });
            }
            if (right_tv != null) {
                right_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ;
                        onTopRightTvListener();
                    }
                });
            }
            if (right_iv != null) {
                right_iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onTopRightIvListener();
                    }
                });
            }
            setToolBar(getTitleStr());
        } else {
            toolbar.setVisibility(View.GONE);
        }
        RxPermissionsTool.with(this)
                .addPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .addPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .addPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                .addPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .addPermission(Manifest.permission.READ_PHONE_STATE)
                .addPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                .addPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .initPermission();
        //butter绑定
        init(savedInstanceState);
        //返回的不是null和空则是为有标题，标题的为返回值
        initEventAndData();
    }

    private void setLoadingStyle() {
        if (progress != null) {
            int color = Color.parseColor("#89CFF0");
            ThreeBounce chasingDots = new ThreeBounce();
            chasingDots.setColor(color);
            progress.setIndeterminateDrawable(chasingDots);
        }
    }

    public abstract int getLayout();

    void onTopRightTvListener() {
    }

    void onTopRightIvListener() {
    }

    void onTopLeftListener() {
        onBackIv();
    }

    // 实例化presenter
    public abstract T initPresenter();

    public abstract void loadMore();

    public abstract void refreData();

    /**
     * 返回按钮，可不实现
     */
    public void onBackIv() {
//        RxActivityTool.finishActivity();
        //跳往扫码页面
        RxActivityTool.finishActivity();
    }

    @Override
    public void onDestroy() {
        if (mPresenter != null) {
            mPresenter.detachView();
        }
        if (mBinder != null) {
            mBinder.unbind();
        }

        SkinManager.getInstance().unregister(this);
        RxBus.getDefault().unregister(this);
        super.onDestroy();
    }

    void setToolBar(String title) {
        title_tv.setText(title);
    }

    public abstract void init(Bundle savedInstanceState);

    public abstract String getTitleStr();

    public abstract void initEventAndData();


    @Override
    public void showLoading() {
        showProDialog();
    }


    @Override
    public void hideLoading() {
        hideProDialog();
    }

    @Override
    public void onBackPressed() {
        if (isLoginOut()) {
            //连续按2次返回键退出
            if (System.currentTimeMillis() - exitTime > 2000) {
                RxToast.showToast("再按一次退出");
                exitTime = System.currentTimeMillis();
            } else {
                RxActivityTool.AppExit(context);
            }
        } else {
            onBackIv();
        }
    }

    public String TAG = getClass().getSimpleName();
}
