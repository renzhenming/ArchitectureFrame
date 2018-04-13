package com.app.rzm.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.app.rzm.R;
import com.rzm.commonlibrary.general.guard.DaemonActivity;
import com.rzm.commonlibrary.general.guard.GuardService1;
import com.rzm.commonlibrary.general.guard.GuardService2;
import com.rzm.commonlibrary.general.guard.JobWakeUpService1;
import com.rzm.commonlibrary.general.guard.JobWakeUpService2;
import com.rzm.commonlibrary.utils.LogUtils;

public class TestDaemonServiceActivity extends AppCompatActivity {

    private static final java.lang.String TAG = "TestDaemonServiceActivity";
    private BootCompleteReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_daemon_service);

        startService(new Intent(getApplicationContext(), GuardService1.class));
        startService(new Intent(getApplicationContext(), GuardService2.class));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            //必须大于5.0

            startService(new Intent(this,JobWakeUpService2.class));

            startService(new Intent(this,JobWakeUpService1.class));
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        receiver = new BootCompleteReceiver();
        registerReceiver(receiver,filter);

    }

    public class BootCompleteReceiver extends BroadcastReceiver {
        @Override public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                LogUtils.e(TAG,"ACTION_SCREEN_OFF");
                DaemonActivity.startDaemon();
            }
            else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                LogUtils.e(TAG,"ACTION_SCREEN_ON");
                DaemonActivity.stopDaemon();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null){
            unregisterReceiver(receiver);
        }
    }
}
