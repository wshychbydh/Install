package com.eye.cool.install.support

import android.content.Context
import android.os.AsyncTask
import com.eye.cool.install.params.Params
import com.eye.cool.install.util.DownloadUtil
import com.eye.cool.install.util.InstallUtil
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 *Created by cool on 2019/11/28
 */
internal class ApkDownloader(
    private val context: Context,
    private val params: Params
) {

  private var task: DownloadTask? = null

  fun start() {
    if (DownloadUtil.checkApkDownload(context, params.downloadParams)) {
      InstallUtil.installApk(context, params.downloadParams.downloadPath!!)
    } else {
      task = DownloadTask()
      task!!.execute(params.downloadParams.downloadUrl)
    }
  }

  fun stop() {
    task?.cancel(true)
  }

  inner class DownloadTask : AsyncTask<String, Float, Unit>() {

    override fun doInBackground(vararg params: String) {
      download(params[0])
    }

    override fun onProgressUpdate(vararg values: Float?) {
      val progress = values[0]!!
      params.progressParams.progress!!.onProgress(progress)
    }

    override fun onPostExecute(result: Unit?) {
      if (!isCancelled) {
        val apkPath = params.downloadParams.downloadPath!!
        if (!params.progressParams.progress!!.onFinished(apkPath)) {
          InstallUtil.installApk(context, apkPath)
        }
      }
    }

    private fun download(downloadUrl: String) {

      var inputStream: InputStream? = null
      var outputStream: OutputStream? = null
      var connection: HttpURLConnection? = null
      try {
        val url = URL(downloadUrl)
        connection = url.openConnection() as HttpURLConnection
        //  connection.requestMethod = "GET"
        connection.readTimeout = 10 * 60 * 1000
        connection.connectTimeout = 60 * 1000
        connection.connect()

        if (connection.responseCode == 200) {
          inputStream = connection.inputStream
          outputStream = FileOutputStream(params.downloadParams.downloadPath!!)
          val buf = ByteArray(1024)
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
        e.printStackTrace()
        File(params.downloadParams.downloadPath!!).delete()
      } finally {
        inputStream?.close()
        outputStream?.close()
        connection?.disconnect()
      }
    }
  }
}