package com.eye.cool.install.support

import android.os.AsyncTask
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 *Created by ycb on 2019/11/28 0028
 */
internal class DownloadTask(
    private val url: String,
    private val filePath: String,
    private val progress: IProgress? = null
) : AsyncTask<String, Float, Unit>() {

  override fun onPreExecute() {
    super.onPreExecute()
    progress?.onStart()
  }

  override fun doInBackground(vararg params: String) {
    download()
  }

  override fun onProgressUpdate(vararg values: Float?) {
    progress?.onProgress(values[0]!!)
  }

  override fun onPostExecute(result: Unit?) {
    progress?.onFinished(filePath)
  }

  private fun download() {

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
        outputStream = FileOutputStream(filePath)
        val buf = ByteArray(1024)
        var len: Int
        val fileLength = connection.contentLength
        var readLength = 0
        do {
          len = inputStream.read(buf)
          readLength += len
          outputStream.write(buf, 0, len)
          publishProgress(readLength / fileLength.toFloat() * 100f)
        } while (len > -1)
      }
    } catch (e: Exception) {
      e.printStackTrace()
    } finally {
      inputStream?.close()
      outputStream?.close()
      connection?.disconnect()
    }
  }
}