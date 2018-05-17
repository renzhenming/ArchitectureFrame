package com.rzm.commonlibrary.general.http.impl.engine.okhttp;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.rzm.commonlibrary.general.http.base.HttpCacheUtils;
import com.rzm.commonlibrary.general.http.base.ICallBack;
import com.rzm.commonlibrary.general.http.base.HttpUtils;
import com.rzm.commonlibrary.general.http.base.IHttpEngine;
import com.rzm.commonlibrary.utils.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by rzm on 2017/8/20.
 */
public class OkHttpEngine implements IHttpEngine {

    private static final String TAG = "OkHttpEngine";
    private static final String DOWNLOAD_SAVE_PATH = "Download_File";
    private static OkHttpClient mOkHttpClient = new OkHttpClient();

    private static Handler mHandler = new Handler();


    /***********************************************************************************************
     *
     * ***********************************      post请求          ***********************************
     *
     **********************************************************************************************/


    @Override
    public void post(final boolean cache, final Context context, String url, Map<String, Object> params, final ICallBack callBack) {

        new OkHttpClient.Builder().connectTimeout(10, TimeUnit.MINUTES);

        final String paramsUrl = HttpUtils.printUrlWithParams(url, params);  //打印
        LogUtils.e(TAG, "post url:"+paramsUrl);

        if (cache) {
            final String cacheJson = HttpCacheUtils.getInstance().getCache(context,paramsUrl);
            if (!TextUtils.isEmpty(cacheJson)) {
                LogUtils.e(TAG, "post cache：" + cacheJson);
                //获取到缓存，直接执行成功方法
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onSuccess(cacheJson);
                    }
                });
            }
        }

        RequestBody requestBody = appendBody(params);
        Request request = new Request.Builder()
                .url(url)
                .tag(context)
                .post(requestBody)
                .build();

        // 两个回掉方法都不是在主线程中
        mOkHttpClient.newCall(request).enqueue(
                new Callback() {
                    @Override
                    public void onFailure(Call call, final IOException e) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callBack.onError(e);
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        final String result = response.body().string();
                        LogUtils.e(TAG,"post result:"+result);

                        if (cache) {
                            String cacheJson = HttpCacheUtils.getInstance().getCache(context,paramsUrl);
                            //2.每次获取到的结果，和上次的缓存进行比对
                            if (!TextUtils.isEmpty(cacheJson)) {
                                if (result.equals(cacheJson)) {
                                    LogUtils.e(TAG, "data is the same with cache");
                                    return;
                                }
                            }
                        }

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callBack.onSuccess(result);
                            }
                        });

                        if (cache) {
                            boolean success = HttpCacheUtils.getInstance().setCache(context,paramsUrl,result);
                            LogUtils.e(TAG, "post set cache -->> " + success+","+result);
                        }
                    }
                }
        );
    }

    private RequestBody appendBody(Map<String, Object> params) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        addParams(builder, params);
        return builder.build();
    }

    // 添加参数
    private void addParams(MultipartBody.Builder builder, Map<String, Object> params) {
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                builder.addFormDataPart(key, params.get(key) + "");
                Object value = params.get(key);
                if (value instanceof File) {
                    // 处理文件 --> Object File
                    File file = (File) value;
                    builder.addFormDataPart(key, file.getName(), RequestBody
                            .create(MediaType.parse(OkHttpUtils.guessMimeType(file
                                    .getAbsolutePath())), file));
                } else if (value instanceof List) {
                    // 代表提交的是 List集合
                    try {
                        List<File> listFiles = (List<File>) value;
                        for (int i = 0; i < listFiles.size(); i++) {
                            // 获取文件
                            File file = listFiles.get(i);
                            builder.addFormDataPart(key + i, file.getName(), RequestBody
                                    .create(MediaType.parse(OkHttpUtils.guessMimeType(file
                                            .getAbsolutePath())), file));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    builder.addFormDataPart(key, value + "");
                }
            }
        }
    }

    /***********************************************************************************************
     *
     * ***********************************      get请求           ***********************************
     *
     **********************************************************************************************/

    @Override
    public void get(final boolean cache, final Context context, String url, Map<String, Object> params, final ICallBack callBack) {

        final String paramsUrl = HttpUtils.printUrlWithParams(url, params);
        LogUtils.e(TAG,"get url:"+paramsUrl);

        if (cache) {
            final String cacheJson = HttpCacheUtils.getInstance().getCache(context,paramsUrl);
            if (!TextUtils.isEmpty(cacheJson)) {
                LogUtils.e(TAG, "get cache：" + cacheJson);
                //获取到缓存，直接执行成功方法
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onSuccess(cacheJson);
                    }
                });
            }
        }

        Request.Builder requestBuilder = new Request.Builder().url(paramsUrl).tag(context);
        //默认是GET请求
        Request request = requestBuilder.build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        callBack.onError(e);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();

                if (cache) {
                    String cacheJson = HttpCacheUtils.getInstance().getCache(context,paramsUrl);
                    //将获取到的结果和缓存对比，如果数据没有更新，就不再刷新
                    if (!TextUtils.isEmpty(cacheJson)) {
                        if (result.equals(cacheJson)) {
                            LogUtils.e(TAG, "data is the same with cache");
                            return;
                        }
                    }
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onSuccess(result);
                    }
                });
                LogUtils.e(TAG,"get result:"+ result);

                if (cache) {
                    boolean success = HttpCacheUtils.getInstance().setCache(context,paramsUrl,result);
                    LogUtils.e(TAG, "get set cache " + success+","+result);

                }
            }
        });
    }

    /***********************************************************************************************
     *
     * ***********************************      download请求      ***********************************
     *
     **********************************************************************************************/

    @Override
    public void download(final String url,final ICallBack callBack) {
        Request request = new Request.Builder().url(url).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.e(TAG,"download file error:"+url);
                        callBack.onError(e);
                    }
                });

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                String savePath = OkHttpUtils.isExistDir(DOWNLOAD_SAVE_PATH);

                try {
                    is = response.body().byteStream();
                    final long total = response.body().contentLength();
                    File file = new File(savePath, OkHttpUtils.getNameFromUrl(url));
                    LogUtils.e(TAG,"download file save to:"+file.getAbsolutePath());
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        // 下载中
                        final long currentProgress = sum;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                LogUtils.e(TAG,"download file progress:"+currentProgress+"/"+total);
                                callBack.onDownloadProgress(total, currentProgress);
                            }
                        });
                    }
                    fos.flush();
                    // 下载完成
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            LogUtils.e(TAG,"download file finish");
                            callBack.onSuccess("");
                        }
                    });
                } catch (final Exception e) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            LogUtils.e(TAG,"download file error:"+e.toString());
                            callBack.onError(e);
                        }
                    });
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    /***********************************************************************************************
     *
     * ***********************************       upload请求       ***********************************
     *
     **********************************************************************************************/

    @Override
    public void upload(String path, String url, final ICallBack callBack) {
        File file = new File(path);
        if (!file.isFile() && !file.exists()){
            throw new RuntimeException("file not found");
        }
        LogUtils.e(TAG,"upload file:"+file.getAbsolutePath());
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create
                        (MediaType.parse(OkHttpUtils.guessMimeType(file.getAbsolutePath())),file))
                .build();

        //静态代理，监听上传进度
        ProgressRequestBody body = new ProgressRequestBody(requestBody, new ProgressListener() {
            @Override
            public void onProgress(final long total, final long current, final boolean done) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.e(TAG,"upload file progress:"+current+"/"+total + " is finish "+done);
                        callBack.onUploadProgress(total, current);
                        if (done){
                            callBack.onSuccess(null);
                        }
                    }
                });
            }
        });

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.e(TAG,"upload file error:"+e.toString());
                        callBack.onError(e);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
        });
    }



}
