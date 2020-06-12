package com.eye.cool.install.params

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import com.eye.cool.install.util.DownloadLog
import java.io.File

/**
 *Created by ycb on 2019/11/29 0029
 */
class DownloadParams private constructor() {

  internal var downloadUrl: String? = null
  internal var downloadDirType: String = Environment.DIRECTORY_DOWNLOADS
  internal var downloadSubPath: String? = null
  internal var downloadPath: String? = null
  internal var useDownloadManager: Boolean = true
  internal var request: DownloadManager.Request? = null
  internal var forceDownload: Boolean = false
  internal var repeatDownload: Boolean = false

  internal fun createRequest(context: Context): Pair<DownloadManager.Request, File?>? {
    if (request != null) return Pair(request!!, null)

    var fileDir: File? = null
    var pubDir: File? = Environment.getExternalStoragePublicDirectory(downloadDirType)
    if (pubDir == null || (!pubDir.exists() && !pubDir.mkdirs()) || !pubDir.canRead() || !pubDir.canWrite()) {
      fileDir = context.getExternalFilesDir(downloadDirType)
      if (fileDir == null || (!fileDir.exists() && !fileDir.mkdirs()) || !fileDir.canRead() || !fileDir.canWrite()) {
        DownloadLog.logE("The file directory(${pubDir?.absolutePath} or ${fileDir?.absolutePath}) are unavailable or inaccessible!")
        return null
      }
    }
    DownloadLog.logI("Download by DownloadManager...")

    val request = DownloadManager.Request(Uri.parse(downloadUrl))
    val downloadDir: File?
    if (fileDir == null) {
      downloadDir = Environment.getExternalStoragePublicDirectory(downloadDirType)
      request.setDestinationInExternalPublicDir(downloadDirType, downloadSubPath)
    } else {
      downloadDir = context.getExternalFilesDir(downloadDirType)
      request.setDestinationInExternalFilesDir(context, downloadDirType, downloadSubPath)
    }

    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

    return Pair(request, downloadDir)
  }

  internal fun composeDownloadFile(context: Context, isApk: Boolean): File {
    if (!downloadPath.isNullOrEmpty()) return File(downloadPath)

    val dir = Environment.getExternalStoragePublicDirectory(downloadDirType)
        ?: Environment.getDownloadCacheDirectory()
    if (dir == null || (!dir.exists() && !dir.mkdirs()) || !dir.canRead() || !dir.canWrite()) {
      throw IllegalStateException("The file directory(${dir.absolutePath}) is unavailable or inaccessible")
    }
    val file = File(dir, composeDownloadSubPath(context, isApk))
    downloadPath = file.absolutePath
    return file
  }

  private fun composeDownloadSubPath(context: Context, isApk: Boolean): String {

    if (!downloadSubPath.isNullOrEmpty()) {
      return downloadSubPath!!
    }

    val url = downloadUrl

    downloadSubPath = if (isApk || url.isNullOrEmpty()) {
      if (url?.endsWith(".apk") == true) {
        url.substring(url.lastIndexOf("/"), url.length)
      } else {
        val appInfo = context.packageManager.getApplicationInfo(context.packageName, 0)
        val appName = context.packageManager.getApplicationLabel(appInfo) as String
        "$appName${if (isApk) ".apk" else ""}"
      }
    } else {
      url.substring(url.lastIndexOf("/"), url.length)
    }
    return downloadSubPath!!
  }

  class Builder {

    private val params = DownloadParams()


    /**
     * The download url of the apk to be downloaded
     *
     * @param downloadUrl The url must be valid , check by {@link URLUtil.isValidUrl()}
     */
    fun setDownloadUrl(downloadUrl: String): Builder {
      params.downloadUrl = downloadUrl
      return this
    }

    /**
     * The download dir where the apk will be download to, default Download
     * Another dirType need to be checked for access {@link FileProvider}
     *
     *@param dirType The directory type to pass to {@link Context#getExternalPubDir(String)} or {@link Context#getExternalFilesDir(String)}
     *@param subPath The path within the external directory, including the destination filename
     */
    fun setDownloadExternalDir(dirType: String, subPath: String): Builder {
      params.downloadDirType = dirType
      params.downloadSubPath = subPath
      return this
    }

    /**
     * If you download using DownloadManager, use @See{downloadDirType} and @See{downloadSubPath} or provide a @See{request}
     *
     * The download path where the file will be download to.
     * Please make sure you have access to the file. {@link FileProvider}
     *
     * @param downloadPath Default in external or file cache {download/${app_name}.apk}
     * {@link getExternalStoragePublicDirectory()} or {@link getExternalFilesDir()}
     *
     * It can be an absolute path or a directory.
     * If it is a directory, it will be spliced with filename.
     *
     */
    fun setDownloadPath(downloadPath: String): Builder {
      params.downloadPath = downloadPath
      return this
    }

    /**
     * Forced to upgrade. The upgrade progress box displays within the application
     *
     * @param forceUpdate  default false
     */
    fun forceUpdate(forceUpdate: Boolean): Builder {
      params.forceDownload = forceUpdate
      return this
    }

    /**
     * Use DownloadManager to download, but sometimes there are problems, then you can set it to false
     *
     * @param useDownloadManager default true
     */
    fun useDownloadManager(useDownloadManager: Boolean): Builder {
      params.useDownloadManager = useDownloadManager
      return this
    }

    /**
     * DownloadManger.Request
     *
     * @param request
     */
    fun setRequest(request: DownloadManager.Request): Builder {
      params.request = request
      return this
    }

    /**
     * If the downloaded file already exists, do you need to download it again
     *
     * @param repeatDownload re-download default false
     */
    fun setRepeatDownload(repeatDownload: Boolean): Builder {
      params.repeatDownload = repeatDownload
      return this
    }

    fun build(): DownloadParams {
      return params
    }
  }
}