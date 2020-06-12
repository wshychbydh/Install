package com.eye.cool.install.support

import android.content.Context
import android.os.AsyncTask
import com.eye.cool.install.params.Params
import com.eye.cool.install.params.ProgressParams
import com.eye.cool.install.util.InstallUtil
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 *Created by cool on 2019/11/28
 */
internal class FileDownloader(
    private val context: Context,
    private val params: Params,
    private val defaultProgress: ProgressParams.IProgressListener? = null
) {

  private var task: AsyncTask<String, Float, Unit>? = null

  fun start() {
    task = DownloadTask().execute()
  }

  fun stop() {
    task?.cancel(true)
  }

  inner class DownloadTask : AsyncTask<String, Float, Unit>() {

    private val downloadFile = params.downloadParams.composeDownloadFile(context, params.fileParams.isApk)

    override fun doInBackground(vararg params: String) {
      download()
    }

    override fun onProgressUpdate(vararg values: Float?) {
      val progress = values[0]!!
      defaultProgress?.onProgress(progress)
      params.progressParams.progressListener?.onProgress(progress)
    }

    override fun onPostExecute(result: Unit?) {
      if (!isCancelled) {
        defaultProgress?.onFinished(downloadFile.absolutePath)
        params.progressParams.progressListener?.onFinished(downloadFile.absolutePath)
        if (params.fileParams.isApk) {
          InstallUtil.installApk(context, downloadFile)
        }
      }
    }

    private fun download() {

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
        try {
          downloadFile.delete()
        } catch (e: Exception) {
          //ignore
        }
      } finally {
        inputStream?.close()
        outputStream?.close()
        connection?.disconnect()
      }
    }
  }
}