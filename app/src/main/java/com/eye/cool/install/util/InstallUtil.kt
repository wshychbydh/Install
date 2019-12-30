package com.eye.cool.install.util

import android.annotation.TargetApi
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.eye.cool.install.DownloadHelper
import com.eye.cool.install.ui.PermissionActivity
import java.io.File


/**
 *Created by ycb on 2019/11/25 0025
 */
internal object InstallUtil {

  fun installApk(context: Context, downloadId: Long) {
    val apkPath = queryPathByDownloadId(context, downloadId)
    if (apkPath.isNullOrEmpty()) {
      DownloadLog.logE("The apk path doest not exist!")
      return
    }
    installApk(context, apkPath)
  }

  fun installApk(context: Context, apkPath: String) {
    val file = File(apkPath)
    if (!DownloadUtil.isFileExist(context, file)) {
      DownloadLog.logE("The apk file doest not exist!")
      return
    }
    installApk(context, file)
  }

  private fun installApk(context: Context, apkFile: File) {
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
      PermissionActivity.requestInstall(context) {
        if (it) {
          installBetweenNAndO(context, apkFile)
        } else {
          DownloadLog.logE("Install failed, because of the permission of 'android.permission.REQUEST_INSTALL_PACKAGES' is denied!")
        }
      }
    }
  }

  private fun installBetweenNAndO(context: Context, apkFile: File) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    val authority = DownloadHelper.authority ?: "${context.packageName}.apk.FileProvider"
    val uri = FileProvider.getUriForFile(context, authority, apkFile)
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    intent.setDataAndType(uri, "application/vnd.android.package-archive")
    context.startActivity(intent)
  }

  private fun installBelowN(context: Context, uri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.setDataAndType(uri, "application/vnd.android.package-archive")
    context.startActivity(intent)
  }

  private fun queryApkPathByDownloadId(context: Context, downloadId: Long): String? {
    if (downloadId < 0) return null
    val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val uri = manager.getUriForDownloadedFile(downloadId)
    val projection = arrayOf(MediaStore.Downloads.DATA)
    val cr = context.contentResolver.query(uri, projection, null, null, null) ?: return null
    cr.use { cr ->
      if (cr.moveToFirst()) {
        val index = cr.getColumnIndexOrThrow(MediaStore.Downloads.DATA)
        if (index > -1) {
          return cr.getString(index)
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
      cur.use { cur ->
        if (cur.moveToFirst()) {
          return cur?.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
        }
      }
    }
    return null
  }
}