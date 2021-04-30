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
  internal var downloadPath: String? = null
  internal var useDownloadManager: Boolean = false
  internal var request: DownloadManager.Request? = null
  internal var forceDownload: Boolean = false
  internal var repeatDownload: Boolean = false

  internal fun createRequest(context: Context, isApk: Boolean) = try {
    val subPath = composeDownloadSubPath(context, isApk)
    val request = DownloadManager.Request(Uri.parse(downloadUrl))
    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, subPath)
    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
  } catch (e: Exception) {
    DownloadLog.logE(e.message ?: "The external storage directory cannot be found or created.")
    null
  }

  internal fun composeDownloadFile(context: Context, isApk: Boolean): File {
    if (!downloadPath.isNullOrEmpty()) return File(downloadPath)

    val dir = context.externalCacheDir
        ?: context.cacheDir
        ?: Environment.getDownloadCacheDirectory()
    if (dir == null || (!dir.exists() && !dir.mkdirs()) || !dir.canRead() || !dir.canWrite()) {
      throw IllegalStateException("The file directory(${dir.absolutePath}) is unavailable or inaccessible")
    }
    val file = File(dir, composeDownloadSubPath(context, isApk))
    downloadPath = file.absolutePath
    return file
  }

  private fun composeDownloadSubPath(context: Context, isApk: Boolean): String {

    val url = downloadUrl

    return if (isApk || url.isNullOrEmpty()) {
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
  }

  class Builder {

    private val params = DownloadParams()


    /**
     * The download url of the apk to be downloaded
     *
     * @param [downloadUrl] The url must be valid , check by {@link URLUtil.isValidUrl()}
     */
    fun downloadUrl(downloadUrl: String): Builder {
      params.downloadUrl = downloadUrl
      return this
    }

    /**
     * If you download using DownloadManager, you should provide a [request]
     *
     * The download path where the file will be download to.
     * Please make sure you have access to the file. {@link FileProvider}
     * or has permission for [android.Manifest.permission.WRITE_EXTERNAL_STORAGE]
     *
     * @param [downloadPath] Default in cache dir
     * {@link context.externalCacheDir} or {@link context.cacheDir}
     *
     * It can be an absolute path or a directory.
     * If it is a directory, it will be spliced with filename.
     *
     */
    fun downloadPath(downloadPath: String): Builder {
      params.downloadPath = downloadPath
      return this
    }

    /**
     * Forced to upgrade. The upgrade progress box displays within the application
     *
     * @param [forceUpdate] default false
     */
    fun forceUpdate(forceUpdate: Boolean): Builder {
      params.forceDownload = forceUpdate
      return this
    }

    /**
     * Use DownloadManager to download, but sometimes there are problems, then you can set it to false
     *
     * @param [useDownloadManager] default true
     */
    fun useDownloadManager(useDownloadManager: Boolean): Builder {
      params.useDownloadManager = useDownloadManager
      return this
    }

    /**
     * DownloadManger.Request
     *
     * @param [request]
     */
    fun request(request: DownloadManager.Request): Builder {
      params.request = request
      return this
    }

    /**
     * If the downloaded file already exists, do you need to download it again
     *
     * @param [repeatDownload] re-download default false
     */
    fun repeatDownload(repeatDownload: Boolean): Builder {
      params.repeatDownload = repeatDownload
      return this
    }

    fun build(): DownloadParams {
      return params
    }
  }
}