package com.rzm.commonlibrary.general.http.impl.engine.okhttp;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;

/**
 * @author rzm
 * create at 2018/5/17 上午10:13
 * 为获取上传文件进度而设置的代理类
 */
class ProgressRequestBody extends RequestBody {

    private RequestBody mRequestBody;
    private ProgressListener mProgressListener;
    private long mCurrentLength;
    private long mContentLength;

    public ProgressRequestBody(RequestBody requestBody) {
        this.mRequestBody = requestBody;
    }

    public ProgressRequestBody(MultipartBody requestBody, ProgressListener progressListener) {
        this.mRequestBody = requestBody;
        this.mProgressListener = progressListener;
    }

    @Override
    public long contentLength() throws IOException {
        return mRequestBody.contentLength();
    }

    @Override
    public MediaType contentType() {
        return mRequestBody.contentType();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if (mContentLength != -1) {
            mContentLength = contentLength();
        }
        //获取当前写了多少数据？BufferedSink Sink 就是一个服务器的输出流
        //ForwardingSink仍然是一个代理
        ForwardingSink forwardingSink = new ForwardingSink(sink) {
            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                //每次写都会来到这里
                mCurrentLength += byteCount;
                if (mProgressListener != null) {

                    if (mContentLength == mCurrentLength) {
                        mProgressListener.onProgress(mContentLength, mCurrentLength, true);
                    } else {
                        mProgressListener.onProgress(mContentLength, mCurrentLength, false);
                    }
                }
                super.write(source, byteCount);
            }
        };

        BufferedSink bufferedSink = Okio.buffer(forwardingSink);
        mRequestBody.writeTo(bufferedSink);
        bufferedSink.flush();
    }
}