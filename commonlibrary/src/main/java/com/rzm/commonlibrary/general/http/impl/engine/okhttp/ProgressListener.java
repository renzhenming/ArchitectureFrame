package com.rzm.commonlibrary.general.http.impl.engine.okhttp;

/**
* @author rzm
* create at 2018/5/17 上午10:11
* 上传下载进度监听
*/
interface ProgressListener {
    void onProgress(long total, long current, boolean done);
}