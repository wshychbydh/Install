package com.eye.cool.install.params

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.webkit.URLUtil
import com.eye.cool.install.util.DownloadLog
import java.io.File

/**
 *Created by ycb on 2019/11/29 0029
 */
class DownloadParams private constructor(
    internal val downloadUrl: String,
    private var downloadPath: String?,
    internal val useDownloadManager: Boolean,
    internal val request: DownloadManager.Request?,
    internal val forceDownload: Boolean,
    internal val repeatDownload: Boolean,
) {

  companion object {
    inline fun build(
        downloadUrl: String,
        block: Builder.() -> Unit
    ) = Builder(downloadUrl).apply(block).build()
  }

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
      if (url.endsWith(".apk")) {
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

  data class Builder(
      var downloadUrl: String,
      var downloadPath: String? = null,
      var useDownloadManager: Boolean = false,
      var request: DownloadManager.Request? = null,
      var forceDownload: Boolean = false,
      var repeatDownload: Boolean = false,
  ) {

    /**
     * The download url of the apk to be downloaded
     *
     * @param [downloadUrl] The url must be valid , check by {@link URLUtil.isValidUrl()}
     */
    fun downloadUrl(downloadUrl: String) = apply { this.downloadUrl = downloadUrl }

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
    fun downloadPath(downloadPath: String) = apply { this.downloadPath = downloadPath }

    /**
     * Forced to download. The upgrade progress box displays within the application
     *
     * @param [forceDownload] default false
     */
    fun forceDownload(forceDownload: Boolean) = apply { this.forceDownload = forceDownload }

    /**
     * Use DownloadManager to download, but sometimes there are problems, then you can set it to false
     *
     * @param [useDownloadManager] default true
     */
    fun useDownloadManager(useDownloadManager: Boolean) = apply {
      this.useDownloadManager = useDownloadManager
    }

    /**
     * DownloadManger.Request
     *
     * @param [request]
     */
    fun request(request: DownloadManager.Request) = apply { this.request = request }

    /**
     * If the downloaded file already exists, do you need to download it again
     *
     * @param [repeatDownload] re-download default false
     */
    fun repeatDownload(repeatDownload: Boolean) = apply { this.repeatDownload = repeatDownload }

    fun build(): DownloadParams {
      val url = downloadUrl
      if (!URLUtil.isValidUrl(url)) {
        throw IllegalArgumentException("DownloadUrl is invalid!")
      }

      return DownloadParams(
          downloadUrl = downloadUrl,
          downloadPath = downloadPath,
          useDownloadManager = useDownloadManager,
          request = request,
          forceDownload = forceDownload,
          repeatDownload = repeatDownload
      )
    }
  }
}