package com.app.rzm.utils;

import android.content.Context;

/**
 * ndk实现参数加密
 */
public class EncryptUtils {

    static {
        System.loadLibrary("encrypt");
    }

    /**
     * 对一个字符串进行加密
     * @param context
     * @param param
     * @return
     */
    public static native String encrypt(Context context, String param);

    /*
     * 校验app签名
     * @param context
     */
    public static native void checkSignature(Context context);
}
