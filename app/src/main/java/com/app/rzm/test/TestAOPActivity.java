package com.app.rzm.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.app.rzm.R;
import com.app.rzm.aop.CalculateConsume;
import com.app.rzm.aop.NetworkCheck;
import com.rzm.commonlibrary.general.global.BaseApplication;
import com.rzm.commonlibrary.utils.LogUtils;

public class TestAOPActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_aop);
    }

    @CalculateConsume()
    public void start(View view) {
        int sum = 0;
        for (int i = 0; i < 100000000; i++) {
            sum += i;
        }
        LogUtils.e("结果是："+sum);
    }
    @NetworkCheck()
    public void check(View view) {
        Toast.makeText(BaseApplication.getContext(),"网络正常",Toast.LENGTH_SHORT).show();
    }
}
