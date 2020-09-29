package com.wonderful.permissionlibrary.core;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.wonderful.permissionlibrary.R;
import com.wonderful.permissionlibrary.annotation.PermissionResultInterface;
import com.wonderful.permissionlibrary.utils.MemoryUtil;

/**
 * @Author wonderful
 * @Date 2020-9-28
 * @Description 权限申请的主要场所，因为权限申请依赖于Activity，所有需要这样一个Activity来集中处理需要申请的权限
 * 当一个界面需要申请权限是都会跳转到这里进行
 * 需要把它做成对话框形式并取消所有动画效果
 * @Version 1.0
 */
public class PermissionActivity extends AppCompatActivity {

    private static final String TAG = "PermissionActivity";

    public static final String PERMISSION_KEY = "PERMISSION_KEY";
    //需要申请的权限
    private String[] permissions;
    private int requestCode = 1001;
    //权限处理结果回掉
    public static PermissionResultInterface permissionResultInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        Intent intent = getIntent();
        permissions = intent.getStringArrayExtra(PERMISSION_KEY);

        requestPermission();
    }

    private void requestPermission(){
        if (permissions == null || permissions.length == 0){
            finish();
            return;
        }

        //开始申请权限
        ActivityCompat.requestPermissions(this,permissions,requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //将申请过权限的做一个记录,方便shouldShowRequestPermissionRationale的判断
        stateSave(permissions);

        //解析授权结果
        Result result = PermissionUtil.permissionRequestResult(this,permissions,grantResults);

        //回调结果,这个回调一定会调用
        permissionResultInterface.permissionResult(result);

        //如果有授权了的权限
        if (result.getGranted() != null && result.getGranted().size() != 0){
            permissionResultInterface.granted(result.getGranted());
        }

        //如果有禁止了权限，但是没有勾选不再提醒
        if (result.getDenied() != null && result.getDenied().size() != 0){
            permissionResultInterface.denied(result.getDenied());
        }

        //如果有禁止了，并勾选了不再提醒的权限
        if (result.getForbidden() != null && result.getForbidden().size() != 0){
            permissionResultInterface.forbidden(result.getForbidden());
        }
        Log.d(TAG, "onRequestPermissionsResult: ");
        finish();
    }

    //将申请过权限的做一个记录,方便shouldShowRequestPermissionRationale的判断
    private void stateSave(String[] permissions){
        for (String permission:permissions){
            MemoryUtil.sharedPreferencesSaveBoolean(this,permission,true);
        }
    }

    /**
     * 发起权限申请
     */
    public static void permissionRequest(Context context, String[] permissions, PermissionResultInterface permissionResultInterface){
        PermissionActivity.permissionResultInterface = permissionResultInterface;
        Intent intent = new Intent(context,PermissionActivity.class);
        intent.putExtra(PERMISSION_KEY,permissions);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        permissionResultInterface = null;
        permissions = null;
        requestCode = -1;
        System.gc();
    }

    @Override
    public void finish() {
        super.finish();
        //取消动画
        overridePendingTransition(0, 0);
    }
}