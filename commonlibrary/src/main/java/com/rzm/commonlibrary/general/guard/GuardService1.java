package com.rzm.commonlibrary.general.guard;

import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.example.renzhenming.appmarket.test.GuardAidl;
import com.rzm.commonlibrary.utils.LogUtils;

/**
 * create by rzm on 4/25/2018
 * 双进程守护
 */
public class GuardService1 extends Service {
    private final int GuardServiceId2 = 2;
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.i("TAG", "GuardService1 wait for signal");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //提高进程优先级
        startForeground(GuardServiceId2,new Notification());
        //绑定建立链接
        bindService(new Intent(getApplicationContext(),GuardService2.class),connection, Context.BIND_IMPORTANT);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new GuardAidl.Stub(){};
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LogUtils.i("TAG", "connect to guardservice2");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            // 断开链接 ,重新启动，重新绑定
            startService(new Intent(getApplicationContext(), GuardService2.class));
            bindService(new Intent(getApplicationContext(),GuardService2.class),connection, Context.BIND_IMPORTANT);
            LogUtils.i("TAG", "disconnect from guardservice2");
        }
    };

    @Override
    public void onDestroy() {
        LogUtils.i("TAG", "guardservice2 is onDestroy");
        unbindService(connection);
        super.onDestroy();
    }
}
