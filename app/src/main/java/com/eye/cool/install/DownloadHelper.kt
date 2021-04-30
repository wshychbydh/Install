package com.eye.cool.install

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.webkit.URLUtil
import androidx.core.content.ContextCompat
import com.eye.cool.install.params.DownloadParams
import com.eye.cool.install.params.Params
import com.eye.cool.install.params.ProgressParams
import com.eye.cool.install.support.DownloadInfo
import com.eye.cool.install.support.DownloadService
import com.eye.cool.install.support.SharedHelper
import com.eye.cool.install.support.complete
import com.eye.cool.install.ui.*
import com.eye.cool.install.util.DownloadLog
import com.eye.cool.install.util.DownloadUtil
import com.eye.cool.install.util.InstallUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

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
    DownloadLog.tag = params.logTag
    DownloadLog.enableLog = params.enableLog
  }

  fun start() {
    GlobalScope.launch {

      if (!checkParams()) return@launch

      if (!tryShowPrompt()) {
        DownloadLog.logI("Download canceled")
        return@launch
      }

      if (checkInstallPermission()) {
        download()
      } else {
        DownloadLog.logE("Download failed, install_package permission denied.")
      }
    }
  }

  private fun checkParams(): Boolean {
    val downloadParams = params.downloadParams
    if (!URLUtil.isValidUrl(downloadParams.downloadUrl)) {
      DownloadLog.logE("Download url(${downloadParams.downloadUrl}) is invalid..")
      return false
    }

    if (downloadParams.forceDownload && DownloadProgressDialog.sParams != null) {
      DownloadLog.logE("Only one mandatory download task is supported")
      return false
    }

    return true
  }

  private suspend fun checkInstallPermission(): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

      if (params.fileParams.isApk) {

        if (context.packageManager.canRequestPackageInstalls()) return true

        return if (params.installPermissionInvoker == null) {
          InstallPermissionActivity.requestInstallPermission(context)
        } else {
          requestInstallPermission()
        }
      }
    }
    return true
  }

  private suspend fun requestInstallPermission() = suspendCancellableCoroutine<Boolean> {
    params.installPermissionInvoker!!.request { result ->
      it.complete(result)
    }
  }

  private suspend fun tryShowPrompt(): Boolean {
    return if (params.promptParams?.isValid() == true) {
      PromptDialog.show(context, params.promptParams!!)
    } else {
      true
    }
  }

  private suspend fun download() {
    val downloadParams = params.downloadParams
    if (!downloadParams.repeatDownload) {
      val downloadFile = downloadParams.composeDownloadFile(context, params.fileParams.isApk)
      val download = DownloadUtil.checkFileDownload(context, downloadFile, params.fileParams)
      if (download) {
        DownloadLog.logI("File is download!")
        if (params.fileParams.isApk) {
          InstallUtil.installApk(context, downloadFile)
        }
        return
      }
    }

    if (downloadParams.forceDownload && DownloadProgressDialog.sParams != null) {
      DownloadLog.logE("Only one mandatory download task is supported")
      return
    }

    if (downloadParams.useDownloadManager) {
      val result = requestReadPermission()
      if (result) {
        downloadByDownloadManager()
      } else {
        DownloadLog.logE("Download failed, read_storage permission denied.")
      }
    } else {
      if (downloadParams.forceDownload) {
        DownloadProgressDialog.show(context, params)
      } else {
        downloadByService()
      }
    }
  }

  private fun downloadByService() {
    val downloadParams = params.downloadParams
    val intent = Intent(context, DownloadService::class.java)
    intent.putExtra(DownloadService.DOWNLOAD_URL, downloadParams.downloadUrl)
    intent.putExtra(DownloadService.FILE_PATH,
        downloadParams.composeDownloadFile(context, params.fileParams.isApk).absolutePath)
    intent.putExtra(DownloadService.IS_APK, params.fileParams.isApk)
    intent.putExtra(DownloadService.NOTIFY_PARAMS, params.notifyParams)
    ContextCompat.startForegroundService(context, intent)
  }

  private suspend fun requestReadPermission() = suspendCancellableCoroutine<Boolean> {

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      it.complete(true)
      return@suspendCancellableCoroutine
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()) {
      it.complete(true)
      return@suspendCancellableCoroutine
    } else {

    }

    if (params.permissionInvoker == null) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        PermissionManageFileActivity.request(context, it)
      } else {
        val permissions = arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        PermissionActivity.request(context, permissions, it)
      }
    } else {
      val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        arrayOf(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE)
      } else {
        arrayOf(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
      }
      params.permissionInvoker!!.requestPermission(permissions) { result ->
        it.complete(result)
      }
    }
  }

  private fun downloadByDownloadManager() {

    val request = params.downloadParams.request
        ?: params.downloadParams.createRequest(context, params.fileParams.isApk) ?: return
    val downloadParams = params.downloadParams
    val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val downloadId = manager.enqueue(request)

    if (downloadParams.forceDownload) {
      DownloadProgressDialog.show(context, params, downloadId)
    }

    SharedHelper.saveDownload(context, DownloadInfo(
        downloadId,
        params.fileParams.isApk
    ))
  }

  companion object {
    internal var authority: String? = null
  }
}