package com.wonderful.permissionlibrary.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author wonderful
 * @Date 2020-9-28
 * @Description 权限授权结果回调接口，当进行了权限申请时被它注解的方法一定会被调用
 * TODO 注意一定会被调用的意思是一旦触发了权限申请,无论权限是已经授权了,没有授权,还是被禁止了,被注解的方法就会被调用,授权窗口不一定弹出
 * TODO 而被注解的方法应该设置一个参数Result,这个参数包含了以PermissionCheck注解申请的所有权限为基础的授权结果
 * @Version 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PermissionResult {

}
