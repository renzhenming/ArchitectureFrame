package com.app.rzm.service;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.rzm.commonlibrary.utils.LogUtils;

public class DaemonActivity extends AppCompatActivity {

    private static final java.lang.String TAG = "DaemonActivity";
    private static DaemonActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.context = this;

        Window window = getWindow();
        window.setGravity(Gravity.LEFT & Gravity.TOP);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.x = 0;
        attributes.y = 0;
        attributes.width = 1;
        attributes.height = 1;
        window.setAttributes(attributes);
    }

    /**
     * 监听手机的锁屏和开屏广播，锁屏后调用start开启这个activity
     */
    public static void startDaemon(){
        if (context == null) return;
        Intent intent = new Intent(context,DaemonActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        LogUtils.d(TAG,"startDaemon");
    }

    /**
     * 监听手机的锁屏和开屏广播，开屏后调用stop销毁这个activity
     */
    public static void stopDaemon(){
        if (context != null){
            LogUtils.d(TAG,"stopDaemon");
            context.finish();
        }
    }
}
