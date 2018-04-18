package com.app.rzm.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.app.rzm.R;
import com.rzm.commonlibrary.general.hook.HookActivityUtil;

/**
 * Created by rzm on 2017/10/21.
 */

public class TestHookActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hook_test);
        HookActivityUtil hookActivityUtil = new HookActivityUtil(this,TestHookRegisteredActivity.class);
        try {
            hookActivityUtil.hookStartActivity();
            hookActivityUtil.hookLaunchActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void open(View view) {
        startActivity(new Intent(getApplicationContext(),TestHookUnRegisteredActivity.class));
    }
}
