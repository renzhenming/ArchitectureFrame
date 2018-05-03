package com.app.rzm.test;

import android.content.Intent;
import android.os.Environment;
import android.os.Bundle;
import android.view.View;

import com.rzm.commonlibrary.general.global.BaseSkinActivity;
import com.rzm.commonlibrary.general.skin.SkinManager;
import com.app.rzm.R;

import java.io.File;

public class TestSkinActivity extends BaseSkinActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_skin);

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initTitle() {

    }

    @Override
    public void setContentView() {

    }


    public void skin(View view){
        // 从服务器上下载
        String skinPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator +"red.skin";

        // 换肤
        int result = SkinManager.getInstance().loadSkin(skinPath);
    }

    public void skin1(View view){
        // 恢复默认
         SkinManager.getInstance().restoreDefault();
    }


    public void skin2(View view){
        // 跳转
        startActivity(new Intent(getApplicationContext(),TestSkinActivity.class));
    }

}
