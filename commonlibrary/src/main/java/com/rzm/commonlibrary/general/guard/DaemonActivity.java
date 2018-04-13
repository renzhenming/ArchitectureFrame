package com.rzm.commonlibrary.general.guard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.rzm.commonlibrary.general.BaseApplication;
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
     * 调用此方法时，activity未启动，不能使用this.context,只能用application的
     */
    public static void startDaemon(){
        Intent intent = new Intent(BaseApplication.getContext(),DaemonActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        BaseApplication.getContext().startActivity(intent);
        LogUtils.e(TAG,"startDaemon");
    }

    /**
     * 监听手机的锁屏和开屏广播，开屏后调用stop销毁这个activity
     * 调用此方法时，可以使用activity的context
     */
    public static void stopDaemon(){
        if (context != null){
            LogUtils.e(TAG,"stopDaemon");
            context.finish();
        }
    }
}
