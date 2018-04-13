package com.app.rzm.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by renzhenming on 2018/4/13.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface CalculateConsume {
    //String value();
    //int type();
}
