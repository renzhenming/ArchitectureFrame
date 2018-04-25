package com.app.rzm.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.app.rzm.R;
import com.rzm.commonlibrary.general.guard.GuardAppManager;

public class TestDaemonServiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_daemon_service);
        GuardAppManager.getInstance().start(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GuardAppManager.getInstance().stop(this);
    }
}
