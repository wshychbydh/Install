package com.eye.cool.install.support

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.eye.cool.install.params.Params
import com.eye.cool.install.params.ProgressParams
import com.eye.cool.install.util.DownloadLog
import com.eye.cool.install.util.InstallUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 *Created by cool on 2019/11/28
 */
internal class FileDownloader(
    private val activity: AppCompatActivity,
    private val params: Params,
    private val defaultProgress: ProgressParams.IProgressListener? = null
) {

  fun start() {
    activity.lifecycleScope.launch(Dispatchers.IO) {
      val downloadFile = params.downloadParams.composeDownloadFile(activity, params.fileParams.isApk)
      download(downloadFile) { progress ->
        withContext(Dispatchers.Main) {
          defaultProgress?.onProgress(progress)
          params.progressParams.progressListener?.onProgress(progress)
        }
      }
      defaultProgress?.onFinished(downloadFile.absolutePath)
      params.progressParams.progressListener?.onFinished(downloadFile.absolutePath)
      if (downloadFile.exists() && params.fileParams.isApk) {
        InstallUtil.installApk(activity, downloadFile)
      }
    }
  }

  private suspend fun download(
      downloadFile: File,
      publishProgress: suspend (Float) -> Unit
  ) {
    var inputStream: InputStream? = null
    var outputStream: OutputStream? = null
    var connection: HttpURLConnection? = null
    try {
      val url = URL(params.downloadParams.downloadUrl)
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
        val fileLength = connection.contentLength.toFloat() / 100f
        var readLength = 0
        do {
          len = inputStream.read(buf)
          if (len < 0) break
          readLength += len
          outputStream.write(buf, 0, len)
          publishProgress(readLength.toFloat() / fileLength)
        } while (len > -1)
      }
    } catch (e: Exception) {
      DownloadLog.logE(e.message ?: "Download failed")
      downloadFile.delete()
    } finally {
      inputStream?.close()
      outputStream?.close()
      connection?.disconnect()
    }
  }
}