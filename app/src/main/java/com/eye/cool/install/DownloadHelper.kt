package com.eye.cool.install

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.webkit.URLUtil
import com.eye.cool.install.params.DownloadParams
import com.eye.cool.install.params.Params
import com.eye.cool.install.params.ProgressParams
import com.eye.cool.install.support.DownloadReceiver
import com.eye.cool.install.support.DownloadService
import com.eye.cool.install.support.IPromptListener
import com.eye.cool.install.ui.PermissionActivity
import com.eye.cool.install.ui.ProgressDialog
import com.eye.cool.install.ui.PromptDialog
import com.eye.cool.install.util.DownloadLog
import com.eye.cool.install.util.DownloadUtil
import com.eye.cool.install.util.InstallUtil
import java.io.File

class DownloadHelper {

  private var context: Context
  private var params: Params = Params.Builder().build()

  constructor(context: Context, downloadUrl: String) {
    this.context = context
    params.downloadParams.downloadUrl = downloadUrl
  }

  constructor(context: Context, downloadParams: DownloadParams) {
    this.context = context
    this.params.downloadParams = downloadParams
  }

  constructor(context: Context, progressParams: ProgressParams) {
    this.context = context
    this.params.progressParams = progressParams
  }

  constructor(context: Context, params: Params) {
    this.context = context
    this.params = params
    authority = params.authority
    DownloadLog.enableLog = params.enableLog
  }

  fun start() {

    if (!checkParams()) return

    tryShowPrompt()
  }

  private fun checkParams(): Boolean {
    if (!URLUtil.isValidUrl(params.downloadParams.downloadUrl)) {
      DownloadLog.logE("Download url(${params.downloadParams.downloadUrl}) is invalid..")
      return false
    }
    if (!params.useDownloadManager || params.forceUpdate) {
      if (params.downloadParams.downloadPath.isNullOrEmpty()) {
        try {
          params.downloadParams.downloadPath = composeDownloadPath()
        } catch (e: Exception) {
          DownloadLog.logE(e.message ?: "")
          return false
        }
      } else {
        var file = File(params.downloadParams.downloadPath!!)
        if (file.isFile) {
          file = file.parentFile
        }
        if (file == null || (!file.exists() && !file.mkdirs()) || !file.canRead() || !file.canWrite()) {
          DownloadLog.logE("The file directory(${file.absolutePath}) is unavailable or inaccessible")
          return false
        }
        if (file.isDirectory) {
          params.downloadParams.downloadPath = File(file, composeDownloadSubPath()).absolutePath
        }
      }
    }
    if (params.downloadParams.downloadSubPath.isNullOrEmpty()) {
      params.downloadParams.downloadSubPath = composeDownloadSubPath()
    }
    return true
  }

  private fun startOnPermissionGranted() {
    val target = context.applicationInfo.targetSdkVersion
    if (target >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      checkPermission { result ->
        if (result) {
          if (params.forceUpdate) {
            if (DownloadUtil.checkApkDownload(context, params.downloadParams)) {
              DownloadLog.logI("Apk is download!")
              InstallUtil.installApk(context, params.downloadParams.downloadPath!!)
            } else {
              ProgressDialog.show(context, params)
            }
          } else {
            download(context, params)
          }
        } else {
          DownloadLog.logE("Download failed, permission denied.")
        }
      }
    } else {
      //UseDownloadManager check it later, download path's accessibility is checked
      if (params.forceUpdate) {
        ProgressDialog.show(context, params)
      } else {
        download(context, params)
      }
    }
  }

  private fun checkPermission(invoker: (Boolean) -> Unit) {

    val permissions = arrayOf(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    if (params.permissionInvoker == null) {
      PermissionActivity.requestPermission(context, permissions) {
        if (it) {
          checkInstallPermission(invoker)
        } else {
          invoker.invoke(false)
        }
      }
    } else {
      params.permissionInvoker!!.request(permissions) {
        if (it) {
          checkInstallPermission(invoker)
        } else {
          invoker.invoke(false)
        }
      }
    }
  }

  private fun checkInstallPermission(invoker: (Boolean) -> Unit) {
    if (params.settingInvoker != null
        && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        && !context.packageManager.canRequestPackageInstalls()) {
      params.settingInvoker!!.request {
        if (it) {
          invoker.invoke(true)
        } else {
          DownloadLog.logE("Download Failed, missing installation unknown package permissions.")
        }
      }
    } else {
      invoker.invoke(true)
    }
  }

  private fun tryShowPrompt() {
    if (params.promptParams?.isValid() == true) {
      PromptDialog.show(context, params.promptParams!!, object : IPromptListener {
        override fun onCancel() {
          DownloadLog.logI("Download canceled")
        }

        override fun onUpgrade() {
          startOnPermissionGranted()
        }
      })
    } else {
      startOnPermissionGranted()
    }
  }

  private fun download(context: Context, params: Params) {
    if (params.useDownloadManager) {
      try {
        var fileDir: File? = null
        var pubDir: File? = Environment.getExternalStoragePublicDirectory(params.downloadParams.downloadDirType)
        if (pubDir == null || (!pubDir.exists() && !pubDir.mkdirs()) || !pubDir.canRead() || !pubDir.canWrite()) {
          fileDir = context.getExternalFilesDir(params.downloadParams.downloadDirType)
          if (fileDir == null || (!fileDir.exists() && !fileDir.mkdirs()) || !fileDir.canRead() || !fileDir.canWrite()) {
            "The file directory(${pubDir?.absolutePath} or ${fileDir?.absolutePath}) are unavailable or inaccessible"
            return
          }
        }
        val file = File(fileDir ?: pubDir, params.downloadParams.downloadSubPath)
        if (DownloadUtil.checkApkDownload(context, file, params.downloadParams)) {
          DownloadLog.logI("Apk is download!")
          InstallUtil.installApk(context, params.downloadParams.downloadPath!!)
        } else {
          DownloadLog.logI("Download by DownloadManager...")
          val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
          val request = DownloadManager.Request(Uri.parse(params.downloadParams.downloadUrl))
          if (fileDir == null) {
            request.setDestinationInExternalPublicDir(params.downloadParams.downloadDirType, params.downloadParams.downloadSubPath)
          } else {
            request.setDestinationInExternalFilesDir(context, params.downloadParams.downloadDirType, params.downloadParams.downloadSubPath)
          }

          request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
          val downloadId = manager.enqueue(request)
          context.getSharedPreferences(DownloadReceiver.DOWNLOAD, Context.MODE_PRIVATE).edit().putLong(DownloadReceiver.DOWNLOAD_ID, downloadId).commit()
        }
      } catch (e: Exception) {
        e.printStackTrace()
        DownloadLog.logE(e.message ?: "")
      }
    } else {
      if (DownloadUtil.checkApkDownload(context, params.downloadParams)) {
        DownloadLog.logI("Apk is download!")
        InstallUtil.installApk(context, params.downloadParams.downloadPath!!)
        return
      }
      val intent = Intent(context, DownloadService::class.java)
      intent.putExtra(DownloadService.DOWNLOAD_URL, params.downloadParams.downloadUrl)
      intent.putExtra(DownloadService.FILE_PATH, params.downloadParams.downloadPath)
      context.startService(intent)
    }
  }

  private fun composeDownloadPath(): String {
    val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        ?: Environment.getDownloadCacheDirectory()
    if (dir == null || (!dir.exists() && !dir.mkdirs()) || !dir.canRead() || !dir.canWrite()) {
      throw IllegalStateException("The file directory(${dir.absolutePath}) is unavailable or inaccessible")
    }
    return File(dir, composeDownloadSubPath()).absolutePath
  }

  private fun composeDownloadSubPath(): String {
    val appInfo = context.packageManager.getApplicationInfo(context.packageName, 0)
        ?: return "install.apk"
    val name = context.packageManager.getApplicationLabel(appInfo) as? String ?: "install"
    return "$name.apk"
  }

  companion object {

    internal var authority: String? = null

  }
}