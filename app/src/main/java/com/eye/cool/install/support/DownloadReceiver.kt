package com.eye.cool.install.support

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.eye.cool.install.util.DownloadLog
import com.eye.cool.install.util.DownloadUtil
import com.eye.cool.install.util.InstallUtil

/**
 * Created by ycb on 2019/11/28 0028
 */
class DownloadReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent?) {

    if (DownloadManager.ACTION_NOTIFICATION_CLICKED == intent?.action) {

      DownloadUtil.toDownloadPage(context)

    } else if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == intent?.action) {

      val currentDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

      var downloadId = context.getSharedPreferences(DOWNLOAD, Context.MODE_PRIVATE)?.getLong(DOWNLOAD_ID, 0)

      if (currentDownloadId != downloadId) return

      DownloadLog.logI("DownloadManager download by Finished")

      InstallUtil.installApk(context, downloadId)
    }
  }

  companion object {
    const val DOWNLOAD = "download"
    const val DOWNLOAD_ID = "download_id"
  }
}