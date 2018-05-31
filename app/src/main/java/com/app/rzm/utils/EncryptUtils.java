package com.app.rzm.utils;

import android.content.Context;

/**
 * ndk实现参数加密
 */
public class EncryptUtils {

    static {
        System.loadLibrary("encrypt");
    }

    public static native String encryptForAndroid(Context context, String param);
}
