package com.rzm.commonlibrary.general.imageloader.cache;

import android.content.Context;
import android.graphics.Bitmap;

import com.rzm.commonlibrary.general.imageloader.disk.DiskCache;
import com.rzm.commonlibrary.general.imageloader.request.BitmapRequest;


/**
 * 二级缓存
 */
public class MemoryDiskCache implements BitmapCache {

    //内存缓存
    private MemoryCache mMemoryCache;
    //硬盘缓存
    private DiskCache mDiskCache;

    public MemoryDiskCache(Context context) {
        mMemoryCache = new MemoryCache();
        mDiskCache = DiskCache.getInstance(context);
    }

    @Override
    public void put(BitmapRequest request, Bitmap bitmap) {
        mMemoryCache.put(request, bitmap);
        mDiskCache.put(request, bitmap);
    }

    @Override
    public Bitmap get(BitmapRequest request) {
        Bitmap bitmap = mMemoryCache.get(request);
        if(bitmap == null){
            bitmap = mDiskCache.get(request);
            if(bitmap != null){
                //放入内存，方便再获取
                mMemoryCache.put(request, bitmap);
            }
        }
        return bitmap;
    }

    @Override
    public void remove(BitmapRequest request) {
        mMemoryCache.remove(request);
        mDiskCache.remove(request);
    }

}
