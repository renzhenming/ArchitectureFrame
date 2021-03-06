package com.app.rzm.test;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.app.rzm.R;
import com.app.rzm.utils.EncryptUtils;
import com.rzm.commonlibrary.utils.LogUtils;

public class TestParamsEncryptActivity extends AppCompatActivity {

    private TextView mText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_params_encrypt);
        mText = (TextView) findViewById(R.id.text);

        //拿到签名
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signatures = packageInfo.signatures;
            LogUtils.d("signature:"+signatures[0].toCharsString());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //将网络请求中的http参数拼接成这样的字符串username=renzhenming&password=123456
        //然后将这个参数字符串进行加密
        String params = EncryptUtils.encrypt(this,"username=renzhenming&password=123456");

        //作为参数给到服务器，服务器也生成同样的密文，然后将加密的字符串进行比较
        mText.setText(params);

    }
}




















