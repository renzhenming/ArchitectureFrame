package com.rzm.commonlibrary.general.thread;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import java.util.Arrays;
import java.util.List;

/**
 * 继承HandlerThread实现的异步任务
 *
 *    Handler mUIHandler = new Handler(this);
 *    mDownloadThread = new DownloadThread("下载线程");
 *    mDownloadThread.setUIHandler(mUIHandler);
 *    mDownloadThread.setDownloadUrls("http://pan.baidu.com/s/1qYc3EDQ",
 *   "http://bbs.005.tv/thread-589833-1-1.html", "http://list.youku.com/show/id_zc51e1d547a5b11e2a19e.html?");
 *    mDownloadThread.start();
 *
 *  @Override
 *  public boolean handleMessage(final Message msg) {
 *      switch (msg.what) {
 *          case DownloadThread.TYPE_FINISHED:
 *              mTvFinishMsg.setText(mTvFinishMsg.getText().toString() + "\n " + msg.obj);
 *          break;
 *          case DownloadThread.TYPE_START:
 *              mTvStartMsg.setText(mTvStartMsg.getText().toString() + "\n " + msg.obj);
 *          break;
 *      }
 *      return true;
 *  }
 */
public class DownloadThread extends HandlerThread implements Handler.Callback {

    private final String TAG = this.getClass().getSimpleName();
    private final String KEY_URL = "url";
    public static final int TYPE_START = 1;
    public static final int TYPE_FINISHED = 2;

    private Handler mWorkerHandler;
    private Handler mUIHandler;
    private List<String> mDownloadUrlList;

    public DownloadThread(final String name) {
        super(name);
    }

    @Override
    protected void onLooperPrepared() {    //执行初始化任务
        super.onLooperPrepared();
        mWorkerHandler = new Handler(getLooper(), this);    //使用子线程中的 Looper
        if (mUIHandler == null) {
            throw new IllegalArgumentException("Not set UIHandler!");
        }

        //将接收到的任务消息挨个添加到消息队列中
        for (String url : mDownloadUrlList) {
            Message message = mWorkerHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString(KEY_URL, url);
            message.setData(bundle);
            mWorkerHandler.sendMessage(message);
        }
    }

    public void setDownloadUrls(String... urls) {
        mDownloadUrlList = Arrays.asList(urls);
    }

    public Handler getUIHandler() {
        return mUIHandler;
    }

    //注入主线程 Handler
    public DownloadThread setUIHandler(final Handler UIHandler) {
        mUIHandler = UIHandler;
        return this;
    }

    /**
     * 子线程中执行任务，完成后发送消息到主线程
     *
     * @param msg
     * @return
     */
    @Override
    public boolean handleMessage(final Message msg) {
        if (msg == null || msg.getData() == null) {
            return false;
        }

        String url = (String) msg.getData().get(KEY_URL);


        //下载开始，通知主线程
        Message startMsg = mUIHandler.obtainMessage(TYPE_START, "\n 开始下载 @" + System.currentTimeMillis() + "\n" + url);
        mUIHandler.sendMessage(startMsg);

        //TODO

        //下载完成，通知主线程
        Message finishMsg = mUIHandler.obtainMessage(TYPE_FINISHED, "\n 下载完成 @" + System.currentTimeMillis() + "\n" + url);
        mUIHandler.sendMessage(finishMsg);

        return true;
    }

    @Override
    public boolean quitSafely() {
        mUIHandler = null;
        return super.quitSafely();
    }
}