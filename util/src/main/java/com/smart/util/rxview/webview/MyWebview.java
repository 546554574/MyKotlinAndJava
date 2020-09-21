package com.zydl.util.rxview.webview;

import android.content.Context;
import android.graphics.Color;
import android.net.http.SslError;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.zydl.util.rxview.progressing.style.ThreeBounce;

public class MyWebview extends WebView {
    private NumberProgressBar progressbar;  //进度条

    private int progressHeight = 10;  //进度条的高度，默认10px

    private Context mContext;
    private LinearLayout linearLayout;

    public MyWebview(Context context) {
        super(context);
        initView(context);
    }

    public MyWebview(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;

        //chrome调试时候打开
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
//            setWebContentsDebuggingEnabled(true);
//        }
        //开启js脚本支持
        getSettings().setJavaScriptEnabled(true);
        getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//        getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //设置缓存

        //创建进度条
//        progressbar = new ProgressBar(context, null,
//                android.R.attr.progressBarStyleHorizontal);
//        //设置加载进度条的高度
//        progressbar.setLayoutParams(new AbsoluteLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, progressHeight, 0, 0));
//
//        Drawable drawable = context.getResources().getDrawable(R.drawable.progress_bar_states);
//        progressbar.setProgressDrawable(drawable);

        progressbar = new NumberProgressBar(context, null);
        linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.setBackgroundColor(Color.parseColor("#33000000"));
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setLayoutParams(linearLayoutParams);
        ProgressBar loading = new ProgressBar(context, null);
        int color = Color.parseColor("#89CFF0");
        ThreeBounce chasingDots = new ThreeBounce();
        chasingDots.setColor(color);
        loading.setIndeterminateDrawable(chasingDots);
        LinearLayout.LayoutParams loadingParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingParams.gravity = Gravity.CENTER;
        linearLayout.addView(loading, loadingParams);
        //添加等待框到webview
        addView(linearLayout);
        //添加进度到WebView
        addView(progressbar);

        //适配手机大小
        getSettings().setUseWideViewPort(true);
        getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        getSettings().setLoadWithOverviewMode(true);
        getSettings().setSupportZoom(true);
        getSettings().setBuiltInZoomControls(true);
        getSettings().setDisplayZoomControls(false);
        getSettings().setBlockNetworkImage(false);
        getSettings().setDatabaseEnabled(true);
        getSettings().setAppCacheEnabled(true);
        getSettings().setDomStorageEnabled(true);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            wetSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        }
        getSettings().setUserAgentString(getSettings().getUserAgentString());

        setWebChromeClient(new WVChromeClient());
        setWebViewClient(new WVClient());
    }


    //进度显示
    private class WVChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                progressbar.setVisibility(GONE);
                linearLayout.setVisibility(GONE);
            } else {
                if (progressbar.getVisibility() == GONE) {
                    progressbar.setVisibility(VISIBLE);
                }
                if (linearLayout.getVisibility() == GONE) {
                    linearLayout.setVisibility(VISIBLE);
                }
                progressbar.setProgress(newProgress);
            }

            if (mListener != null) {
                mListener.onProgressChange(view, newProgress);
            }

            super.onProgressChanged(view, newProgress);
        }

    }

    private class WVClient extends WebViewClient {

        @Override
        public void onReceivedError(WebView webView, int i, String s, String s1) {
            super.onReceivedError(webView, i, s, s1);
//            webView.loadUrl(s1);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            //在当前Activity打开
            if (url.contains("http") || url.contains("https")) {
                // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
            return true;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            //https忽略证书问题
            handler.proceed();
            super.onReceivedSslError(view, handler, error);
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            progressbar.setVisibility(GONE);
            linearLayout.setVisibility(GONE);
            if (mListener != null) {
                mListener.onPageFinish(view);
            }

            super.onPageFinished(view, url);

        }

    }

    private onWebViewListener mListener;

    public void setOnWebViewListener(onWebViewListener listener) {
        this.mListener = listener;
    }

    //进度回调接口
    public interface onWebViewListener {
        void onProgressChange(WebView view, int newProgress);

        void onPageFinish(WebView view);
    }

}