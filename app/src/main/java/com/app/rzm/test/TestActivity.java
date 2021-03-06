package com.app.rzm.test;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import com.rzm.commonlibrary.general.global.BaseSkinActivity;
import com.app.rzm.R;
import com.rzm.commonlibrary.general.navigationbar.StatusBarManager;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestActivity extends BaseSkinActivity {

    private static final String TAG = "TestActivity";
    private ImageView mImage;

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {

        mImage = (ImageView) findViewById(R.id.image);
        new StatusBarManager.builder(this)
                .setTintType(StatusBarManager.TintType.PURECOLOR)
                .setStatusBarColor(R.color.colorPrimary)
                .build();


    }

    public void onPermissionGranted(){

        //路径url参数都需要放到jni中，防止反编译被盗取到url
        //（https无法被抓包，http可以）






        /**
         * 在这里修复遇到一个问题，就是第一次启动无法达到修复的效果，只有再次启动才可以，我想大概
         * 是调用的时机不对，于是把这行代码放在了application中，果然修复成功，目前这个问题不确定是不是
         * 机型的问题，因为别人好像在activity中调用修复就没问题
         */
        // fixDex();
    }

    @Override
    protected void initTitle() {


    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_test);
    }

    public void change(View view){
        try {
            //点击从手机中一个apk中获取图片资源并且设置给ImageView显示
            //获取系统的两个参数
            Resources superResources = getResources();
            //创建assetManger(无法直接new因为被hide了，所以用反射)
            AssetManager assetManager = AssetManager.class.newInstance();
            //添加资源目录（addAssetPath也是一样被hide无法直接调用）
            Method method = AssetManager.class.getDeclaredMethod("addAssetPath", String.class);
            method.setAccessible(true);//如果是私有的，添上防止万一某一天他变成了私有的
            method.invoke(assetManager,Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"red.skin");//注意你资源的名字要一致
            Resources resources = new Resources(assetManager,superResources.getDisplayMetrics(),superResources.getConfiguration());
            //用创建好的Resources获取资源(注意着三个参数，第一个是要获取资源的名字，我们设置的是girl，不要忘了，第二个参数代表这个资源在哪个文件夹中，第三个参数表示要获取资源的apk的包名，缺一不可)
            int identifier = resources.getIdentifier("girl", "drawable", "com.example.myapplication");
            if (identifier  != 0){
                Drawable drawable = resources.getDrawable(identifier);
                mImage.setImageDrawable(drawable);
            }

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }



}
