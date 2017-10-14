package com.xxl.viewfindlib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by xxl on 2017/10/12.
 * 自定义findViewById注解
 */
@Target(ElementType.FIELD)//成员变量注解
@Retention(RetentionPolicy.RUNTIME)//运行时注解
public @interface ViewById {
    int value();
}
