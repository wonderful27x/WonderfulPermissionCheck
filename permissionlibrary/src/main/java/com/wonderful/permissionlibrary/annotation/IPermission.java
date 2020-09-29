package com.wonderful.permissionlibrary.annotation;

import java.util.List;

/**
 * @Author wonderful
 * @Date 2020-9-28
 * @Description 权限申请授权结果回调接口,可对外暴露
 * TODO 注意这三个接口回调的条件是弹出了权限授权窗口后用户执行了授权操作
 * @Version 1.0
 */
public interface IPermission {
    public void granted(List<String> permission);   //同意了权限,参数：同意的授权的权限
    public void denied(List<String> permission);    //拒绝了权限,参数：拒绝授权的权限
    public void forbidden(List<String> permission); //拒绝并勾选了不在提示,参数：拒绝并勾选了不在提示的权限
}
