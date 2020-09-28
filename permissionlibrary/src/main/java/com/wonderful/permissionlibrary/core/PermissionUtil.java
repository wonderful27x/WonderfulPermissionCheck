package com.wonderful.permissionlibrary.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.collection.SimpleArrayMap;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author wonderful
 * @Date 2020-9-28
 * @Description 权限申请工具类
 * @Version 1.0
 */
class PermissionUtil {

    private static final String TAG = "PermissionUtil";

    //定义需要申请的运行时权限，key：权限，value：权限的最小sdk版本
    private static SimpleArrayMap<String, Integer> MIN_SDK_PERMISSIONS = new SimpleArrayMap<>(8);

    static {
        MIN_SDK_PERMISSIONS.put("android.permission.READ_EXTERNAL_STORAGE", 16);
        MIN_SDK_PERMISSIONS.put("android.permission.READ_CALENDAR", 23);
        MIN_SDK_PERMISSIONS.put("android.permission.CALL_PHONE", 23);
    }

    /**
     * 判断传入的权限是否需要申请
     * @param context
     * @param permissions
     * @return 返回需要申请的权限
     */
    public static List<String> needPermissionRequest(Context context, String... permissions){
        List<String> permissionList = new ArrayList<>();
        for (String permission:permissions){
            //如果权限存在并且没有被授权则说明需要去申请权限
            if (permissionExists(permission) && !permissionGranted(context,permission)){
                permissionList.add(permission);
            }
        }
        return permissionList;
    }

    /**
     * 判断当前sdk版本中是否存在这个权限
     * @param permission
     * @return
     */
    private static boolean permissionExists(String permission){
        Integer minSdk = MIN_SDK_PERMISSIONS.get(permission);
        //如果是定义的权限库中的权限并且当前sdk版本大于等于最小运行时权限版本，则说明权限存在
        if (minSdk != null && Build.VERSION.SDK_INT >= minSdk){
            return true;
        }
        return false;
    }

    /**
     * 判断权限是否已经被授权了
     * @param context
     * @param permission
     * @return
     */
    private static boolean permissionGranted(Context context, String permission){
        int granted = ContextCompat.checkSelfPermission(context, permission);
        return granted == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 验证授权结果判断权限是否真的申请成功了
     * @param grantedResults
     * @return 返回授权结果
     */
    public static PermissionResult permissionRequestResult(Activity activity, String[] permissions, int[] grantedResults){
        PermissionResult permissionResult = new PermissionResult();
        for (int index=0; index<grantedResults.length; index++){
            int result = grantedResults[index];
            //如果授权了则加入授权列表
            if (result == PackageManager.PERMISSION_GRANTED){
                permissionResult.addGranted(permissions[index]);
            }else if (result == PackageManager.PERMISSION_DENIED){
                //如果是拒绝了权限
                if(shouldShowRequestPermissionRationale(activity,permissions[index])){
                    permissionResult.addDenied(permissions[index]);
                }
                //否则是拒绝了并勾选了不再提醒
                else {
                    permissionResult.addForbidden(permissions[index]);
                }
            }
        }
        return permissionResult;
    }

    /**
     * 判断是否需要提示用户权限被拒绝了
     * @param activity
     * @param permission
     * @return
     *
     * shouldShowRequestPermissionRationale
     * 1，在允许询问时返回true ；
     * 2，在权限通过 或者权限被拒绝并且禁止询问时返回false 但是有一个例外，就是重来没有询问过的时候，
     * 也是返回的false 所以单纯的使用shouldShowRequestPermissionRationale去做什么判断，
     * 是没用的，只能在请求权限回调后再使用。
     * Google的原意是：
     * 1，没有申请过权限，申请就是了，所以返回false；
     * 2，申请了用户拒绝了，那你就要提示用户了，所以返回true；
     * 3，用户选择了拒绝并且不再提示，那你也不要申请了，也不要提示用户了，所以返回false；
     * 4，已经允许了，不需要申请也不需要提示，所以返回false；
     */
    public static boolean shouldShowRequestPermissionRationale(Activity activity, String permission){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,permission)){
            return true;
        }
        return false;
    }

    /**
     * 跳转到系统权限设置界面
     */
    public static void startSystemPermissionActivity(Context context){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + context.getPackageName()));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
