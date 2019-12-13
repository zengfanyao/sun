package com.frank.lib;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.frank.lib.utils.CheckUpdateInfo;
import com.frank.lib.utils.FRetrofitWrapper;
import com.frank.lib.utils.InstallUtils;
import com.just.library.AgentWeb;
import com.just.library.AgentWebSettings;
import com.just.library.DownLoadResultListener;
import com.just.library.IWebLayout;
import com.just.library.PermissionInterceptor;
import com.just.library.WebDefaultSettingsManager;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class WebActivity extends Activity {
    private static final String STRING_KEY_URL = "string_key_url";


    private WebView webView;
    private AgentWeb mAgentWeb;
    private WebView mWebView;
    private String url;
    private boolean isExit;
    private Context context;

    public static void startActivity(@NonNull Context context, @NonNull String url) {
        Intent intent = new Intent();
        intent.setClass(context, WebActivity.class);
        intent.putExtra(STRING_KEY_URL, url);
        context.startActivity(intent);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_base_web_view);
        context = this;
        webView = this.findViewById(R.id.webView);
        url=getIntent().getStringExtra(STRING_KEY_URL);
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(webView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
                .useDefaultIndicator()
                .setIndicatorColorWithHeight(R.color.white, 1)
                .setWebChromeClient(getWebChromeClient())
                .setWebViewClient(getWebViewClient())
                .setWebView(getWebView())
                .setPermissionInterceptor(getPermissionInterceptor())
                .setWebLayout(getWebLayout())
                .addDownLoadResultListener(mDownLoadResultListener)
                .setAgentWebSettings(getAgentWebSettings())
                .setSecutityType(AgentWeb.SecurityType.strict)
                .openParallelDownload()//打开并行下载 , 默认串行下载。
                .setNotifyIcon(R.mipmap.download) //下载通知图标。
                .createAgentWeb()//创建AgentWeb。
                .ready()//设置 WebSettings。
                .go(url);
        mWebView = mAgentWeb.getWebCreator().get();

        mWebView.setDownloadListener(new MyWebViewDownLoadListener());
        this.findViewById(R.id.rbt_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.clearCache(true);
                mWebView.loadUrl(url);
                mWebView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.clearHistory();
                    }
                }, 1000);
            }
        });

        this.findViewById(R.id.rbt_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.goBack();
            }
        });

        this.findViewById(R.id.rbt_go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.goForward();
            }
        });

        this.findViewById(R.id.rbt_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.reload();
            }
        });

        this.findViewById(R.id.rbt_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder normalDialog = new AlertDialog.Builder(WebActivity.this);
                normalDialog.setTitle("清除缓存");
                normalDialog.setMessage("是否清除");
                normalDialog.setPositiveButton("清除"
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAgentWeb.getWebCreator().get().clearHistory();
                                mAgentWeb.getWebCreator().get().clearCache(true);
                                dialog.dismiss();
                                Toast.makeText(mWebView.getContext(),"清除成功", Toast.LENGTH_LONG).show();
                            }
                        });
                normalDialog.setNegativeButton("取消"
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                normalDialog.create().show();
            }
        });
    }


    //    @OnClick({R.id.rbt_home, R.id.rbt_back, R.id.rbt_go, R.id.rbt_refresh, R.id.rbt_clear})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.rbt_home) {//首页
            mWebView.clearCache(true);
            mWebView.loadUrl(url);
            mWebView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mWebView.clearHistory();
                }
            }, 1000);
        } else if (id == R.id.rbt_back) {//后退
            mWebView.goBack();
        } else if (id == R.id.rbt_go) {//前进
            mWebView.goForward();
        } else if (id == R.id.rbt_refresh) {//刷新
            mWebView.reload();
        } else if (id == R.id.rbt_clear) {//清除缓存
            AlertDialog.Builder normalDialog = new AlertDialog.Builder(WebActivity.this);
            normalDialog.setTitle("清除缓存");
            normalDialog.setMessage("是否清除");
            normalDialog.setPositiveButton("清除"
                    , new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAgentWeb.getWebCreator().get().clearHistory();
                            mAgentWeb.getWebCreator().get().clearCache(true);
                            dialog.dismiss();
                            Toast.makeText(mWebView.getContext(), "清除成功", Toast.LENGTH_LONG).show();
                        }
                    });
            normalDialog.setNegativeButton("取消"
                    , new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            normalDialog.create().show();
        }
    }


    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent,
                                    String contentDisposition, String mimetype, long contentLength) {
            if(!TextUtils.isEmpty(url)&&url.contains("apk")){
                InstallUtils.updateApk(context,url);
            }else {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }


        }
    }

    @Nullable
    WebChromeClient getWebChromeClient() {
        return null;
    }

    protected DownLoadResultListener mDownLoadResultListener = new DownLoadResultListener() {
        @Override
        public void success(String path) {
            //do you work

        }

        @Override
        public void error(String path, String resUrl, String cause, Throwable e) {
            //do you work
        }
    };

    protected
    @Nullable
    WebViewClient getWebViewClient() {
        return mWebViewClient;
    }

    protected
    @Nullable
    WebView getWebView() {
        return null;
    }

    protected
    @Nullable
    IWebLayout getWebLayout() {
        return null;
    }

    protected PermissionInterceptor getPermissionInterceptor() {
        return null;
    }

    public
    @Nullable
    AgentWebSettings getAgentWebSettings() {
        return WebDefaultSettingsManager.getInstance();
    }


    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //do you  work
            if (url.startsWith("http") || url.startsWith("https")) {
                super.onPageStarted(view, url, favicon);
            } else {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } catch (Exception e) {
//                    Toast.makeText(WebActivity.this, "您没有安装相应的程序", Toast.LENGTH_LONG).show();
                    //当手机上没有安装对应应用时报出异常
                }
            }

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url.startsWith("http") || url.startsWith("https")) {
                view.loadUrl(url);
                return false;
            } else {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } catch (Exception e) {
//                    Toast.makeText(WebActivity.this, "您没有安装相应的程序", Toast.LENGTH_LONG).show();
                    //当手机上没有安装对应应用时报出异常
                }
                return true;

            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            Log.d("SslError:", error.toString());
            handler.proceed();
            super.onReceivedSslError(view, handler, error);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            if (!mAgentWeb.getWebCreator().get().canGoBack()) {
                return false;
            }
        }
        if (mAgentWeb != null && mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    private void exit() {
        if (!isExit) {
            finish();
            isExit = true;
//            Toast.makeText(this, "再点击一次退出程序", Toast.LENGTH_LONG).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);

        } else {
            Intent it = new Intent(Intent.ACTION_MAIN);
            it.addCategory(Intent.CATEGORY_HOME);
            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(it);
        }
    }
}
