package com.eye.cool.install.util

import android.annotation.TargetApi
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetFileDescriptor
import android.net.Uri
import android.os.Build
import com.eye.cool.install.params.DownloadParams
import java.io.File
import java.io.FileNotFoundException

/**
 * Created by ycb on 2019/11/28
 */
object DownloadUtil {

  internal fun checkApkDownload(context: Context, params: DownloadParams): Boolean {
    val file = File(params.downloadPath ?: return false)
    if (isFileExist(context, file)) {
      val result = isApkDownload(context, file, params)
      if (result) {
        return true
      } else {
        file.delete()
      }
    }
    return false
  }

  internal fun checkApkDownload(context: Context, file: File, params: DownloadParams): Boolean {
    if (isFileExist(context, file)) {
      val result = isApkDownload(context, file, params)
      if (result) {
        return true
      } else {
        file.delete()
      }
    }
    return false
  }

  private fun isApkDownload(context: Context, file: File, params: DownloadParams): Boolean {
    if (params.versionCode <= 0 || params.versionName.isNullOrEmpty()) return false
    try {
      val packageManager = context.packageManager
      val packageInfo = packageManager.getPackageArchiveInfo(file.absolutePath, PackageManager.GET_ACTIVITIES)
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

  @JvmStatic
  fun toDownloadPage(context: Context) {
    val intent = Intent(DownloadManager.ACTION_VIEW_DOWNLOADS)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    context.startActivity(intent)
  }

  @JvmStatic
  fun isFileExist(context: Context, path: String?): Boolean {
    if (path.isNullOrEmpty()) return false
    return isFileExist(context, File(path))
  }

  @JvmStatic
  fun isFileExist(context: Context, file: File): Boolean {
    val exist = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      isFileExistsAboveQ(context, file)
    } else {
      file.exists()
    } && file.length() > 1024
    DownloadLog.logI("File-->${file.absolutePath}; exist:$exist; length:${file.length()}")
    return exist
  }

  @TargetApi(Build.VERSION_CODES.Q)
  private fun isFileExistsAboveQ(context: Context, file: File): Boolean {
    var afd: AssetFileDescriptor? = null
    val cr = context.contentResolver
    return try {
      val afd = cr.openAssetFileDescriptor(Uri.fromFile(file), "r")
      if (afd == null) {
        false
      } else {
        afd.close()
        true
      }
    } catch (e: FileNotFoundException) {
      DownloadLog.logE(e.message ?: "")
      false
    } finally {
      afd?.close()
    }
  }
}
