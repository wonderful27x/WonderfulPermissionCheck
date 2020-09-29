package com.wonderful.permissionlibrary.annotation;

import com.wonderful.permissionlibrary.core.Result;

/**
 * @Author wonderful
 * @Date 2020-9-28
 * @Description 权限申请授权结果回调接口,内部使用,不对外暴露
 * @Version 1.0
 */
public interface PermissionResultInterface extends IPermission{

    //授权结果回调方法,参数为授权结果封装类
    public void permissionResult(Result result);

}
