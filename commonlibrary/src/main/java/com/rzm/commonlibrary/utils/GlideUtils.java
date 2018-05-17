package com.rzm.commonlibrary.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.rzm.commonlibrary.R;

import java.lang.ref.WeakReference;


public class GlideUtils {

    public static void setCircleImageWithRing(final Context context, final String url,final ImageView imageView, int defImage){
        if (!canLoad(context))return;
        Glide.with(context).load(url).placeholder(defImage)
                .transform(new GlideCircleWithRing(context,2, ContextCompat.getColor(context, R.color.gray)))
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
    }


    /**
     * 针对imageview
     *
     * @param context
     * @param url
     * @param imageView
     * @param defImage  添加signature，防止更新头像后glide显示的仍是旧头像（图片服务器地址不变，导致缓存的key不改变，这时候glide不会请求新的图片）
     *                  signature 不能为null否则会崩溃
     */
    public static void setCircleTargetImage(final Activity context, final String url,final ImageView imageView, int defImage) {
        if (!canLoad(context)) return;
        MainBitmapImageViewTarget mainTarget = new MainBitmapImageViewTarget(context, imageView);
        Glide.with(context).load(url).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(defImage).centerCrop().into(mainTarget);
    }
    public static void setCircleTargetImage(final Context context, final String url,final ImageView imageView, int defImage) {
        if (context == null)
            return;
        MainBitmapImageViewTarget mainTarget = new MainBitmapImageViewTarget(context, imageView);
        Glide.with(context).load(url).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(defImage).centerCrop().into(mainTarget);
    }
    /**
     * 静态内部类，防止内存泄漏
     * 这里的上下文使用的是activity，需要若引用
     * 用于评论个人头像显示
     */
    private static class MainBitmapImageViewTarget extends BitmapImageViewTarget {

        private WeakReference<ImageView> reference;
        private WeakReference<Context> cReference;

        public MainBitmapImageViewTarget(Context context, ImageView imageView) {
            super(imageView);
            reference = new WeakReference<>(imageView);
            cReference = new WeakReference<>(context);
        }

        @Override
        protected void setResource(Bitmap resource) {
            super.setResource(resource);
            if (reference != null && cReference != null) {
                ImageView imageView = reference.get();
                Context context = cReference.get();
                if (imageView != null && context != null) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    imageView.setImageDrawable(circularBitmapDrawable);
                }
            }
        }
    }


    /**
     * 针对imageview
     * 用于评论列表页头像
     *
     * @param context
     * @param url
     * @param imageView
     * @param defImage
     */
    public static void setListImage(final Context context, final String url, final ImageView imageView, int defImage) {
        if (!canLoad(context)) return;
        CuBitmapImageViewTarget target = new CuBitmapImageViewTarget(context, imageView, url);
        Glide.with(context).load(url).asBitmap().override(150, 150).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(defImage).centerCrop().into(target);
    }

    /**
     * 判断当前上下文是否存在
     * @param context
     * @return
     */
    private static boolean canLoad(Context context){
        if (context == null)
            return false;
        if (context instanceof Activity){
            Activity activity = (Activity) context;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (activity.isDestroyed()){
                    return false;
                }
            }else if (activity.isFinishing()){
                return false;
            }
        }
        return true;
    }

    /**
     * 静态内部类，防止内存泄漏
     * 这里的Context 为applicioncontext 不需要弱引用
     * 用于评论列表页头像
     */
    private static class CuBitmapImageViewTarget extends BitmapImageViewTarget {

        private final Context context;
        private final String imageUrl;
        private final ImageView imageView;

        public CuBitmapImageViewTarget(Context context, ImageView imageView, String imageUrl) {
            super(imageView);
            this.context = context;
            this.imageUrl = imageUrl;
            this.imageView = imageView;
        }

        @Override
        protected void setResource(Bitmap resource) {
            super.setResource(resource);
            if (imageView != null) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imageView.setImageDrawable(circularBitmapDrawable);
            }
        }
    }

    static class GlideCircleWithRing extends BitmapTransformation {

        private Paint mBorderPaint;
        private float mBorderWidth = 4;

        public GlideCircleWithRing(Context context) {
            super(context);
        }

        public GlideCircleWithRing(Context context, int borderWidth, int borderColor) {
            super(context);
            mBorderWidth = Resources.getSystem().getDisplayMetrics().density * borderWidth;
            mBorderPaint = new Paint();
            mBorderPaint.setDither(true);
            mBorderPaint.setAntiAlias(true);
            mBorderPaint.setColor(borderColor);
            mBorderPaint.setAlpha(50);
            mBorderPaint.setStyle(Paint.Style.STROKE);
            mBorderPaint.setStrokeWidth(mBorderWidth);
        }


        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return circleCrop(pool, toTransform);
        }

        private Bitmap circleCrop(BitmapPool pool, Bitmap source) {
            if (source == null) return null;

            int size = (int) (Math.min(source.getWidth(), source.getHeight()) - (mBorderWidth / 2));
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);
            Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            }
            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            //paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.MIRROR, BitmapShader.TileMode.MIRROR));
            paint.setAntiAlias(true);
            float r = size / 2f;
            //canvas.drawCircle(r, r, r, paint);
            canvas.drawCircle(r, r, r - mBorderWidth, paint);
            if (mBorderPaint != null) {
                float borderRadius = r - mBorderWidth / 2;
                canvas.drawCircle(r, r, borderRadius, mBorderPaint);
            }
            return result;
        }

        @Override
        public String getId() {
            return getClass().getName();
        }
    }

    /**
     * 暂停请求，用于使用application context的情况下，在onDestory中取消请求
     * @param context
     */
    public static void pauseRequest(Context context){
        Glide.with(context).pauseRequests();
    }

}
