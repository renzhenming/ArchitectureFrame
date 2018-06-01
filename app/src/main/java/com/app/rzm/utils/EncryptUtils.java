package com.app.rzm.utils;

import android.content.Context;

/**
 * ndk实现参数加密
 */
public class EncryptUtils {

    static {
        System.loadLibrary("encrypt");
    }

    public static String encrypt(Context context, String param){
        checkSignature(context);
        return encryptNative(context,param);
    }

    /**
     * 对一个字符串进行加密
     * @param context
     * @param param
     * @return
     */
    private static native String encryptNative(Context context, String param);

    /**
     * 校验app签名
     * @param context
     */
    private static native void checkSignature(Context context);
}
