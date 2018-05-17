package com.app.rzm.test;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.app.rzm.R;
import com.rzm.commonlibrary.general.http.base.HttpUtils;
import com.rzm.commonlibrary.general.http.impl.callback.CallBackImpl;

import java.io.File;

public class TestHttpUtilsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_http_utils);
    }

    public void get(View view) {
        HttpUtils.with(this)
                .cache(true)
                .get()
                .url("http://is.snssdk.com/2/essay/discovery/v3/")
                .addParams("iid","6152551759")
                .addParams("aid","7")
                .execute(new CallBackImpl<String>() {

                    @Override
                    public void onPreExecute() {
                        super.onPreExecute();
                        Toast.makeText(getApplicationContext(),"加载中",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(final Exception e) {
                        Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onSuccess(final String result) {
                        Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
                    }

                });
    }

    public void post(View view) {
        HttpUtils.with(this)
                .cache(true)
                .post()
                .url("http://is.snssdk.com/2/essay/discovery/v3/")
                .addParams("iid","6152551759")
                .addParams("aid","7")
                .execute(new CallBackImpl<String>() {

                    @Override
                    public void onPreExecute() {
                        super.onPreExecute();
                        Toast.makeText(getApplicationContext(),"加载中",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(final Exception e) {
                        Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onSuccess(final String result) {
                        Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
                    }

                });
    }

    public void upload(View view) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator+"input.mp4";
        String url = "https://api.saiwuquan.com/api/upload";
        HttpUtils.with(this)
                .upload()
                .path(path)
                .url(url)
                .execute(new CallBackImpl<String>() {

                    @Override
                    public void onPreExecute() {
                        super.onPreExecute();
                        Toast.makeText(getApplicationContext(),"开始上传",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String result) {
                        Log.i("tag","uploadComplete");
                        Toast.makeText(getApplicationContext(),"上传完成",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.i("tag","onError:"+e.toString());
                        Toast.makeText(getApplicationContext(),"上传失败。。。"+e.toString(),Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void uploadProgress(long total, long current) {
                        super.uploadProgress(total, current);
                        Log.i("tag","uploadProgress:"+total+"/"+current);
                    }

                });
    }

    public void download(View view) {
        String url = "http://sightpvideo-cdn.sightp.com/2018/04/05/20/23/c1e5bb5b03d34d2ebfe37338dcbfdea9.mp4";
        HttpUtils.with(this).url(url).download()
                .execute(new CallBackImpl<String>() {

                    @Override
                    public void onPreExecute() {
                        super.onPreExecute();
                        Toast.makeText(getApplicationContext(),"开始下载",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void downloadProgress(long total, long current) {
                        super.downloadProgress(total, current);
                    }

                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(getApplicationContext(),"下载完成",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(getApplicationContext(),"下载失败。。。"+e.toString(),Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
