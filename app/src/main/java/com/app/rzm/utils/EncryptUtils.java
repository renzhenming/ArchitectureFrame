package com.app.rzm.utils;

/**
 * ndk实现参数加密
 */
public class EncryptUtils {

    static {
        System.loadLibrary("encrypt");
    }

    public static native String encryptToMd5(String param);
}
