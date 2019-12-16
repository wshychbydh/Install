package com.eye.cool.install.support

import android.app.IntentService
import android.content.Intent
import android.os.IBinder
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
internal class DownloadService : IntentService("download") {

  override fun onBind(intent: Intent?): IBinder? = null
  override fun onHandleIntent(intent: Intent?) {
    val url = intent?.getStringExtra(DOWNLOAD_URL) ?: return
    val path = intent?.getStringExtra(FILE_PATH) ?: return
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