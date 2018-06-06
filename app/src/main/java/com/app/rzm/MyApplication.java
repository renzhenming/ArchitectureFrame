package com.app.rzm;


import android.content.Context;

import com.rzm.commonlibrary.general.global.BaseApplication;
import com.rzm.commonlibrary.general.http.base.HttpUtils;
import com.rzm.commonlibrary.general.http.impl.cache.SPCacheEngine;
import com.rzm.commonlibrary.general.http.impl.engine.okhttp.OkHttpEngine;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;


/**
 * Created by rzm on 2017/7/22.
 */

public class MyApplication extends BaseApplication {

    private static MyApplication context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

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

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .writeTimeout(10000L,TimeUnit.MILLISECONDS)
                .build();
        OkHttpEngine okHttpEngine = OkHttpEngine.initClient(okHttpClient);

        HttpUtils.initHttpEngine(okHttpEngine);
        HttpUtils.initCacheEngine(new SPCacheEngine());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public static MyApplication getContext(){
        return context;
    }
}
