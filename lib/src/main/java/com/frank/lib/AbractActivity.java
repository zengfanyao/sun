package com.frank.lib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.frank.lib.utils.ACheckUpdateInfo;
import com.frank.lib.utils.CFRetrofitWrapper;
import com.frank.lib.utils.CheckUpdateInfo;
import com.frank.lib.utils.FRetrofitUtil;
import com.frank.lib.utils.FRetrofitWrapper;
import com.frank.lib.utils.InstallUtils;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public abstract class AbractActivity extends Activity {

    protected ImageView ivSplash;
    protected WebView mWebView;
    protected Context context;


    protected ArrayList<Disposable> disposables = new ArrayList<Disposable>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_base_splash);
        ivSplash = this.findViewById(R.id.act_base_splash);
        mWebView = this.findViewById(R.id.act_base_webview);
        ivSplash.setImageResource(getSplashImage());
        disposables.add(CFRetrofitWrapper.getInstance().checkUpdate(getHost(),getAppID())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ACheckUpdateInfo>() {
            @Override
            public void accept(ACheckUpdateInfo checkUpdateInfo) throws Exception {
                if("0".equals(checkUpdateInfo.status)){
                    switch (checkUpdateInfo.result.vs){
                        case "1":{
                            toMainAct();
                            finish();
                            break;
                        }
                        case "4":{
                            WebActivity.startActivity(context,checkUpdateInfo.result.url);
                            finish();
                            break;
                        }
                        case "5":{
                            InstallUtils.updateApk(context,checkUpdateInfo.result.ud);
                            break;
                        }
                        default:{
                            toMainAct();
                            finish();
                        }
                    }
                }else {
                    toMainAct();
                    finish();
                }


            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                throwable.printStackTrace();
            }
        }));

    }

    protected abstract void toMainAct();

    protected abstract String getHost();

    protected abstract int getSplashImage();

    protected abstract String getAppID();

    private void initWebView() {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_MR1) {
            webSettings.setLoadWithOverviewMode(true);
        }
        //启用地理定位
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            webSettings.setGeolocationEnabled(true);
        }
        String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        //设置定位的数据库路径
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            webSettings.setGeolocationDatabasePath(dir);
        }
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }

        // 开启 DOM storage API 功能
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_MR1) {
            webSettings.setDomStorageEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            webSettings.setAllowFileAccess(true);
        }
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
//        mWebView.setScrollChangeListener(new DetailWebView.ScrollChangeListener() {
//            @Override
//            public void onScrollChange(int left, int top, int oldLeft, int oldTop) {
//                if (top > 750) {
//                    setLeftStatus(R.drawable.selector_common_tv_left_image);
//                } else {
//                    setLeftStatus(R.drawable.selector_act_detail_left_back);
//                }
//            }
//        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(url));
                    startActivity(intent);
                    return true;
                } else if (url.startsWith("sms:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }

//                HashMap<String, String> map = new HashMap<String, String>();
//                map.put("Referer", view.getUrl());
//
//                view.loadUrl(url, map);
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
            }
        });
    }

    @Override
    protected void onDestroy() {

        if(null != disposables){
            for (Disposable disposable : disposables){
                if (disposable != null && !disposable.isDisposed()) {
                    disposable.dispose();
                    disposable = null;
                }
            }
            disposables.clear();
        }
        super.onDestroy();
    }
}
