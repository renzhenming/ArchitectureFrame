package com.rzm.ffmpeglibrary;

public class FFmpegUtils {

    static{
        System.loadLibrary("myffmpeg");
    }

    public native static void decode(String input,String output);
}
