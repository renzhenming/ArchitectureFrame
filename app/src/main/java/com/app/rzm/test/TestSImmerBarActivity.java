package com.app.rzm.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.app.rzm.R;
import com.gyf.barlibrary.BarHide;
import com.gyf.barlibrary.ImmersionBar;

public class TestSImmerBarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_simmer_bar);

        initBar();
    }

    private void initBar() {
        ImmersionBar.with(this)
                .transparentStatusBar()  //透明状态栏，不写默认透明色
                .transparentNavigationBar()  //透明导航栏，不写默认黑色(设置此方法，fullScreen()方法自动为true)
                .transparentBar()             //透明状态栏和导航栏，不写默认状态栏为透明色，导航栏为黑色（设置此方法，fullScreen()方法自动为true）
                .statusBarDarkFont(true)   //状态栏字体是深色，不写默认为亮色
                .flymeOSStatusBarFontColor(R.color.black)  //修改flyme OS状态栏字体颜色
                .hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)  //隐藏状态栏或导航栏或两者，不写默认不隐藏
                .fullScreen(true)      //有导航栏的情况下，activity全屏显示，也就是activity最下面被导航栏覆盖，不写默认非全屏
                .removeSupportAllView() //移除全部view支持
                .navigationBarWithKitkatEnable(true)  //是否可以修改安卓4.4和emui3.1手机导航栏颜色，默认为true
                .fixMarginAtBottom(true)   //已过时，当xml里使用android:fitsSystemWindows="true"属性时,解决4.4和emui3.1手机底部有时会出现多余空白的问题，默认为false，非必须
                .init();  //必须调用方可沉浸式

    }
}
