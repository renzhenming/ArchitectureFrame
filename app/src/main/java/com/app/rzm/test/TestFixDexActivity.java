package com.app.rzm.test;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.alipay.euler.andfix.patch.PatchManager;
import com.app.rzm.R;
import com.rzm.commonlibrary.general.fix.FixDexManager;
import com.rzm.commonlibrary.utils.AppUtils;
import com.rzm.commonlibrary.utils.ToastUtil;

import java.io.File;
import java.io.IOException;

public class TestFixDexActivity extends AppCompatActivity {

    private PatchManager mPatchManger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_fix_dex);
    }

    public void bump(View view) {
        int a = 2/1;
        ToastUtil.showCustomToast(getApplicationContext(),"a="+a);
    }

    public void fixBump(View view) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"fix.apatch");
        if (file.exists()){
            FixDexManager manager = new FixDexManager(this);
            try {
                manager.fixDex(file.getAbsolutePath());
                Toast.makeText(getApplicationContext(),"修复bug成功",Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),"修复bug失败",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    public void AndFix(View view) {
        //初始化阿里热修复
        mPatchManger = new PatchManager(this);
        //获取当前应用版本
        mPatchManger.init(AppUtils.getVersionName(this));
        mPatchManger.loadPatch();
        //获取下载到的patch包
        File patchFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"fix.apatch");
        if (patchFile != null){
            try {
                mPatchManger.addPatch(patchFile.getAbsolutePath());
                Toast.makeText(this,"AndFix修复成功",Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this,"AndFix修复失败",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
