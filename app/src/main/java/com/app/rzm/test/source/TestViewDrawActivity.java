package com.app.rzm.test.source;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.app.rzm.R;

public class TestViewDrawActivity extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println(this.getClass().getSimpleName()+":onCreate");
        setContentView(R.layout.activity_test_view_draw);
        mTextView = (TextView) findViewById(R.id.text);
        int measuredHeight = mTextView.getMeasuredHeight();
        System.out.println("onCreate measuredHeight:"+measuredHeight);
        mTextView.post(new Runnable() {
            @Override
            public void run() {
                int measuredHeight1 = mTextView.getMeasuredHeight();
                System.out.println("post measuredHeight:"+measuredHeight1);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        int measuredHeight = mTextView.getMeasuredHeight();
        System.out.println("onResume measuredHeight:"+measuredHeight);
    }

    public void next(View view) {
        startActivity(new Intent(getApplicationContext(),TestViewDrawActivity.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println(this.getClass().getSimpleName()+":onPause");
    }
}
