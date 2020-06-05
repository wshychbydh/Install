package com.eye.cool.install.support

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.eye.cool.install.util.DownloadLog
import com.eye.cool.install.util.DownloadUtil
import com.eye.cool.install.util.InstallUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

/**
 * Created by ycb on 2019/11/28 0028
 */
class DownloadReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent?) {

    if (DownloadManager.ACTION_NOTIFICATION_CLICKED == intent?.action) {

      DownloadUtil.toDownloadPage(context)

    } else if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == intent?.action) {
      GlobalScope.launch {

        val currentDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

        DownloadLog.logI("DownloadManager download is Finished")

        val downloadInfo = SharedHelper.getDownloadById(context, currentDownloadId) ?: return@launch
        SharedHelper.clearDownload(context, currentDownloadId)

        if (!downloadInfo.isApk) return@launch

        DownloadLog.logI("DownloadId：$currentDownloadId, isApk:${downloadInfo.isApk}, path:${downloadInfo.path}")

        if (DownloadUtil.isFileExist(context, downloadInfo.path)) {
          InstallUtil.installApk(context, File(downloadInfo.path))
        } else {
          InstallUtil.installApk(context, currentDownloadId)
        }
      }
    }
  }

  companion object {
    const val DOWNLOAD = "download"
  }
}