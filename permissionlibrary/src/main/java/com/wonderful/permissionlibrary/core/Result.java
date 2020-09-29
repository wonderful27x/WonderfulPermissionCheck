package com.wonderful.permissionlibrary.core;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author wonderful
 * @Date 2020-9-28
 * @Description 权限申请的授权结果
 * @Version 1.0
 */
public class Result {

    private List<String> granted = new ArrayList<>();    //同意授权的权限
    private List<String> denied = new ArrayList<>();     //被拒绝的权限
    private List<String> forbidden = new ArrayList<>();  //被拒绝并且勾选不再提醒的权限

    public void addGranted(String permission){
        if (permission == null)return;
        if (granted == null){
            granted = new ArrayList<>();
        }
        if (!granted.contains(permission)){
            granted.add(permission);
        }
    }

    public void addGranted(List<String> permissions){
        if (permissions == null || permissions.size() == 0)return;
        for (String permission:permissions){
            addGranted(permission);
        }
    }

    public void addDenied(String permission){
        if (permission == null)return;
        if (denied == null){
            denied = new ArrayList<>();
        }
        if (!denied.contains(permission)){
            denied.add(permission);
        }
    }

    public void addDenied(List<String> permissions){
        if (permissions == null || permissions.size() == 0)return;
        for (String permission:permissions){
            addDenied(permission);
        }
    }

    public void addForbidden(String permission){
        if (permission == null)return;
        if (forbidden == null){
            forbidden = new ArrayList<>();
        }
        if (!forbidden.contains(permission)){
            forbidden.add(permission);
        }
    }

    public void addForbidden(List<String> permissions){
        if (permissions == null || permissions.size() == 0)return;
        for (String permission:permissions){
            addForbidden(permission);
        }
    }

    public void Fusion(Result result){
        addGranted(result.getDenied());
        addDenied(result.getDenied());
        addForbidden(result.getForbidden());
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

    @Override
    public String toString() {
        return "Result{\n" +
                "granted=" + granted + "\n" +
                "denied=" + denied + "\n" +
                "forbidden=" + forbidden + "\n" +
                '}';
    }
}
