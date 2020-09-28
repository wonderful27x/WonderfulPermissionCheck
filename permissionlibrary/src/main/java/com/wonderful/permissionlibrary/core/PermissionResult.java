package com.wonderful.permissionlibrary.core;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author wonderful
 * @Date 2020-9-28
 * @Description 权限申请的授权结果
 * @Version 1.0
 */
public class PermissionResult {

    private List<String> granted = new ArrayList<>();    //同意授权的权限
    private List<String> denied = new ArrayList<>();     //被拒绝的权限
    private List<String> forbidden = new ArrayList<>();  //被拒绝并且勾选不再提醒的权限

    public void addGranted(String permission){
        if (granted == null){
            granted = new ArrayList<>();
        }
        granted.add(permission);
    }

    public void addGranted(List<String> permissions){
        if (granted == null){
            granted = new ArrayList<>();
        }
        granted.addAll(permissions);
    }

    public void addDenied(String permission){
        if (denied == null){
            denied = new ArrayList<>();
        }
        denied.add(permission);
    }

    public void addDenied(List<String> permissions){
        if (denied == null){
            denied = new ArrayList<>();
        }
        denied.addAll(permissions);
    }

    public void addForbidden(String permission){
        if (forbidden == null){
            forbidden = new ArrayList<>();
        }
        forbidden.add(permission);
    }

    public void addForbidden(List<String> permissions){
        if (forbidden == null){
            forbidden = new ArrayList<>();
        }
        forbidden.addAll(permissions);
    }

    public List<String> getGranted() {
        return granted;
    }

    public List<String> getDenied() {
        return denied;
    }

    public List<String> getForbidden() {
        return forbidden;
    }
}
