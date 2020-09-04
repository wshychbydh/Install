package com.eye.cool.install

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.webkit.URLUtil
import androidx.core.content.ContextCompat
import com.eye.cool.install.params.DownloadParams
import com.eye.cool.install.params.Params
import com.eye.cool.install.params.ProgressParams
import com.eye.cool.install.params.PromptParams
import com.eye.cool.install.support.DownloadInfo
import com.eye.cool.install.support.DownloadService
import com.eye.cool.install.support.SharedHelper
import com.eye.cool.install.ui.DownloadProgressDialog
import com.eye.cool.install.ui.InstallPermissionActivity
import com.eye.cool.install.ui.PermissionActivity
import com.eye.cool.install.ui.PromptDialog
import com.eye.cool.install.util.DownloadLog
import com.eye.cool.install.util.DownloadUtil
import com.eye.cool.install.util.InstallUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
    DownloadLog.tag = params.logTag
    DownloadLog.enableLog = params.enableLog
  }

  fun start() {
    GlobalScope.launch {
      if (!checkParams()) return@launch
      tryShowPrompt()
    }
  }

  private fun checkParams(): Boolean {
    val downloadParams = params.downloadParams
    if (!URLUtil.isValidUrl(downloadParams.downloadUrl)) {
      if (!downloadParams.useDownloadManager || downloadParams.request == null) {
        DownloadLog.logE("Download url(${downloadParams.downloadUrl}) is invalid..")
        return false
      }
    }

    if (downloadParams.forceDownload && DownloadProgressDialog.sParams != null) {
      DownloadLog.logE("Only one mandatory download task is supported")
      return false
    }

    return true
  }

  private fun startOnPermissionGranted() {
    val target = context.applicationInfo.targetSdkVersion
    if (target >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      checkPermission { result ->
        if (result) {
          download()
        } else {
          DownloadLog.logE("Download failed, permission denied.")
        }
      }
    } else {
      download()
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
          if (params.fileParams.isApk) {
            checkInstallPermission(invoker)
          } else {
            invoker.invoke(true)
          }
        } else {
          invoker.invoke(false)
        }
      }
    } else {
      params.permissionInvoker!!.request(permissions) {
        if (it) {
          if (params.fileParams.isApk) {
            checkInstallPermission(invoker)
          } else {
            invoker.invoke(true)
          }
        } else {
          invoker.invoke(false)
        }
      }
    }
  }

  private fun checkInstallPermission(invoker: (Boolean) -> Unit) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || context.packageManager.canRequestPackageInstalls()) {
      invoker.invoke(true)
      return
    }
    if (params.installPermissionInvoker == null) {
      InstallPermissionActivity.requestInstallPermission(context, invoker)
    } else {
      params.installPermissionInvoker!!.request(invoker)
    }
  }

  private fun tryShowPrompt() {
    if (params.promptParams?.isValid() == true) {
      PromptDialog.show(context, params.promptParams!!, object : PromptParams.IPromptListener {
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

  private fun download() {
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
      downloadByDownloadManager()
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
    intent.putExtra(DownloadService.FILE_PATH, downloadParams.composeDownloadFile(context, params.fileParams.isApk).absolutePath)
    intent.putExtra(DownloadService.IS_APK, params.fileParams.isApk)
    intent.putExtra(DownloadService.NOTIFY_PARAMS, params.notifyParams)
    ContextCompat.startForegroundService(context, intent)
  }

  private fun downloadByDownloadManager() {

    val pair = params.downloadParams.createRequest(context) ?: return
    val downloadParams = params.downloadParams
    val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val downloadId = manager.enqueue(pair.first)
    val downloadDir = pair.second
    val downloadFile: File? = if (downloadDir == null) null else File(downloadDir, downloadParams.downloadSubPath)

    if (downloadParams.forceDownload) {
      DownloadProgressDialog.show(context, params, downloadId)
    }

    SharedHelper.saveDownload(context, DownloadInfo(
        downloadId,
        params.fileParams.isApk,
        downloadFile?.absolutePath
    ))
  }

  companion object {
    internal var authority: String? = null
  }
}