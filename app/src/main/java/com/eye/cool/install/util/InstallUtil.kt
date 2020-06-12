package com.eye.cool.install.util

import android.annotation.TargetApi
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.WorkerThread
import androidx.core.content.FileProvider
import com.eye.cool.install.DownloadHelper
import com.eye.cool.install.ui.InstallPermissionActivity
import com.eye.cool.install.ui.PermissionActivity
import java.io.File


/**
 *Created by ycb on 2019/11/25 0025
 */
object InstallUtil {

  @WorkerThread
  @JvmStatic
  fun installApk(context: Context, downloadId: Long) {
    val apkPath = queryFileByDownloadId(context, downloadId)
    if (apkPath.isNullOrEmpty()) {
      DownloadLog.logE("The apk file could not be found by downloadId($downloadId)")
      return
    }
    installApk(context, convertPath(apkPath))
  }

  private fun convertPath(url: String): String {
    if (url.startsWith("file://")) {
      return url.replace("file://", "")
    }
    return url
  }

  @JvmStatic
  fun installApk(context: Context, apkPath: String) {
    val file = File(apkPath)
    if (!DownloadUtil.isFileExist(context, file)) {
      DownloadLog.logE("The apk file($apkPath) doest not exist or can not access!")
      return
    }
    installApk(context, file)
  }

  @JvmStatic
  fun installApk(context: Context, apkFile: File) {
    when {
      Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
        installAboveO(context, apkFile)
      }
      Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
        installBetweenNAndO(context, apkFile)
      }
      else -> {
        installBelowN(context, Uri.fromFile(apkFile))
      }
    }
  }

  @TargetApi(Build.VERSION_CODES.O)
  private fun installAboveO(context: Context, apkFile: File) {
    val hasPermission = context.packageManager.canRequestPackageInstalls()
    if (hasPermission) {
      installBetweenNAndO(context, apkFile)
    } else {
      InstallPermissionActivity.requestInstallPermission(context) {
        if (it) {
          installBetweenNAndO(context, apkFile)
        } else {
          DownloadLog.logE("Install failed, because of the permission of 'android.permission.REQUEST_INSTALL_PACKAGES' is denied!")
        }
      }
    }
  }

  private fun installBetweenNAndO(context: Context, apkFile: File) {
    DownloadLog.logI("Install apk file-->${apkFile.absolutePath}")
    val intent = Intent(Intent.ACTION_VIEW)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    val authority = DownloadHelper.authority ?: "${context.packageName}.apk.FileProvider"
    val uri = FileProvider.getUriForFile(context, authority, apkFile)
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    intent.setDataAndType(uri, "application/vnd.android.package-archive")
    context.startActivity(intent)
  }

  private fun installBelowN(context: Context, uri: Uri) {
    DownloadLog.logI("Install apk file-->${uri.path}")
    val intent = Intent(Intent.ACTION_VIEW)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.setDataAndType(uri, "application/vnd.android.package-archive")
    context.startActivity(intent)
  }

  @WorkerThread
  @JvmStatic
  fun queryFileByDownloadId(context: Context, downloadId: Long): String? {
    val path = queryPathByDownloadId(context, downloadId)
    if (path.isNullOrEmpty()) return queryFilePathByDownloadId(context, downloadId)

    if (DownloadUtil.isFileExist(context, convertPath(path))) {
      return path
    }
    return queryFilePathByDownloadId(context, downloadId)
  }

  private fun queryFilePathByDownloadId(context: Context, downloadId: Long): String? {
    if (downloadId < 0) return null
    val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val uri = manager.getUriForDownloadedFile(downloadId)
    val projection = arrayOf(MediaStore.Downloads.DATA)
    val cr = context.contentResolver.query(uri, projection, null, null, null) ?: return null
    cr.use { c ->
      if (c.moveToFirst()) {
        val index = c.getColumnIndexOrThrow(MediaStore.Downloads.DATA)
        if (index > -1) {
          return c.getString(index)
        }
      }
    }
    return null
  }

  private fun queryPathByDownloadId(context: Context, downloadId: Long): String? {
    val downloader = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    if (downloadId != -1L) {
      val query = DownloadManager.Query()
      query.setFilterById(downloadId)
      query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL)
      val cur: Cursor = downloader.query(query) ?: return null
      cur.use { c ->
        if (c.moveToFirst()) {
          return c.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
        }
      }
    }
    return null
  }
}