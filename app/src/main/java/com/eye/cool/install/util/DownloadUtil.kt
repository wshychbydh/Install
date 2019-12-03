package com.eye.cool.install.util

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.eye.cool.install.params.DownloadParams
import java.io.File

/**
 * Created by ycb on 2019/11/28
 */
internal object DownloadUtil {

  fun checkApkDownload(context: Context, params: DownloadParams): Boolean {
    val file = File(params.downloadPath ?: "")
    DownloadLog.logI("download path-->${file.absolutePath}")
    if (file.exists()) {
      val result = isApkDownload(context, params)
      if (result) {
        return true
      } else {
        file.delete()
      }
    }
    return false
  }

  fun checkApkDownload(context: Context, file: File, params: DownloadParams): Boolean {
    if (file.exists()) {
      val result = isApkDownload(context, params)
      if (result) {
        return true
      } else {
        file.delete()
      }
    }
    return false
  }

  @JvmStatic
  private fun isApkDownload(context: Context, params: DownloadParams): Boolean {
    if (params.versionCode <= 0 || params.versionName.isNullOrEmpty()) return false
    try {
      val packageManager = context.packageManager
      val packageInfo = packageManager.getPackageArchiveInfo(params.downloadPath, PackageManager.GET_ACTIVITIES)
      if (packageInfo != null) {
        DownloadLog.logI("old download apk-->${packageInfo.packageName} : ${packageInfo.versionCode} ; ${packageInfo.versionName}")
        DownloadLog.logI("new download apk-->${context.packageName} : ${params.versionCode} ; ${params.versionName}")
        return packageInfo.versionCode == params.versionCode
            && packageInfo.versionName == params.versionName
            && packageInfo.packageName == context.packageName
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return false
  }

  fun toDownloadPage(context: Context) {
    val intent = Intent(DownloadManager.ACTION_VIEW_DOWNLOADS)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    context.startActivity(intent)
  }
}
