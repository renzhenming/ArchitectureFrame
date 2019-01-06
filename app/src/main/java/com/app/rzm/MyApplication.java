package com.app.rzm;


import android.content.Context;

import com.rzm.commonlibrary.general.global.BaseApplication;
import com.rzm.commonlibrary.general.http.base.HttpUtils;
import com.rzm.commonlibrary.general.http.impl.cache.SPCacheEngine;
import com.rzm.commonlibrary.general.http.impl.engine.okhttp.OkHttpEngine;
import com.rzm.commonlibrary.general.imageloader.cache.MemoryDiskCache;
import com.rzm.commonlibrary.general.imageloader.config.ImageLoaderConfig;
import com.rzm.commonlibrary.general.imageloader.loader.SimpleImageLoader;
import com.rzm.commonlibrary.general.imageloader.policy.SerialPolicy;

import java.util.concurrent.TimeUnit;


/**
 * Created by rzm on 2017/7/22.
 */

public class MyApplication extends BaseApplication {

    private static MyApplication context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        initImageLoader();
        initNet();


        /*.initCacheEngine(new SPCacheEngine(this)*/
        //HttpCacheUtils.initHttpEngine(new SPCacheEngine(this));

        /*PluginHelper.getInstance().applicationOnCreate(getBaseContext()); //must behind super.onCreate()
        try {
            PluginManager.getInstance().installPackage("", 0);
            PluginManager.getInstance().deletePackage("",0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }*/
    }

    private void initNet() {

//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
//                .readTimeout(10000L, TimeUnit.MILLISECONDS)
//                .writeTimeout(10000L,TimeUnit.MILLISECONDS)
//                .build();
//        OkHttpEngine okHttpEngine = OkHttpEngine.initClient(okHttpClient);
//
//        HttpUtils.initHttpEngine(okHttpEngine);
//        HttpUtils.initCacheEngine(new SPCacheEngine());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public static MyApplication getContext(){
        return context;
    }




    private void initImageLoader() {
        //配置
        ImageLoaderConfig.Builder build = new ImageLoaderConfig.Builder();
        build.setDispatcherCount(3) //线程数量
                .setLoadPolicy(new SerialPolicy()) //加载策略
                .setCachePolicy(new MemoryDiskCache(this)) //缓存策略
                .setLoadingImage(R.drawable.loading)
                .setErrorImage(R.drawable.loading_failure);

        ImageLoaderConfig config = build.build();
        //初始化
        SimpleImageLoader.init(config);
    }
}
