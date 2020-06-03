package com.eye.cool.install.support

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.eye.cool.install.util.DownloadLog
import com.eye.cool.install.util.InstallUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 *Created by ycb on 2019/11/28 0028
 */
internal class DownloadService : Service() {

  override fun onBind(intent: Intent?): IBinder? = null

  override fun onCreate() {
    super.onCreate()
    startNotification()
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    val url = intent?.getStringExtra(DOWNLOAD_URL) ?: return START_NOT_STICKY
    val path = intent?.getStringExtra(FILE_PATH) ?: return START_NOT_STICKY
    DownloadLog.logI("Download($url) by DownloadService...")
    GlobalScope.launch {
      withContext(Dispatchers.IO) {
        download(url, path)
        withContext(Dispatchers.Main) {
          DownloadLog.logI("DownloadService download($path) Finished")
          InstallUtil.installApk(this@DownloadService, path)
        }
      }
    }
    return START_STICKY
  }

  private fun startNotification() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
      val channelId = javaClass.simpleName
      nm.createNotificationChannel(NotificationChannel(channelId, "Download Apk", NotificationManager.IMPORTANCE_DEFAULT))
      val builder = NotificationCompat.Builder(this, channelId)
      startForeground(channelId.hashCode(), builder.build())
    }
  }

  private fun download(url: String, path: String) {
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
        outputStream = FileOutputStream(path)
        val buf = ByteArray(1024)
        var len: Int
        var readLength = 0
        do {
          len = inputStream.read(buf)
          if (len <= 0) break
          readLength += len
          outputStream.write(buf, 0, len)
        } while (len > -1)
      }
    } catch (e: Exception) {
      e.printStackTrace()
      File(path).delete()
    } finally {
      inputStream?.close()
      outputStream?.close()
      connection?.disconnect()
    }
  }

  companion object {
    const val DOWNLOAD_URL = "download_url"
    const val FILE_PATH = "file_path"
  }
}