package com.app.rzm.test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.app.rzm.R;
import com.rzm.commonlibrary.general.http.base.HttpUtils;
import com.rzm.commonlibrary.general.http.impl.callback.CallBackImpl;
import com.rzm.commonlibrary.general.web.WebViewActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class TestWebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_web_view);
    }

    public void start(View view) {
        HttpUtils.with(this)
                .cache(true)
                .get()
                .url("http://mobile-appv3-test.sightp.com/intergral/login-dui-ba")
                .addParams("token","")
                .addParams("redirect","")
                .execute(new CallBackImpl<String>() {

                    @Override
                    public void onPreExecute() {
                        super.onPreExecute();
                        Toast.makeText(getApplicationContext(),"加载中。。。。。。。。。。",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(final Exception e) {
                        Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onSuccess(final String result) {
                        try {
                            JSONObject jo = new JSONObject(result);
                            String url = jo.getString("result");
                            Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                            intent.putExtra(WebViewActivity.URL,url);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                });

    }
}
