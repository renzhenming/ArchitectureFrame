package com.app.rzm.test.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.rzm.commonlibrary.utils.LogUtils;

public class TestTouchViewGroup extends FrameLayout {
    public TestTouchViewGroup(Context context) {
        super(context);
    }

    public TestTouchViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TestTouchViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        LogUtils.d("ViewGroup","dispatchTouchEvent:"+event.getAction());
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        LogUtils.d("ViewGroup","onInterceptTouchEvent:"+event.getAction());
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtils.d("ViewGroup","onTouchEvent:"+event.getAction());
        return false;
    }
}
