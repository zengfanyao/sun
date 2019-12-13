package com.frank.lib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.util.HashMap;


/**
 * 协议
 * Created by Frank on 2019/07/26.
 */
public class WebYXActivity extends Activity {


    private Context mContext;

    private WebView mWebView;
    private TextView tvTitle;
    private WebServerChromeClient webServerChromeClient;

    public static void startActivity(Context context, String title, String path) {
//        "file:///android_asset/userAgreement.html"
        Intent intent = new Intent(context, WebYXActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("path", path);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_yx);
        initView();
    }

    protected void initView() {

        mContext = this;
        mWebView = (WebView) findViewById(R.id.act_yx_webView);
        tvTitle = (TextView) findViewById(R.id.act_yx_tv_title);
        tvTitle.setText(getIntent().getStringExtra("title"));
        registerForContextMenu(mWebView);// 注册上下文菜单

        WebSettings webSettings = mWebView.getSettings();
        // 启用javascript
        webSettings.setJavaScriptEnabled(true);
        //启用地理定位
        webSettings.setGeolocationEnabled(true);
        String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        //设置定位的数据库路径
        webSettings.setGeolocationDatabasePath(dir);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }

        //设置 缓存模式
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        // 开启 DOM storage API 功能
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
        if (webServerChromeClient == null) {
            webServerChromeClient = new WebServerChromeClient(this);
        }
        mWebView.setWebChromeClient(webServerChromeClient);

        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mWebView.setWebViewClient(new MyWebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("Referer", view.getUrl());
                view.loadUrl(url, map);
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
            }


        });

        mWebView.setOnKeyListener(new View.OnKeyListener() { // webview can
            // go back
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (mWebView.canGoBack()) {
                            mWebView.goBack();
                        } else {
                            finish();
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        mWebView.loadUrl(getIntent().getStringExtra("path"));
        mWebView.requestFocus();
    }



    // js通信接口
    public class JavascriptInterface {

        private Context context;

        public JavascriptInterface(Context context) {
            this.context = context;
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        super.onCreateContextMenu(contextMenu, view, contextMenuInfo);

    }

    //MyWebViewClient 监听
    private class MyWebViewClient extends WebViewClient {

        @Override
        public WebResourceResponse shouldInterceptRequest(final WebView view, String url) {
            Log.i("shouldInterceptRequest",url);
            if (url.endsWith("jquery.min.js") || url.endsWith("bootstrap.js") || url.endsWith("respond.min.js") || url.endsWith("tools.js") || url.endsWith("Validform_v5.3.2_min.js")) {
                return getWebResourceResponseFromAsset("js", url);
            } else if (url.endsWith("bootstrap.css") || url.endsWith("style.css") || url.endsWith("tools.css")) {
                return getWebResourceResponseFromAsset("css", url);
            } else if (url.endsWith("button-color_03.png") || url.endsWith("login_icon01.png") || url.endsWith("login_icon02.png") || url.endsWith("login_img.png")) {
                return getWebResourceResponseFromAsset("img", url);
            }
            if(url.contains("1drj.com")){
                return new WebResourceResponse(null,null,null);
            }
            return super.shouldInterceptRequest(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }


    }

    /**
     * 从本地assets文件夹获取资源
     *
     * @param resType：资源类型。css、js、img等等
     * @param url
     */
    private WebResourceResponse getWebResourceResponseFromAsset(String resType, String url) {
        WebResourceResponse response = null;
        try {
            AssetManager am = getResources().getAssets();
            //资源路径，如bootstrap.css、jquery.min.js
            //http://saas.jufuns.cn/crm/html5/js/jquery.min.js
            String resPath = url.substring(url.lastIndexOf("/") + 1, url.length());
            if ("css".equals(resType)) {
                resPath = "css/" + resPath;
                response = new WebResourceResponse("text/css",
                        "utf-8", am.open(resPath));
            } else if ("js".equals(resType)) {
                resPath = "js/" + resPath;
                response = new WebResourceResponse("text/javascript",
                        "utf-8", am.open(resPath));
            } else if ("img".equals(resType)) {
                resPath = "img/" + resPath;
                response = new WebResourceResponse("image/png",
                        "utf-8", am.open(resPath));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }


    private class WebServerChromeClient extends WebChromeClient {
        private static final int CHOOSE_REQUEST_CODE = 0x9001;
        private ValueCallback<Uri> uploadFile;//定义接受返回值
        private ValueCallback<Uri[]> uploadFiles;
        private Activity mActivity;

        public WebServerChromeClient(Activity mActivity) {
            this.mActivity = mActivity;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {

        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message,
                                  String defaultValue, JsPromptResult result) {

            return true;
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }


        @Override
        public void onPermissionRequest(PermissionRequest request) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                request.grant(request.getResources());
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        loadUrl();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
