package com.app.rzm.test.source;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;

import com.app.rzm.R;
import com.app.rzm.test.view.TestTouchView;
import com.rzm.commonlibrary.utils.LogUtils;

public class TestViewTouchActivity extends AppCompatActivity {

    private TestTouchView mTouchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_view_touch);
        mTouchView = (TestTouchView) findViewById(R.id.touch);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        LogUtils.d("Activity","dispatchTouchEvent:"+event.getAction());
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtils.d("Activity","onTouchEvent:"+event.getAction());
        return super.onTouchEvent(event);
    }
}
