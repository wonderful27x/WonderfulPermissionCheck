package com.wonderful.wonderfulpermissioncheck;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.wonderful.permissionlibrary.annotation.PermissionCheck;
import com.wonderful.permissionlibrary.annotation.PermissionDenied;
import com.wonderful.permissionlibrary.annotation.PermissionForbid;
import com.wonderful.permissionlibrary.annotation.PermissionGranted;
import com.wonderful.permissionlibrary.annotation.PermissionResult;
import com.wonderful.permissionlibrary.core.PermissionUtil;
import com.wonderful.permissionlibrary.core.Result;
import java.util.List;

/**
 * @Author wonderful
 * @Date 2020-9-29
 * @Description 权限申请用法1
 * @Version 1.0
 */
public class MainActivity1 extends AppCompatActivity {

    private static final String TAG = "MainActivity1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        findViewById(R.id.request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permission();
            }
        });
        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionUtil.startSystemPermissionActivity(MainActivity1.this);
            }
        });
    }


    /**
     * 写一个方法,并使用PermissionCheck注解声明需要申请的权限,然后在需要申请权限的地方调用此方法
     */
    @PermissionCheck(
            permissions = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_CALENDAR
            }
    )
    private void permission(){}

    /**
     * 使用PermissionResult注解一个方法来获取授权结果,结果封装在参数Result中,
     * 这里的结果是基于PermissionCheck中的所有权限的,Result包含所有权限授权结果的信息
     * @param permissionResult
     */
    @PermissionResult
    private void permissionResult(Result permissionResult){
        //Toast.makeText(this,"您拒绝了权限！",Toast.LENGTH_SHORT).show();
        Log.d(TAG, "permissionGranted-授权结果: " + permissionResult.toString());
    }

    /**
     * 使用PermissionGranted注解一个方法来获取授权结果,仅当有权限被授权时调用,permission参数包含被授权的权限
     * @param permissions
     */
    @PermissionGranted
    private void permissionGranted(List<String> permissions){
        //Toast.makeText(this,"您拒绝了权限！",Toast.LENGTH_SHORT).show();
        Log.d(TAG, "PermissionGranted-已授权的权限: " + permissions);
    }

    /**
     * 使用PermissionDenied注解一个方法来获取授权结果,仅当有权限被拒绝时调用,permission参数包含被拒绝的权限
     * @param permissions
     */
    @PermissionDenied
    private void permissionDenied(List<String> permissions){
        //Toast.makeText(this,"您拒绝了权限！",Toast.LENGTH_SHORT).show();
        Log.d(TAG, "permissionDenied-被拒绝的权限: " + permissions);
    }

    /**
     * 使用PermissionForbid注解一个方法来获取授权结果,仅当有权限被禁止并勾选了不再提醒时调用,permission参数包含被禁止并勾选了不再提醒的权限
     * @param permissions
     */
    @PermissionForbid
    private void permissionForbid(List<String> permissions){
        //Toast.makeText(this,"您取消了权限，将不再提醒！",Toast.LENGTH_SHORT).show();
        Log.d(TAG, "permissionForbid-被拒绝并不再提醒的权限: " + permissions);
    }
}