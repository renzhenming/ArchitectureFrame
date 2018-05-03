package com.app.rzm.aop;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.rzm.commonlibrary.general.global.BaseApplication;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * Created by renzhenming on 2018/4/13.
 * 切面
 */
@Aspect
public class BehaviorAspect {

    public static final String TAG = "BehaviorAspect";
    /**
     * 切点(指定当前要切入的是com.app.rzm.aop.CalculateConsume这个注解，* *表示任何方法名(..)表示任何参数)
     */
    @Pointcut("execution(@com.app.rzm.aop.CalculateConsume  * *(..))")
    public void ABehavior(){

    }

    /**
     * 切点(指定当前要切入的是com.app.rzm.aop.NetworkCheck这个注解，* *表示任何方法名(..)表示任何参数)
     */
    @Pointcut("execution(@com.app.rzm.aop.NetworkCheck  * *(..))")
    public void BBehavior(){

    }

    @Around("ABehavior()")
    public Object calculateTime(ProceedingJoinPoint point) throws  Throwable{
        MethodSignature signature = (MethodSignature) point.getSignature();
        CalculateConsume annotation = signature.getMethod().getAnnotation(CalculateConsume.class);
        if (annotation != null) {
            long start = System.currentTimeMillis();
            //执行方法
            Object object = point.proceed();
            long end = System.currentTimeMillis();
            //方法执行完成
            Log.i(TAG, "consume time = " + (end - start));
            return null;
        }
        return null;

    }

    @Around("BBehavior()")
    public Object checkNet(ProceedingJoinPoint point) throws  Throwable{
        MethodSignature signature = (MethodSignature) point.getSignature();
        NetworkCheck annotation = signature.getMethod().getAnnotation(NetworkCheck.class);
        if (annotation != null) {
            if (!isNetWorkConn(BaseApplication.getContext())) {
                Toast.makeText(BaseApplication.getContext(),"网络断开",Toast.LENGTH_SHORT).show();
                return null;
            }
            Object object = point.proceed();

            return null;
        }
        return null;
    }

    //判断网络是否连接
    public static boolean isNetWorkConn(Context context){
        ConnectivityManager manager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info=manager.getActiveNetworkInfo();
        if(null!=info){
            return info.isConnected();
        }else {
            return false;
        }
    }
}
