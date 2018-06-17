package com.rzm.commonlibrary.general.imageloader.policy;


import com.rzm.commonlibrary.general.imageloader.request.BitmapRequest;

/**
 * Author:renzhenming
 * Time:2018/6/13 7:23
 * Description:This is ReversePolicy
 */
public class SerialPolicy implements LoadPolicy {

    @Override
    public int compareTo(BitmapRequest request1, BitmapRequest request2) {
        return request2.getSerialNum()-request1.getSerialNum();
    }
}
