package com.app.rzm.test.source;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.app.rzm.R;
import com.bumptech.glide.Glide;

public class TestGlideSourceActivity extends AppCompatActivity {

    private ImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_glide_source);
        mImage = (ImageView) findViewById(R.id.image);
        GlideOnThread();
    }

    private void GlideOnThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.with(getApplicationContext()).
                        load("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3623506971,629280852&fm=27&gp=0.jpg")
                        .into(mImage);
            }
        }).start();
    }
}
