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

/**
 * 权限申请的主要场所，因为权限申请依赖于Activity，所有需要这样一个Activity来集中处理需要申请的权限
 * 当一个界面需要申请权限是都会跳转到这里进行
 * 需要把它做成对话框形式并取消所有动画效果
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
        //解析授权结果
        PermissionResult permissionResult = PermissionUtil.permissionRequestResult(this,permissions,grantResults);

        //回调结果,这个回调一定会调用
        permissionResultInterface.permissionResult(permissionResult);

        //如果有授权了的权限
        if (permissionResult.getGranted() != null && permissionResult.getGranted().size() != 0){
            permissionResultInterface.granted(permissionResult.getGranted());
        }

        //如果有禁止了权限，但是没有勾选不再提醒
        if (permissionResult.getDenied() != null && permissionResult.getDenied().size() != 0){
            permissionResultInterface.denied(permissionResult.getDenied());
        }

        //如果有禁止了，并勾选了不再提醒的权限
        if (permissionResult.getForbidden() != null && permissionResult.getForbidden().size() != 0){
            permissionResultInterface.forbidden(permissionResult.getForbidden());
        }
        Log.d(TAG, "onRequestPermissionsResult: ");
        finish();
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