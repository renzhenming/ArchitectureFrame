package com.app.rzm.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.app.rzm.R;
import com.app.rzm.utils.EncryptUtils;

public class TestParamsEncryptActivity extends AppCompatActivity {

    private TextView mText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_params_encrypt);
        mText = (TextView) findViewById(R.id.text);

        //参数的加密，应该是在运行时架构HttpUtils
        //对参数字典排序HashMap
        //生成加密链接username=renzhenming&password=123456
        String params = EncryptUtils.encryptForAndroid(this,"username=renzhenming&password=123456");

        //作为参数给到服务器，服务器也生成同样的密文，然后将加密的字符串进行比较
        mText.setText(params);
    }
}




















