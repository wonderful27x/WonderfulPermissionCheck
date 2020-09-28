package com.wonderful.wonderfulpermissioncheck;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.wonderful.permissionlibrary.annotation.PermissionCheck;
import com.wonderful.permissionlibrary.annotation.PermissionDenied;
import com.wonderful.permissionlibrary.annotation.PermissionForbid;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用AspectJ搭建一个动态权限申请框架
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionRequest(new ArrayList<String>());
            }
        });
    }

    //不需要返回参数
//    @Permission(
//            value = {
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.CALL_PHONE,
//                    Manifest.permission.READ_CALENDAR
//            },
//            requestCode = 200
//    )
//    private void permissionRequest(){
//        //Toast.makeText(this,"权限申请成功！",Toast.LENGTH_SHORT).show();
//        Log.d(TAG, "permissionRequest: ");
//    }

    //希望获取成功授权的权限列表,
    //TODO 调用时传入一个不为null的空对象
    @PermissionCheck(
            permissions = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_CALENDAR
            }
    )
    private void permissionRequest(List<String> permission){
        //Toast.makeText(this,"权限申请成功！",Toast.LENGTH_SHORT).show();
        Log.d(TAG, "成功授权的权限: " + permission);
    }

    @PermissionDenied
    private void permissionDenied(){
        //Toast.makeText(this,"您拒绝了权限！",Toast.LENGTH_SHORT).show();
        Log.d(TAG, "permissionDenied: ");
    }

    //这样可以获取被取消的权限列表
    @PermissionDenied
    private void permissionDenied(List<String> permission){
        //Toast.makeText(this,"您拒绝了权限！",Toast.LENGTH_SHORT).show();
        Log.d(TAG, "被拒绝的权限: " + permission);
    }

    @PermissionForbid
    private void permissionCancel(){
        //Toast.makeText(this,"您取消了权限，将不再提醒！",Toast.LENGTH_SHORT).show();
        Log.d(TAG, "permissionCancel: ");
    }

    //这样可以获取被取消并且勾选了不在提醒的权限列表
    @PermissionForbid
    private void permissionCancel(List<String> permissions){
        //Toast.makeText(this,"您取消了权限，将不再提醒！",Toast.LENGTH_SHORT).show();
        Log.d(TAG, "被拒绝并不再提醒的权限: " + permissions);
    }
}