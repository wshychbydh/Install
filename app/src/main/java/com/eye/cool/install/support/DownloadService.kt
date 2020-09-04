package com.eye.cool.install.support

import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.eye.cool.install.params.NotifyParams
import com.eye.cool.install.util.DownloadLog
import com.eye.cool.install.util.InstallUtil
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 *Created by ycb on 2019/11/28 0028
 */
internal class DownloadService : IntentService("Download") {

  private val notifyIds = arrayListOf<Int>()

  override fun onBind(intent: Intent?): IBinder? = null

  override fun onHandleIntent(intent: Intent?) {
    startNotification(intent)
    val url = intent?.getStringExtra(DOWNLOAD_URL) ?: return
    val path = intent.getStringExtra(FILE_PATH) ?: return
    val isApk = intent.getBooleanExtra(IS_APK, true)
    DownloadLog.logI("Download($url) by DownloadService...")
    val downloadFile = File(path)
    download(url, downloadFile)
    DownloadLog.logI("DownloadService download($path) Finished")
    if (isApk && downloadFile.exists()) {
      InstallUtil.installApk(this@DownloadService, downloadFile)
    }
  }

  private fun startNotification(intent: Intent?) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val notifyParams = intent?.getParcelableExtra<NotifyParams>(NOTIFY_PARAMS)!!
      notifyIds.add(notifyParams.notifyId)
      val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
      nm.createNotificationChannel(notifyParams.notifyChannel
          ?: getDefaultChannel(notifyParams.channelId))
      val notify = notifyParams.notification
          ?: NotificationCompat.Builder(this, notifyParams.channelId).build()
      startForeground(notifyParams.notifyId, notify)
    }
  }

  @RequiresApi(Build.VERSION_CODES.O)
  private fun getDefaultChannel(channelId: String): NotificationChannel {
    val appInfo = packageManager.getApplicationInfo(packageName, 0)
    val channelName = packageManager.getApplicationLabel(appInfo) as? String ?: "Download"
    return NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
  }

  private fun download(
      url: String,
      downloadFile: File
  ) {
    var inputStream: InputStream? = null
    var outputStream: OutputStream? = null
    var connection: HttpURLConnection? = null
    try {
      val url = URL(url)
      connection = url.openConnection() as HttpURLConnection
      //  connection.requestMethod = "GET"
      connection.readTimeout = 10 * 60 * 1000
      connection.connectTimeout = 60 * 1000
      connection.connect()

      if (connection.responseCode == 200) {
        inputStream = connection.inputStream
        outputStream = FileOutputStream(downloadFile)
        val buf = ByteArray(4096)
        var len: Int
        do {
          len = inputStream.read(buf)
          if (len <= 0) break
          outputStream.write(buf, 0, len)
        } while (len > -1)
      }
    } catch (e: Exception) {
      e.printStackTrace()
      downloadFile.delete()
    } finally {
      inputStream?.close()
      outputStream?.close()
      connection?.disconnect()
    }
  }

  override fun onDestroy() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
      notifyIds.forEach {
        nm.cancel(it)
      }
    }
    super.onDestroy()
  }

  companion object {
    const val DOWNLOAD_URL = "download_url"
    const val FILE_PATH = "file_path"
    const val IS_APK = "is_apk"
    const val NOTIFY_PARAMS = "notify_params"
  }
}