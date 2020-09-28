package com.wonderful.permissionlibrary.core;

import android.app.Activity;
import android.util.Log;
import androidx.fragment.app.Fragment;
import com.wonderful.permissionlibrary.annotation.ActivityInterface;
import com.wonderful.permissionlibrary.annotation.IPermission;
import com.wonderful.permissionlibrary.annotation.PermissionCheck;
import com.wonderful.permissionlibrary.annotation.PermissionDenied;
import com.wonderful.permissionlibrary.annotation.PermissionForbid;
import com.wonderful.permissionlibrary.annotation.PermissionGranted;
import com.wonderful.permissionlibrary.annotation.PermissionResultInterface;
import com.wonderful.permissionlibrary.utils.MemoryUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 权限申请切面
 */
@Aspect
class PermissionAspect {

    private static final String TAG = "PermissionAspect";
    private boolean enableSettings = false;

    //&& @annotation(permission)能够获取注解
    @Pointcut("execution(@com.wonderful.permissionlibrary.annotation.PermissionCheck * *(..)) && @annotation(permission)")
    public void permissionPointCut(PermissionCheck permission){}

    @Around("permissionPointCut(permission)")
    public void executeAnnotationMethod(final ProceedingJoinPoint joinPoint, final PermissionCheck permission){
        try {
            executeMethod(joinPoint,permission);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                joinPoint.proceed();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    //执行处理逻辑
    private void executeMethod(final ProceedingJoinPoint joinPoint, final PermissionCheck permission) throws Exception{
        final Object object = joinPoint.getThis();
        if (object == null){
            Log.d(TAG, "Aspect calls an error of null object!");
            throw new NullPointerException("Aspect calls an error of null object!");
        }

        //获取Activity环境
        Activity context = null;
        if (object instanceof Activity){
            context = (Activity) object;
        }else if (object instanceof Fragment){
            context = ((Fragment)object).getActivity();
        } else if (object instanceof ActivityInterface){
            context = ((ActivityInterface)object).getActivity();
        }

        if (context == null){
            Log.d(TAG, "Aspect calls an error of null Context!");
            throw new NullPointerException("Aspect calls an error of null Context!");
        }

        enableSettings = permission.enableSettings();

        //先判断权限是否已经授权了
        List<String> needPermissions = PermissionUtil.needPermissionRequest(context,permission.permissions());
        //如果全部权限都授权了
        if (needPermissions.size() == 0){

            //参数校验
            Map<Method, Boolean> methodMap = annotationMethodLegalCheck(object,com.wonderful.permissionlibrary.annotation.PermissionResult.class,new Class<?>[]{PermissionResult.class});
            //调用反射方法
            for (Map.Entry<Method, Boolean> entry:methodMap.entrySet()){
                //有参数，参数可以有多个，但是一定要和annotationMethodLegalCheck校验的一致
                if (entry.getValue()){
                    PermissionResult permissionResult = new PermissionResult();
                    permissionResult.addGranted(Arrays.asList(permission.permissions()));
                    invokeAnnotation(object,entry.getKey(),permissionResult);
                }
                //无参数
                else {
                    invokeAnnotation(object,entry.getKey());
                }
            }

            //接口回调结果
            if (object instanceof IPermission){
                IPermission iPermission = (IPermission) object;
                iPermission.granted(Arrays.asList(permission.permissions()));
            }

            //参数校验
            methodMap = annotationMethodLegalCheck(object, PermissionGranted.class,new Class<?>[]{List.class});
            //调用方法
            for (Map.Entry<Method, Boolean> entry:methodMap.entrySet()){
                //有参数，参数可以有多个，但是一定要和annotationMethodLegalCheck校验的一致
                if (entry.getValue()){
                    invokeAnnotation(object,entry.getKey(),Arrays.asList(permission.permissions()));
                }
                //无参数
                else {
                    invokeAnnotation(object,entry.getKey());
                }
            }

            return;
        }
        //有没有授权的权限并且全是勾选了不再提醒的
        else {
            //获取被拒绝了并且没有勾选不再提醒的权限数量
            int count = 0;
            for (String needs:needPermissions){
                if (PermissionUtil.shouldShowRequestPermissionRationale(context,needs)){
                    count++;
                }
            }
            //如果是0并且不是第一次申请权限，说明所有的权限都是勾选了不在提醒的权限，则不在启动权限申请界面而是跳转到系统设置界面(如果开启了此功能的话)
            //测试发现调用系统禁止权限shouldShowRequestPermissionRationale为true
            boolean firstPermissionCheck = MemoryUtil.sharedPreferencesGetBoolean(context,"firstPermissionCheck",true);
            if (firstPermissionCheck){
                MemoryUtil.sharedPreferencesSaveBoolean(context,"firstPermissionCheck",false);
            }
            if (count == 0 && !firstPermissionCheck){
                //启动系统设置界面
                if (enableSettings){
                    PermissionUtil.startSystemPermissionActivity(context);
                }

                //反射回调结果
                //参数校验
                Map<Method, Boolean> methodMap = annotationMethodLegalCheck(object,com.wonderful.permissionlibrary.annotation.PermissionResult.class,new Class<?>[]{PermissionResult.class});
                //调用方法
                for (Map.Entry<Method, Boolean> entry:methodMap.entrySet()){
                    //有参数，参数可以有多个，但是一定要和annotationMethodLegalCheck校验的一致
                    if (entry.getValue()){
                        PermissionResult permissionResult = new PermissionResult();
                        permissionResult.addForbidden(needPermissions);
                        invokeAnnotation(object,entry.getKey(),permissionResult);
                    }
                    //无参数
                    else {
                        invokeAnnotation(object,entry.getKey());
                    }
                }

                //接口回调结果
                if (object instanceof IPermission){
                    IPermission iPermission = (IPermission) object;
                    iPermission.forbidden(needPermissions);
                }

                //参数校验
                methodMap = annotationMethodLegalCheck(object, PermissionForbid.class,new Class<?>[]{List.class});
                //调用方法
                for (Map.Entry<Method, Boolean> entry:methodMap.entrySet()){
                    //有参数，参数可以有多个，但是一定要和annotationMethodLegalCheck校验的一致
                    if (entry.getValue()){
                        invokeAnnotation(object,entry.getKey(),needPermissions);
                    }
                    //无参数
                    else {
                        invokeAnnotation(object,entry.getKey());
                    }
                }

                return;
            }
        }
        //有需要申请的权限,真正的执行权限申请
        //转成数组
        final String[] permissionArray = new String[needPermissions.size()];
        needPermissions.toArray(permissionArray);
        //所有的检查都通过了则将权限申请交给PermissionActivity去完成
        PermissionActivity.permissionRequest(context, permissionArray, new PermissionResultInterface() {
            //结果回调,必须回调给外界的
            @Override
            public void permissionResult(PermissionResult result) {
                //参数校验
                Map<Method, Boolean> methodMap = annotationMethodLegalCheck(object,com.wonderful.permissionlibrary.annotation.PermissionResult.class,new Class<?>[]{PermissionResult.class});
                //调用方法
                for (Map.Entry<Method, Boolean> entry:methodMap.entrySet()){
                    //有参数，参数可以有多个，但是一定要和annotationMethodLegalCheck校验的一致
                    if (entry.getValue()){
                        invokeAnnotation(object,entry.getKey(),result);
                    }
                    //无参数
                    else {
                        invokeAnnotation(object,entry.getKey());
                    }
                }
            }

            @Override
            public void granted(List<String> permission) {
                //接口回调结果
                if (object instanceof IPermission){
                    IPermission iPermission = (IPermission) object;
                    iPermission.granted(permission);
                }

                //参数校验
                Map<Method, Boolean> methodMap = annotationMethodLegalCheck(object, PermissionGranted.class,new Class<?>[]{List.class});
                //调用方法
                for (Map.Entry<Method, Boolean> entry:methodMap.entrySet()){
                    //有参数，参数可以有多个，但是一定要和annotationMethodLegalCheck校验的一致
                    if (entry.getValue()){
                        invokeAnnotation(object,entry.getKey(),permission);
                    }
                    //无参数
                    else {
                        invokeAnnotation(object,entry.getKey());
                    }
                }
            }

            @Override
            public void denied(List<String> permission) {
                //接口回调结果
                if (object instanceof IPermission){
                    IPermission iPermission = (IPermission) object;
                    iPermission.denied(permission);
                }

                //参数校验
                Map<Method, Boolean> methodMap = annotationMethodLegalCheck(object, PermissionDenied.class,new Class<?>[]{List.class});
                //调用方法
                for (Map.Entry<Method, Boolean> entry:methodMap.entrySet()){
                    //有参数，参数可以有多个，但是一定要和annotationMethodLegalCheck校验的一致
                    if (entry.getValue()){
                        invokeAnnotation(object,entry.getKey(),permission);
                    }
                    //无参数
                    else {
                        invokeAnnotation(object,entry.getKey());
                    }
                }
            }

            @Override
            public void forbidden(List<String> permission) {
                //接口回调结果
                if (object instanceof IPermission){
                    IPermission iPermission = (IPermission) object;
                    iPermission.forbidden(permission);
                }

                //参数校验
                Map<Method, Boolean> methodMap = annotationMethodLegalCheck(object, PermissionForbid.class,new Class<?>[]{List.class});
                //调用方法
                for (Map.Entry<Method, Boolean> entry:methodMap.entrySet()){
                    //有参数，参数可以有多个，但是一定要和annotationMethodLegalCheck校验的一致
                    if (entry.getValue()){
                        invokeAnnotation(object,entry.getKey(),permission);
                    }
                    //无参数
                    else {
                        invokeAnnotation(object,entry.getKey());
                    }
                }
            }
        });
    }


    /**
     * 将用户的授权结果回调给申请权限者，通过反射调用对应注解的方法来实现
     * @param object
     * @param method
     * @param parameters
     */
    public void invokeAnnotation(Object object, Method method, Object... parameters){
        try {
            method.invoke(object,parameters);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    /**
     * 注解方法的规范性检查
     * @param object 目标对象
     * @param annotationClass 注解class
     * @param requestParameters 被注解的方法所要求的参数列表
     * @return 返回需要调用的方法，key：需要调用的方法，value:是否有参数
     */
    private Map<Method, Boolean> annotationMethodLegalCheck(Object object, Class<? extends Annotation> annotationClass, Class<?>[] requestParameters){
        Map<Method, Boolean> methodMap = new HashMap<>();
        //遍历找到注解的方法
        Method[] methods = object.getClass().getDeclaredMethods();
        for (Method method:methods){
            method.setAccessible(true);
            //是被注解annotationClass注解的方法
            if (method.isAnnotationPresent(annotationClass)){
                Class<?>[] parameterTypes = method.getParameterTypes();
                //如果方法无任何参数也是默认可以的
                if (parameterTypes.length == 0){
                    methodMap.put(method,false);
                    continue;
                }
                //否则如何和要求的参数数量不一致则抛出异常
                if (parameterTypes.length != requestParameters.length){
                    throwException(annotationClass,method,requestParameters);
                }
                //如如果和要求的参数不能一一对应则抛出异常
                for (int i=0; i<parameterTypes.length; i++){
                    if (!parameterTypes[i].equals(requestParameters[i])){
                        throwException(annotationClass,method,requestParameters);
                    }
                }
                //否则说明是有参数的，加入集合保存
                methodMap.put(method,true);
            }
        }
        return methodMap;
    }

    //抛出异常
    private void throwException(Class<? extends Annotation> annotationClass, Method method, Class<?>[] requestParameters){
        throw new IllegalArgumentException(
                "\n错误：注解"
                + annotationClass.getName()
                + "注解的方法"
                + method.getDeclaringClass().getName()
                + "."
                + method.getName()
                + "定义了错误的参数！！！\n"
                + "正确的参数列表可以为空或者为：\n"
                + classNames(requestParameters)
        );
    }

    private List<String> classNames(Class<?>[] requestParameters){
        List<String> list = new ArrayList<>();
        for (Class<?> clazz:requestParameters){
            list.add(clazz.getName());
        }
        return list;
    }

    @SafeVarargs
    private final <T> void printList(String message, T... content){
        for (T str:content)
        Log.d(TAG, message + ": " + str);
    }


    /**
     * 获取方法的实参列表
     * * AspectJ使用org.aspectj.lang.JoinPoint接口表示目标类连接点对象，如果是环绕增强时，使用org.aspectj.lang.ProceedingJoinPoint表示连接点对象，该类是JoinPoint的子接口。任何一个增强方法都可以通过将第一个入参声明为JoinPoint访问到连接点上下文的信息。我们先来了解一下这两个接口的主要方法：
     * * 1)JoinPoint
     * *    java.lang.Object[] getArgs()：获取连接点方法运行时的入参列表；
     * *    Signature getSignature() ：获取连接点的方法签名对象；
     * *    java.lang.Object getTarget() ：获取连接点所在的目标对象；
     * *    java.lang.Object getThis() ：获取代理对象本身；
     * * 2)ProceedingJoinPoint
     * * ProceedingJoinPoint继承JoinPoint子接口，它新增了两个用于执行连接点方法的方法：
     * *    java.lang.Object proceed() throws java.lang.Throwable：通过反射执行目标对象的连接点处的方法；
     * *    java.lang.Object proceed(java.lang.Object[] args) throws java.lang.Throwable：通过反射执行目标对象连接点处的方法，不过使用新的入参替换原来的入参。
     *
     * @param proceedingJoinPoint
     * @return
     */
    private Object[] getArguments(ProceedingJoinPoint proceedingJoinPoint){
        if (proceedingJoinPoint == null)return null;

        //printList("代理对象" , proceedingJoinPoint.getThis().getClass().getName());
        //printList("连接点所在的目标对象" , proceedingJoinPoint.getTarget().getClass().getName());
        //printList("连接点的方法名" , proceedingJoinPoint.getSignature().getName());

        Object[] objectArray = proceedingJoinPoint.getArgs();

        printList("实参列表" , objectArray);

        return objectArray;
    }
}
