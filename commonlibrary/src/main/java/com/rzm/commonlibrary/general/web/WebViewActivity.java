package com.rzm.commonlibrary.general.web;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsoluteLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.rzm.commonlibrary.R;
import com.rzm.commonlibrary.utils.LogUtils;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class WebViewActivity extends AppCompatActivity{

    public static final String URL = "url";

    private static final String TAG = "WebViewActivity";

    public static final String SCROLL_PERCENT = "scroll_percent";

    private Handler mHandler = new Handler();

    //加载类型
    private int type = 1;

    //进度条高度
    private static final int mProgressHeight = 5;

    //5.0以上打开相册后回调的code
    private static final int FILE_CHOOSER_RESULT_CODE_FOR_ANDROID_5 = 11111;

    private static final int FILE_CHOOSER_RESULT_CODE_BELOW_ANDROID_5 = 22222;

    //打开系统相册选择图片相关
    private ValueCallback<Uri[]> mUploadMessageForAndroid5;

    private ValueCallback<Uri> mUploadMessageForAndroidBellow5;

    private WebView mWebView;

    //传入的网页链接
    private String mWebUrl = "http://www.baidu.com";

    private ViewGroup mViewContain;

    private ProgressBar mProgressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        initIntentData();
        initView();
        initWebView();
        loadByType();
    }

    private void loadByType() {
        switch (type) {
            case 1:
                loadDefault(mWebUrl);
                break;
        }

    }

    private void loadDefault(String url) {
        LogUtils.i(TAG, "url = " + url);

        if (TextUtils.isEmpty(url)) {
            return;
        }
        mWebView.loadUrl(url);
    }

    private void initView() {
        mViewContain = (ViewGroup) findViewById(R.id.web_contain);
    }

    private void initIntentData() {
        Intent intent = getIntent();
        if (intent == null) return;
        mWebUrl = intent.getStringExtra(URL);
    }

    private void initWebView() {
        try {
            initWebEngine();
            initProgress();
            initSettings();
            initWebListener();
        } catch (Exception e) {
            //如果内置WebView加载出问题就使用系统浏览器
            Intent intentBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse(mWebUrl));
            startActivity(intentBrowser);
            this.finish();
            return;
        }
    }

    /**
     * 初始化监听器
     */
    private void initWebListener() {
        setWebChromeClient();
        setWebDownloadListener();
        setWebViewClient();
    }

    protected void setWebViewClient() {
        mWebView.setWebViewClient(new MyWebViewClient());
    }

    private void setWebDownloadListener() {
        mWebView.setDownloadListener(new MyWebViewDownLoadListener());
    }

    private void setWebChromeClient() {
        mWebView.setWebChromeClient(new MyWebChromeClient());
    }

    /**
     * 初始化WebSetting
     *
     * 有时候为了便于WEB端统计分析，需要将APP的 user-agent 作特征标记，自定义设置传递给H5
     * userAgent示例：Mozilla/5.0 (Linux; Android 6.0; MX6 Build/MRA58K; wv) AppleWebKit/537.36+空格+
     * (KHTML, like Gecko)Version/4.0 Chrome/44.0.2403.144 Mobile Safari/537.36
     */
    private void initSettings() {
        LogUtils.e(TAG, "initSettings");
        if (mWebView == null) return;
        WebSettings webSetting = mWebView.getSettings();

        String userAgent = webSetting.getUserAgentString();
        LogUtils.e(TAG, "userAgent："+userAgent);
        webSetting.setUserAgentString(userAgent + " fkls_student");

        //设置是否支持Javascript
        webSetting.setJavaScriptEnabled(true);

        //支持通过JS打开新窗口
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);

        //设置支持H5的本地存储DomStorage
        webSetting.setDomStorageEnabled(true);

        //设置存储模式
        webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);

        //设置支持本地存储
        webSetting.setDatabaseEnabled(true);
        //取得缓存路径
        String databasePath = getDir("database", Context.MODE_PRIVATE).getPath();
        //缓存设置路径
        webSetting.setDatabasePath(databasePath);

        //开启webview的LBS功能，允许地理位置可用(配置权限ACCESS_COARSE_LOCATION，ACCESS_FINE_LOCATION)
        webSetting.setGeolocationEnabled(true);
        webSetting.setGeolocationDatabasePath(databasePath);

        // <a href="http://www.baidu.com" target="_blank">百度一下</a> 一个超链接
        // setSupportMultipleWindows默认的是false，也就是说WebView默人不支持新窗口，但是这个不是说WebView不能打
        // 开多个页面了，只是你点击页面上的连接，当它的target属性是_blank时。它会在当前你所看到的页面继续加载那个连接。
        // 而不是重新打开一个窗口。当你设置为true时，就代表你想要你的WebView支持多窗口，但是一旦设置为true，
        // 必须要重写WebChromeClient的onCreateWindow方法,这个方法挺危险的，如果设置为true不再onCreateWindow中处理
        // 可能会导致有些页面打不开
        // webSetting.setSupportMultipleWindows(true);


        //禁止或允许WebView从网络上加载图片
        //如果设置是从禁止到允许的转变的话，图片数据并不会在设置改变后立刻去获取，而是在WebView调用reload()的时候才会生效
        //webSetting.setBlockNetworkImage(boolean flag);

        //设置是否支持缩放。
        //setSupportZoom(boolean support);

        //显示或不显示缩放按钮（wap网页不支持）
        //setBuiltInZoomControls(boolean enabled);

        //启用或禁用WebView访问文件数据
        webSetting.setAllowFileAccess(true);

        //支持自动加载图片
        webSetting.setLoadsImagesAutomatically(true);

        //启用或禁用应用缓存。
        webSetting.setAppCacheEnabled(true);
        //设置应用缓存路径，这个路径必须是可以让app写入文件的。该方法应该只被调用一次，重复调用会被无视~
        webSetting.setAppCachePath(databasePath);

        //设置标准的字体族，默认“sans-serif”
        //webSetting.setStandardFontFamily("sans-serif");

        //设置CursiveFont字体族，默认“cursive”
        //webSetting.setFantasyFontFamily("cursive");

        //设置混合字体族，默认“monospace”
        //webSetting.setFixedFontFamily("monospace");

        //设置梵文字体族，默认“sans-serif”
        //webSetting.setSansSerifFontFamily("sans-serif");

        //设置衬线字体族，默认“sans-serif”
        //webSetting.setSerifFontFamily("sans-serif");

        //设置草书字体族，默认“cursive”
        webSetting.setCursiveFontFamily("cursive");



        //设置默认填充字体大小，默认16，取值区间为[1-72]，超过范围，使用其上限值。
        webSetting.setDefaultFixedFontSize(16);

        //设置默认字体大小，默认16，取值区间[1-72]，超过范围，使用其上限值
        webSetting.setDefaultFontSize(16);

        //设置最小字体，默认8. 取值区间[1-72]，超过范围，使用其上限值。
        webSetting.setMinimumFontSize(8);

        //设置最小逻辑字体，默认8. 取值区间[1-72]，超过范围，使用其上限值。
        webSetting.setMinimumLogicalFontSize(8);

        //设置适应屏幕 根据H5界面的viewport字体大小设定显示字体大小
        //第一个方法设置webview推荐使用的窗口,第二个方法是设置webview加载的页面的模式,可以加载更多格式页面
        webSetting.setUseWideViewPort(true);
        webSetting.setLoadWithOverviewMode(true);
    }

    /**
     * 添加WebView
     */
    private void initWebEngine() {
        mWebView = new WebView(this);
        //开启硬件加速
        mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.requestFocus();
        mWebView.requestFocusFromTouch();
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mViewContain.addView(mWebView, lp);
    }

    /**
     * 初始化加载进度条
     */
    private void initProgress() {
        if (mWebView == null)return;
        mProgressbar = new ProgressBar(this, null,
                android.R.attr.progressBarStyleHorizontal);
        mProgressbar.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.MATCH_PARENT,
                dp2px(this,mProgressHeight), 0, 0));

        Drawable drawable = getResources().getDrawable(
                R.drawable.progress_bar_states);
        mProgressbar.setProgressDrawable(drawable);
        mWebView.addView(mProgressbar);
    }

    /**
     * WebChromeClient
     */
    class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                mProgressbar.setVisibility(GONE);
            } else {
                if (mProgressbar.getVisibility() == GONE)
                    mProgressbar.setVisibility(VISIBLE);
                mProgressbar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }

        /**
         * h5请求地理位置权限时会回调到这里
         * @param origin
         * @param callback
         */
        @Override
        public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
            super.onGeolocationPermissionsShowPrompt(origin, callback);
            LogUtils.e(TAG, "onGeolocationPermissionsShowPrompt");

            //是否记住授权结果（true会不再提醒）
            final boolean remember = false;

            AlertDialog.Builder builder = new AlertDialog.Builder(WebViewActivity.this);
            builder.setTitle("位置信息");
            builder.setMessage(origin + "允许获取您的地理位置信息吗？").setCancelable(true).setPositiveButton("允许",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int id) {
                            callback.invoke(origin, true, remember);
                        }
                    })
                    .setNegativeButton("不允许",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    callback.invoke(origin, false, remember);
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }

        @Override
        public void onGeolocationPermissionsHidePrompt() {
            super.onGeolocationPermissionsHidePrompt();
            LogUtils.e(TAG, "onGeolocationPermissionsHidePrompt");
        }

        /**
         * 支持多窗口,重写WebChromeClient的onCreateWindow方法
         * Request the host application to create a new window. If the host
         * application chooses to honor this request, it should return true from
         * this method, create a new WebView to host the window, insert it into the
         * View system and send the supplied resultMsg message to its target with
         * the new WebView as an argument. If the host application chooses not to
         * honor the request, it should return false from this method. The default
         * implementation of this method does nothing and hence returns false.
         */
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            /*LogUtils.e(TAG, "onCreateWindow");
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(view);    //此webview可以是一般新创建的
            resultMsg.sendToTarget();*/
            return false;
        }

        /**
         * 获取到网页标题
         * @param view
         * @param title
         */
        @Override
        public void onReceivedTitle(WebView view, String title) {

        }

        // <3.0 调用这个方法
        @SuppressWarnings("unused")
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooserExe(uploadMsg);
        }

        // 3.0 + 调用这个方法
        @SuppressWarnings("unused")
        public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                    String acceptType) {
            openFileChooserExe(uploadMsg);
        }

        // Android > 4.1.1 调用这个方法
        @SuppressWarnings("unused")
        public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                    String acceptType, String capture) {
            openFileChooserExe(uploadMsg);
        }

        // For Android > 5.0
        public boolean onShowFileChooser (WebView webView, ValueCallback<Uri[]> uploadMsg, WebChromeClient.FileChooserParams fileChooserParams) {
            openFileChooserImplForAndroid5(uploadMsg);
            return true;
        }
    }

    public void openFileChooserExe(ValueCallback<Uri> valueCallback) {
        mUploadMessageForAndroidBellow5 = valueCallback;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "file Browser"),
                FILE_CHOOSER_RESULT_CODE_BELOW_ANDROID_5);
    }

    private void openFileChooserImplForAndroid5(ValueCallback<Uri[]> valueCallback) {
        mUploadMessageForAndroid5 = valueCallback;

        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");

        startActivityForResult(chooserIntent, FILE_CHOOSER_RESULT_CODE_FOR_ANDROID_5);
    }

    /**
     * 下载监听器
     */
    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
            LogUtils.e(TAG, "onDownloadStart");
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (mWebView.canGoBack()) {
                        mWebView.goBack();
                    } else {
                        mWebView.loadUrl("");
                        finish();
                    }
                    return true;
                case KeyEvent.KEYCODE_MENU:
                    final AlertDialog.Builder builder = new AlertDialog.Builder(WebViewActivity.this);
                    builder.setTitle("提示信息");
                    builder.setMessage("确定在系统浏览器中打开此链接吗？").setCancelable(true).setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    lanuchBrowser(mWebUrl);

                                }
                            })
                            .setNegativeButton("取消",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int id) {
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                    return true;

            }

        }
        return super.onKeyDown(keyCode, event);
    }

    class MyWebViewClient extends WebViewClient{
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            LogUtils.e(TAG, "onPageStarted");
        }

        // 修复不能拨打电话问题
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            LogUtils.e(TAG, "shouldOverrideUrlLoading："+url);
            if (url != null && url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
                if (ActivityCompat.checkSelfPermission(WebViewActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),"请允许通话权限",Toast.LENGTH_SHORT).show();
                    return true;
                }
                startActivity(intent);
                return true;
            }else if(url != null && url.contains("sightplus://duiba/login")){
                //do something
                return true;
            } else {
                return super.shouldOverrideUrlLoading(view, url);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            LogUtils.e(TAG, "onPageFinished："+url);
            //如果加载出现了问题，后边内容不再执行
            final float percent = WebViewActivity.this.getIntent()
                    .getFloatExtra(SCROLL_PERCENT, 0);

            //如果页面需要滑动到指定位置，则进行滑动
            if (percent != 0) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int y = (int) (mWebView.getContentHeight()
                                * mWebView.getScale() * percent) + 20;
                        mWebView.scrollTo(0, y);
                    }
                }, 100);
            }


        }


        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            LogUtils.e(TAG, "onReceivedError："+error);
            Toast.makeText(getApplicationContext(),"页面加载失败",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            LogUtils.e(TAG, "onReceivedError："+description);
            Toast.makeText(getApplicationContext(),"页面加载失败",Toast.LENGTH_SHORT).show();
        }
    }

    protected void lanuchBrowser(String urlText) {
        Uri uri = Uri.parse(urlText);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public static int dp2px(Context context, int dp){
        return (int)(dp * context.getResources().getDisplayMetrics().density + 0.5);
    }

    /**
     * 处理查看图库的回调函数以及分享时微博的回调
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent intent) {
        if (requestCode == FILE_CHOOSER_RESULT_CODE_BELOW_ANDROID_5) {
            if (null == mUploadMessageForAndroidBellow5)
                return;
            Uri result = intent == null || resultCode != RESULT_OK ? null: intent.getData();
            mUploadMessageForAndroidBellow5.onReceiveValue(result);
            mUploadMessageForAndroidBellow5 = null;

        } else if (requestCode == FILE_CHOOSER_RESULT_CODE_FOR_ANDROID_5){
            if (null == mUploadMessageForAndroid5)
                return;
            Uri result = (intent == null || resultCode != RESULT_OK) ? null: intent.getData();
            if (result != null) {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{result});
            } else {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
            }
            mUploadMessageForAndroid5 = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null){
            mWebView.stopLoading();
            mWebView.removeAllViews();
            mWebView.destroy();
        }
    }
}
