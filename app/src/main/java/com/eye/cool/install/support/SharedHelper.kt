package com.eye.cool.install.support

import android.content.Context
import androidx.annotation.WorkerThread

/**
 * Created by ycb on 2020/6/5 0005
 */
internal object SharedHelper {

  private const val DOWNLOAD = "download"
  private const val ID = "_id"
  private const val IS_APK = "_is_apk"
  private const val PATH = "_path"

  @WorkerThread
  fun saveDownload(context: Context, downloadInfo: DownloadInfo) {
    val downloadKey = downloadInfo.downloadId.toString()
    context.getSharedPreferences(DownloadReceiver.DOWNLOAD, Context.MODE_PRIVATE)
        .edit()
        .putLong(downloadKey + ID, downloadInfo.downloadId)
        .putBoolean(downloadKey + IS_APK, downloadInfo.isApk)
        .putString(downloadKey + PATH, downloadInfo.path)
        .apply()
  }

  @WorkerThread
  fun getDownloadById(context: Context, downloadId: Long): DownloadInfo? {
    val downloadKey = downloadId.toString()
    val shared = context.getSharedPreferences(DownloadReceiver.DOWNLOAD, Context.MODE_PRIVATE)
    if (!shared.contains(downloadKey + ID)) return null
    val isApk = shared.getBoolean(downloadKey + IS_APK, true)
    val path = shared.getString(downloadKey + PATH, null)
    return DownloadInfo(downloadId, isApk, path)
  }

  @WorkerThread
  fun clearDownload(context: Context, downloadId: Long) {
    val downloadKey = downloadId.toString()
    context.getSharedPreferences(DownloadReceiver.DOWNLOAD, Context.MODE_PRIVATE)
        .edit()
        .remove(downloadKey + ID)
        .remove(downloadKey + IS_APK)
        .remove(downloadKey + PATH)
        .apply()
  }
}