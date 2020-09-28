package com.wonderful.permissionlibrary.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author wonderful
 * @Date 2020-9-28
 * @Description 权限申请的接口，权限申请的切入点
 * @Version 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PermissionCheck {
    //需要申请的权限数组
    String[] permissions();
    //当所有权限都被禁止不再提醒时,是否自动启动系统设置界面,默认不开启
    boolean enableSettings() default false;
}
