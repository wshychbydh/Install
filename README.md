# Apk自动升级安装

### 功能介绍：

1、适配targetApi小于23及大于23时的文件权限

2、适配7.0以上的文件权限

3、适配8.0以上安装包权限引导设置

4、提供3种下载apk方式（带进度条下载、后台下载、DownloadManager下载）

### 使用方法：

1、在root目录的build.gradle目录中添加
```
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
```

2、在项目的build.gradle中添加依赖
```
    dependencies {
        implementation 'com.github.wshychbydh:install:1.0.0'
    }
```

**注**：

1)、如果编译的时候报重复的'META-INF/app_release.kotlin_module'时，在app的build.gradle文件的android下添加
```
    packagingOptions {
        exclude 'META-INF/app_release.kotlin_module'
    }
```
报其他类似的重复错误时，添加方式同上。

2)、该工具类只提供运行时权限申请，提供7.0以上文件访问权限，无需再额外添加

### 示例：

1、构建参数
```
   DownloadParams.Builder()
        .setDownloadUrl(downloadUrl)                  //(必填) 将要下载的apk网络地址
        .setDownloadPath(downloadPath)                //(可选) apk本地存放地址，默认Environment.DIRECTORY_DOWNLOADS下，若是其他路径需注意权限
        .setVersion(versionCode, versionName)         //(可选) 将要下载的apk版本信息，若不填每次都会重新去下载
        .setDownloadExternalPubDir(dirType, subPath)  //(可选) 使用DownloadManager下载时的路径,默认Environment.DIRECTORY_DOWNLOADS
        .build()

    DialogParams.Builder()
        .backgroundDrawable(drawable)                 //(可选) 进度框背景，默认colorPrimary
        .setCoordinate(x, y)                          //(可选) 进度框现在x,y坐标
        .size(width, height)                          //(可选) 进度框大小，默认260dpx80dp
        .cancelAble(cancelAble)                       //(可选) 进度框是否可按返回键取消，默认false
        .cancelOnTouchOutside(cancelOnTouchOutside)   //(可选) 进度框是否在点击区域外取消，默认false
        .dimAmount(dimAmount)                         //(可选) 进度框出现时，背景灰度，默认0
        .gravity(gravity)                             //(可选) 进度框对齐方式，默认Gravity.Center
        .windowAnim(windowAnim)                       //(可选) 进度框动画，默认无
        .progrees(progress)                           //(可选) 自定义进度框
        .build()

    Params.Builder()
        .setDownloadParams(DownloadParams)            //(必须) 构建下载参数
        .setDialogParams(DialogParams)                //(可选) 构建进度框参数
        .setAuthority(String)                         //(可选) 自定下载路径时，需设置临时授权路径，默认已授权external/Download
        .enableLog(Boolean)                           //(可选) 开启日志打印，默认false
        .useDownloadManager(Boolean)                  //(可选) 后台下载时是否使用DownloadManager下载，默认true
        .forceUpdate(Boolean)                         //(可选) 是否强制升级，默认false
        .build() 
```
2、启动下载 （以下任意一种启动方式即可）
```
    DownloadHelper(context, downloadUrl).start()  
        
    DownloadHelper(context, downloadParams).start()
    
    DownloadHelper(context, dialogParams).start()
    
    DownloadHelper(context, params).start()
```

**注**：如无存储权限时会主动请求权限；若无请求安装包权限时，会弹引导授权安装包提示框


#### 联系方式 wshychbydh@gmail.com
