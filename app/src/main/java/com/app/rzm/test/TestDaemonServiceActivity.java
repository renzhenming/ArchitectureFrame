package com.app.rzm.test;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.app.rzm.R;
import com.app.rzm.service.GuardService1;
import com.app.rzm.service.GuardService2;
import com.app.rzm.service.JobWakeUpService;

public class TestDaemonServiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_daemon_service);

        startService(new Intent(getApplicationContext(), GuardService1.class));
        startService(new Intent(getApplicationContext(), GuardService2.class));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            //必须大于5.0
            startService(new Intent(this,JobWakeUpService.class));
        }
    }
}
