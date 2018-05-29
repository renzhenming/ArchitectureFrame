package com.app.rzm.test.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.rzm.commonlibrary.utils.LogUtils;

public class TestTouchView extends View {
    public TestTouchView(Context context) {
        super(context);
    }

    public TestTouchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TestTouchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        LogUtils.d("View","dispatchTouchEvent:"+event.getAction());
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtils.d("View","onTouchEvent:"+event.getAction());
        return super.onTouchEvent(event);
    }
}
