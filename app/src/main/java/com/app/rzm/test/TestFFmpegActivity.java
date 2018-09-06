package com.app.rzm.test;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.View;

import com.app.rzm.R;
import com.app.rzm.utils.FFmpegUtils;
import com.app.rzm.utils.VideoView;
import com.rzm.commonlibrary.utils.LogUtils;

import java.io.File;

//import VideoHandle.EpEditor;
//import VideoHandle.EpVideo;
//import VideoHandle.OnEditorListener;

public class TestFFmpegActivity extends AppCompatActivity {

    private VideoView videoView;
    private FFmpegUtils player;
    private String input;
    private String output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_ffmpeg);
        videoView = (VideoView) findViewById(R.id.video_view);
        input = new File(Environment.getExternalStorageDirectory(),"input.mp4").getAbsolutePath();
        output = new File(Environment.getExternalStorageDirectory(),"output.mp4").getAbsolutePath();
    }

    public void play(View view) {
        player = new FFmpegUtils();
        Surface surface = videoView.getHolder().getSurface();
        player.play(input, surface);

    }


//    public void exec(View view) {
//        String url = "http://sightpvideo-cdn.sightp.com/2018/04/05/20/23/c1e5bb5b03d34d2ebfe37338dcbfdea9.mp4";
//
//        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"a.mp4");
//        EpVideo epVideo = new EpVideo(url);
//        epVideo.clip(1,9);//从第一秒开始，剪辑两秒
//        //输出选项，参数为输出文件路径(目前仅支持mp4格式输出)
//        EpEditor.OutputOption outputOption = new EpEditor.OutputOption(file.getAbsolutePath());
//        outputOption.setWidth(720);//输出视频宽，如果不设置则为原始视频宽高
//        outputOption.setHeight(1280);//输出视频高度
//        outputOption.frameRate = 30;//输出视频帧率,默认30
//        outputOption.bitRate = 10;//输出视频码率,默认10
//        EpEditor.exec(epVideo, outputOption, new OnEditorListener() {
//            @Override
//            public void onSuccess() {
//                LogUtils.d("tag","onSuccess");
//            }
//
//            @Override
//            public void onFailure() {
//                LogUtils.d("tag","onFailure");
//            }
//
//            @Override
//            public void onProgress(float progress) {
//                //这里获取处理进度
//                LogUtils.d("tag","progress："+progress);
//            }
//        });
//    }
//
//    public void exec2(View view) {
//        String url = "http://sightpvideo-cdn.sightp.com/2018/04/05/20/23/c1e5bb5b03d34d2ebfe37338dcbfdea9.mp4";
//        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"input.mp4";
//        String path2 = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"input2.mp4";
//        String cmd = "-i "+path+" -r 50 -b:a 100k "+path2;
//        EpEditor.execCmd(cmd, 0, new OnEditorListener() {
//            @Override
//            public void onSuccess() {
//                LogUtils.d("tag","onSuccess");
//            }
//
//            @Override
//            public void onFailure() {
//                LogUtils.d("tag","onFailure");
//            }
//
//            @Override
//            public void onProgress(float progress) {
//                LogUtils.d("tag","progress："+progress);
//            }
//        });
//    }
}
