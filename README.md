# Apk自动升级安装

适配至11.0

### 功能介绍：

1、适配6.0运行时权限，7.0文件权限，8.0安装包权限，11.0文件管理所有权权限

2、支持自定义升级提示框

3、支持自定义权限请求和引导授权安装框

4、提供3种下载apk方式（带进度条下载、后台服务下载、DownloadManager下载）

5、已下载文件可配置不重复下载，也可以用于普通文件的下载

6、支持自定义DownloadManger.Request

7、解决使用DownloadManager下载中文名的文件，导致查询到的文件路径无效

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
        implementation 'com.github.wshychbydh:install:tag'
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

2)、若无存储权限会主动请求权限；若无请求安装包权限时，会弹引导授权安装包提示框

3)、提供7.0及以上默认路径(Download)的文件访问权限，其他路径需自行添加访问权限

### 示例：

1、构建参数
```
   val promptParams = PromptParams.Builder()           
        .coordinate(x, y)                             //(可选) 提示框显示的x,y坐标
        .size(width, height)                          //(可选) 提示框大小，默认100%屏宽
        .cancelAble(cancelAble)                       //(可选) 提示框是否可按返回键取消，默认false
        .cancelOnTouchOutside(cancelOnTouchOutside)   //(可选) 提示框是否在点击区域外取消，默认false
        .dimAmount(dimAmount)                         //(可选) 提示框出现时，背景灰度，默认0
        .gravity(gravity)                             //(可选) 提示框对齐方式，默认Gravity.Center
        .windowAnim(windowAnim)                       //(可选) 提示框window动画，默认无
        .title(title)                                 //(可选) 提示框标题
        .content(content)                             //(可选) 提示框内容（内容和标题需至少有一个，否则不提示）
        .prompt(prompt)                               //(可选) 自定义提示框
        .build() 

   val downloadParams = DownloadParams.Builder()
        .downloadUrl(downloadUrl)                     //(必填) 将要下载的apk网络地址
        .request(DownloadManager.Request)             //(可选) 自定义DownloadManger.Request
        .useDownloadManager(Boolean)                  //(可选) 后台下载时是否使用DownloadManager下载，默认true
        .forceUpdate(Boolean)                         //(可选) 是否强制升级，默认false
        .repeatDownload(Boolean)                      //(可选) 是否重复下载，默认false
        .build()

   //当downloadParams.repeateDownlaod为false的时候配置不重复下载条件
   val fileParams = FileParams.Builder()
        .isApk(isApk)                                 //(可选) 下载的文件是否为apk，默认true，若是apk将会触发自动安装
        .version(versionCode, versionName)            //(可选) 将要下载的apk版本信息
        .length(versionCode, versionName)             //(可选) 将要下载的文件长度，若与已下载的文件长度一致将不重复下载
        .md5(versionCode, versionName)                //(可选) 将要下载的文件md5值，若与已下载的文件md5一致将不重复下载
        .build()

   val progressParams = ProgressParams.Builder()
        .coordinate(x, y)                             //(可选) 显示的x,y坐标
        .size(width, height)                          //(可选) 进度框大小，默认100%屏宽
        .cancelAble(cancelAble)                       //(可选) 是否可取消，但不会取消任务，默认false
        .cancelOnTouchOutside(cancelOnTouchOutside)   //(可选) 是否在点击区域外取消，不会取消任务，默认false
        .dimAmount(dimAmount)                         //(可选) 进度框出现时，背景灰度，默认0
        .gravity(gravity)                             //(可选) 对齐方式，默认Gravity.Center
        .windowAnim(windowAnim)                       //(可选) 进度框window动画，默认无
        .progressListener(listener)                   //(可选) 监听下载
        .progressView(view)                           //(可选) 自定义view
        .progressTimeout(Long)                        //(可选) 进度框加载超时时长，超时后不会取消下载任务，默认10分钟
        .build()

   //当系统版本>=8.0时，将后台服务变为前台服务
   val notifyParams = NotifyParams.Builder()
        .notifyId(Int)                             //(可选) 通知id
        .channelId(String)                         //(可选) 通知渠道id
        .notificationChannel(width, height)        //(可选) 自定义通知通道
        .notification(cancelAble)                  //(可选) 自定义通知
        .build()

   val params = Params.Builder()
        .downloadParams(downloadParams)            //(必填) 构建下载参数
        .progressParams(progressParams)            //(可选) 构建进度框参数
        .promptParams(promptParams)                //(可选) 构建提示框参数
        .fileParams(promptParams)                  //(可选) 构建下载文件参数
        .notifyParams(promptParams)                //(可选) 构建通知参数
        .authority(String)                         //(可选) 自定下载路径时，需设置临时授权路径，默认已授权external/Download
        .enableLog(Boolean)                        //(可选) 开启日志打印，默认false
        .logTag(String)                            //(可选) 自定义日志标识，默认download
        .permissionInvoker(PermissionInvoker)      //(可选) 自定义存储权限请求
        .installPermissionInvoker(invoker)         //(可选) 自定义安装包权限请求
        .settingInvoker(SettingInvoker)            //(可选) 自定义引导安装未知应用权限设置框
        .build() 
```
2、启动下载 （以下任意一种启动方式即可）
```
    DownloadHelper(context, downloadUrl).start()  
        
    DownloadHelper(context, downloadParams).start()
    
    DownloadHelper(context, progressParams).start()
    
    DownloadHelper(context, params).start()
```


#####   
 
**Demo地址：(https://github.com/wshychbydh/SampleDemo)**    
    
##

###### **欢迎fork，更希望你能贡献commit.** (*￣︶￣)    

###### 联系方式 wshychbydh@gmail.com

[![](https://jitpack.io/v/wshychbydh/install.svg)](https://jitpack.io/#wshychbydh/install)
