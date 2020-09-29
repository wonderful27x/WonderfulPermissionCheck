package com.wonderful.permissionlibrary.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author wonderful
 * @Date 2020-9-28
 * @Description 权限被拒绝的接口，当被拒绝后需要通知，会反射调用被它注解的方法
 * TODO 注意这个注解方法回调的条件是弹出了权限授权窗口后用户执行了授权操作
 * @Version 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PermissionDenied {

}
