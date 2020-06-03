# Apk自动升级安装

适配6.0，7.0，8.0

### 功能介绍：

1、适配targetApi小于23及大于23时的文件权限

2、适配7.0以上的文件权限

3、适配8.0安装包权限引导设置

4、支持自定义升级提示框

5、支持自定义权限请求和引导授权安装框

6、提供3种下载apk方式（带进度条下载、后台下载、DownloadManager下载）

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
        implementation 'com.github.wshychbydh:install:1.0.8'
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

3)、9.0的部分机型使用DownloadManager可能会提示文件路径无效，请使用另外两种下载方式，设置Params.useDownloadManager(false)

### 示例：

1、构建参数
```
   val promptParams = PromptParams.Builder()           
        .setCoordinate(x, y)                          //(可选) 提示框显示的x,y坐标
        .size(width, height)                          //(可选) 提示框大小，默认80%屏宽
        .cancelAble(cancelAble)                       //(可选) 提示框是否可按返回键取消，默认false
        .cancelOnTouchOutside(cancelOnTouchOutside)   //(可选) 提示框是否在点击区域外取消，默认false
        .dimAmount(dimAmount)                         //(可选) 提示框出现时，背景灰度，默认0
        .gravity(gravity)                             //(可选) 提示框对齐方式，默认Gravity.Center
        .windowAnim(windowAnim)                       //(可选) 提示框window动画，默认无
        .setTitle(title)                              //(可选) 提示框标题
        .setContent(content)                          //(可选) 提示框内容（内容和标题需至少有一个，否则不提示）
        .setPrompt(prompt)                            //(可选) 自定义提示框
        .build() 

   val downloadParams = DownloadParams.Builder()
        .setDownloadUrl(downloadUrl)                  //(必填) 将要下载的apk网络地址
        .setDownloadPath(downloadPath)                //(可选) apk本地存放地址，默认Environment.DIRECTORY_DOWNLOADS下，若是其他路径可能需添加相应权限
        .setVersion(versionCode, versionName)         //(可选) 将要下载的apk版本信息，若不填每次都会重新去下载
        .setDownloadExternalPubDir(dirType, subPath)  //(可选) 使用DownloadManager下载时的路径,默认Environment.DIRECTORY_DOWNLOADS
        .build()

   val progressParams = ProgressParams.Builder()
        .setCoordinate(x, y)                          //(可选) 显示的x,y坐标
        .size(width, height)                          //(可选) 进度框大小，默认100%屏宽
        .cancelAble(cancelAble)                       //(可选) 是否可取消，但不会取消任务，默认false
        .cancelOnTouchOutside(cancelOnTouchOutside)   //(可选) 是否在点击区域外取消，不会取消任务，默认false
        .dimAmount(dimAmount)                         //(可选) 进度框出现时，背景灰度，默认0
        .gravity(gravity)                             //(可选) 对齐方式，默认Gravity.Center
        .windowAnim(windowAnim)                       //(可选) 进度框window动画，默认无
        .progress(progress)                           //(可选) 自定义进度框
        .build()

   val params = Params.Builder()
        .setDownloadParams(downloadParams)            //(必填) 构建下载参数
        .setProgressParams(progressParams)            //(可选) 构建进度框参数
        .setPromptParams(promptParams)                //(可选) 构建提示框参数
        .setAuthority(String)                         //(可选) 自定下载路径时，需设置临时授权路径，默认已授权external/Download
        .enableLog(Boolean)                           //(可选) 开启日志打印，默认false
        .useDownloadManager(Boolean)                  //(可选) 后台下载时是否使用DownloadManager下载，默认true
        .forceUpdate(Boolean)                         //(可选) 是否强制升级，默认false
        .setPermissionInvoker(PermissionInvoker)      //(可选) 自定义请求存储权限
        .setSettingInvoker(SettingInvoker)            //(可选) 自定义引导安装未知应用权限设置框
        .build() 
```
2、启动下载 （以下任意一种启动方式即可）
```
    DownloadHelper(context, downloadUrl).start()  
        
    DownloadHelper(context, downloadParams).start()
    
    DownloadHelper(context, progressParams).start()
    
    DownloadHelper(context, params).start()
```

**注**：如无存储权限时会主动请求权限；若无请求安装包权限时，会弹引导授权安装包提示框


#####   
 
**Demo地址：(https://github.com/wshychbydh/SampleDemo)**    
    
##

###### **欢迎fork，更希望你能贡献commit.** (*￣︶￣)    

###### 联系方式 wshychbydh@gmail.com

[![](https://jitpack.io/v/wshychbydh/install.svg)](https://jitpack.io/#wshychbydh/install)
