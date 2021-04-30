package com.eye.cool.install.util

import android.annotation.TargetApi
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetFileDescriptor
import android.net.Uri
import android.os.Build
import com.eye.cool.install.params.FileParams
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Created by ycb on 2019/11/28
 */
object DownloadUtil {

  internal fun checkFileDownload(context: Context, file: File, params: FileParams): Boolean {
    if (isFileExist(context, file)) {
      val result = checkFileAttr(context, file, params)
      if (result) {
        return true
      } else {
        file.delete()
      }
    }
    return false
  }

  private fun checkFileAttr(context: Context, file: File, params: FileParams): Boolean {

    if (params.md5 != null) {
      val md5 = MD5.getFileMD5(file)
      DownloadLog.logI("new file md5:${params.md5}; old file md5:$md5")

      return md5 == params.md5
    }

    if (params.isApk) {
      return isApkDownload(context, file, params)
    }

    if (params.length != null) {
      val len = file.length()
      DownloadLog.logI("new file length:${params.length}; old file length:$len")
      return params.length == len
    }

    return false
  }

  private fun isApkDownload(context: Context, file: File, params: FileParams): Boolean {
    if (params.versionCode <= 0 || params.versionName.isNullOrEmpty()) return false
    try {
      val packageManager = context.packageManager
      val packageInfo = packageManager.getPackageArchiveInfo(file.absolutePath, PackageManager.GET_ACTIVITIES)
      if (packageInfo != null) {
        DownloadLog.logI("old apk-->packageName:${packageInfo.packageName}; " +
            "versionCode:${packageInfo.versionCode}; versionName:${packageInfo.versionName}")
        DownloadLog.logI("new apk-->packageName:${context.packageName}; " +
            "versionCode:${params.versionCode}; versionName:${params.versionName}")
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
