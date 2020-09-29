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

/**
 * @Author wonderful
 * @Date 2020-9-29
 * @Description 权限申请用法2
 * @Version 1.0
 */
public class MainActivity2 extends AppCompatActivity {

    private static final String TAG = "MainActivity2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        findViewById(R.id.request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permission();
            }
        });
        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionUtil.startSystemPermissionActivity(MainActivity2.this);
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
     * 使用PermissionResult注解一个方法来获取授权结果,可以不指定参数,这样没有意义了
     */
    @PermissionResult
    private void permissionResult(){
        //Toast.makeText(this,"您拒绝了权限！",Toast.LENGTH_SHORT).show();
        Log.d(TAG, "permissionGranted-授权结果: ");
    }

    /**
     * 使用PermissionGranted注解一个方法来获取授权结果,仅当有权限被授权时调用,可以不指定参数
     */
    @PermissionGranted
    private void permissionGranted(){
        //Toast.makeText(this,"您拒绝了权限！",Toast.LENGTH_SHORT).show();
        Log.d(TAG, "PermissionGranted-已授权的权限: ");
    }

    /**
     * 使用PermissionDenied注解一个方法来获取授权结果,仅当有权限被拒绝时调用,可以不指定参数
     */
    @PermissionDenied
    private void permissionDenied(){
        //Toast.makeText(this,"您拒绝了权限！",Toast.LENGTH_SHORT).show();
        Log.d(TAG, "permissionDenied-被拒绝的权限: ");
    }

    /**
     * 使用PermissionForbid注解一个方法来获取授权结果,仅当有权限被禁止并勾选了不再提醒时调用,可以不指定参数
     */
    @PermissionForbid
    private void permissionForbid(){
        //Toast.makeText(this,"您取消了权限，将不再提醒！",Toast.LENGTH_SHORT).show();
        Log.d(TAG, "permissionForbid-被拒绝并不再提醒的权限: ");
    }
}