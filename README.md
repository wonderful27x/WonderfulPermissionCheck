# WonderfulPermissionCheck  
An android permission check project using aspectJ  
一个使用AspectJ的android权限申请框架  

# 如何使用:  
## 1.在根目录的build.gradle添加aspectj和jitpack仓库配置  
buildscript {  
    ...  
    dependencies {  
        ...  
        // AspectJ使用  
        classpath 'org.aspectj:aspectjtools:1.8.9'  
        classpath 'org.aspectj:aspectjweaver:1.8.9'  
    }  
}  

allprojects {
    repositories {
        google()
        jcenter()
        maven { url 'https://jitpack.io' }
    }
}

## 2.在要使用的module根目录创建name.gradle,注意如果是library则variants需要改成variants = project.android.libraryVariants  
===========================name.gradle=============================  
// AspectJ使用  
// 版本界限：As-3.0.1 + gradle4.4-all （需要配置r17的NDK环境）  
// 或者：As-3.2.1 + gradle4.6-all （正常使用，无警告）  
import org.aspectj.bridge.IMessage  
import org.aspectj.bridge.MessageHandler  
import org.aspectj.tools.ajc.Main  

final def log = project.logger  
final def variants = project.android.applicationVariants  

// AspectJ使用  
// 版本界限：As-3.0.1 + gradle4.4-all （需要配置r17的NDK环境）  
// 或者：As-3.2.1 + gradle4.6-all （正常使用，无警告）  
// 编译时用Aspect专门的编译器，不再使用传统的javac  
buildscript {  
    repositories {  
        mavenCentral()  
    }  
    dependencies {  
        classpath 'org.aspectj:aspectjtools:1.8.9'  
        classpath 'org.aspectj:aspectjweaver:1.8.9'  
    }  
}  

variants.all { variant ->  
    if (!variant.buildType.isDebuggable()) {  
        log.debug("Skipping non-debuggable build type '${variant.buildType.name}'.")  
        //TODO do not return or it calls an error!!!  
    }  

    JavaCompile javaCompile = variant.javaCompile  
    javaCompile.doLast {  
        String[] args = ["-showWeaveInfo",  
                         "-1.8",  
                         "-inpath", javaCompile.destinationDir.toString(),  
                         "-aspectpath", javaCompile.classpath.asPath,  
                         "-d", javaCompile.destinationDir.toString(),  
                         "-classpath", javaCompile.classpath.asPath,  
                         "-bootclasspath", project.android.bootClasspath.join(File.pathSeparator)]  
        log.debug "ajc args: " + Arrays.toString(args)  

        MessageHandler handler = new MessageHandler(true);  
        new Main().run(args, handler);  
        for (IMessage message : handler.getMessages(null, true)) {  
            switch (message.getKind()) {  
                case IMessage.ABORT:  
                case IMessage.ERROR:  
                case IMessage.FAIL:  
                    log.error message.message, message.thrown  
                    break;  
                case IMessage.WARNING:  
                    log.warn message.message, message.thrown  
                    break;  
                case IMessage.INFO:  
                    log.info message.message, message.thrown  
                    break;  
                case IMessage.DEBUG:  
                    log.debug message.message, message.thrown  
                    break;  
            }  
        }  
    }  
}  
===========================name.gradle=============================  

## 3.在module的build.gradle中引入name.gradle文件,并在dependencies中引入依赖  
...  
apply from: "aspectJ_application.gradle"  
dependencies {  
    ...  
    implementation 'com.github.wonderful27x:WonderfulPermissionCheck:v1.2.0'  
}  

## 4.通过注解来申请权限和获取授权结果  
![demo](https://github.com/wonderful27x/WonderfulPermissionCheck/tree/master/app/src/main/java/com/wonderful/wonderfulpermissioncheck)  
