package com.rzm.commonlibrary.general.guard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import com.rzm.commonlibrary.utils.LogUtils;


/**
 * Created by renzhenming on 2018/4/25.
 *
 * app守护管理类
 */

public class GuardAppManager {

    private GuardAppManager(){}

    private static volatile GuardAppManager mInstance;

    private BootCompleteReceiver mReceiver;

    public static GuardAppManager getInstance(){
        if (mInstance == null){
            synchronized (GuardAppManager.class){
                if (mInstance == null){
                    return new GuardAppManager();
                }
            }
        }
        return mInstance;
    }

    public class BootCompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                LogUtils.i("TAG","ACTION_SCREEN_OFF");
                DaemonActivity.startDaemon();
            }
            else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                LogUtils.i("TAG","ACTION_SCREEN_ON");
                DaemonActivity.stopDaemon();
            }
        }
    }

    /**
     * 开启守护
     * @param context
     */
    public void start(Context context){
        context.startService(new Intent(context, GuardService1.class));
        context.startService(new Intent(context, GuardService2.class));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            //必须大于5.0

            context.startService(new Intent(context,JobWakeUpService2.class));

            context.startService(new Intent(context,JobWakeUpService1.class));
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new BootCompleteReceiver();
        context.registerReceiver(mReceiver,filter);
    }

    /**
     * 关闭守护
     * @param context
     */
    public void stop(Context context){
        if (mReceiver != null){
            context.unregisterReceiver(mReceiver);
        }
    }
}
