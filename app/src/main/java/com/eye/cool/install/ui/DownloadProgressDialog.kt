package com.eye.cool.install.ui

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.TextView
import com.eye.cool.install.R
import com.eye.cool.install.params.Params
import com.eye.cool.install.params.ProgressParams
import com.eye.cool.install.support.FileDownloader
import com.eye.cool.install.support.SharedHelper
import com.eye.cool.install.util.DownloadLog
import com.eye.cool.install.util.InstallUtil
import kotlin.math.roundToInt


/**
 *Created by ycb on 2019/11/28 0028
 */
internal class DownloadProgressDialog : DialogActivity() {

  private var apkDownloader: FileDownloader? = null

  private lateinit var progressParams: ProgressParams
  private var downloadId = -1L

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    progressParams = params!!.progressParams

    setFinishOnTouchOutside(progressParams.cancelOnTouchOutside)

    if (progressParams.progress == null) {
      progressParams.progress = DefaultProgressView(this)
    }

    setContentView(progressParams.progress!!.getProgressView())
    setupWindow(progressParams)
    if (params!!.downloadParams.useDownloadManager) {
      downloadId = intent.getLongExtra(DOWNLOAD_ID, -1)
      DownloadLog.logE("Download id :$downloadId")
      if (downloadId > 0L) {
        progressParams.progress!!.onStart()
        contentResolver.registerContentObserver(Uri.parse("content://downloads/my_downloads"), true, downloadObserver)
      }
    } else {
      progressParams.progress!!.onStart()
      apkDownloader = FileDownloader(this, params!!)
      apkDownloader!!.start()
    }
  }

  private val downloadObserver: ContentObserver = object : ContentObserver(null) {
    override fun onChange(selfChange: Boolean) {
      queryDownloadStatus()
    }
  }

  private fun queryDownloadStatus() {
    if (downloadId <= 0L) {
      contentResolver.unregisterContentObserver(downloadObserver)
      DownloadLog.logE("Download id exception($downloadId)!")
      finish()
      return
    }
    val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val cursor: Cursor = manager.query(DownloadManager.Query().setFilterById(downloadId))
    if (cursor.moveToFirst()) {
      val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
      val totalSizeBytes = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
      val bytesDownloadSoFar = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
      val progress = bytesDownloadSoFar.toFloat() / totalSizeBytes * 100f
      DownloadLog.logI("Download status:$status; progress:$progress")
      if (status == DownloadManager.STATUS_RUNNING) {
        runOnUiThread {
          progressParams.progress?.onProgress(progress)
        }
      } else {
        if (status == DownloadManager.STATUS_SUCCESSFUL) {
          val info = SharedHelper.getDownloadById(this, downloadId)
          val path = info?.path ?: InstallUtil.queryFileByDownloadId(this, downloadId)
          progressParams.progress?.onFinished(path)
        }
        finish()
      }
    }
  }

  private fun setupWindow(progressParams: ProgressParams) {
    window.decorView.setPadding(0, 0, 0, 0)

    val lp = window.attributes

    if (progressParams.width > 0) {
      lp.width = progressParams.width
    }
    if (progressParams.height > 0) {
      lp.height = progressParams.height
    }

    lp.windowAnimations = progressParams.windowAnim

    if (progressParams.x > 0) {
      lp.x = progressParams.x
    }
    if (progressParams.y > 0) {
      lp.y = progressParams.y
    }
    lp.gravity = progressParams.gravity
    lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
    lp.dimAmount = progressParams.dimAmount
    window.attributes = lp
  }

  override fun onBackPressed() {
    if (params!!.progressParams.cancelAble) {
      super.onBackPressed()
    }
  }

  override fun finish() {
    super.finish()
    overridePendingTransition(0, 0)
  }

  override fun onDestroy() {
    apkDownloader?.stop()
    params = null
    contentResolver.unregisterContentObserver(downloadObserver)
    super.onDestroy()
  }

  companion object {

    private var params: Params? = null

    private const val DOWNLOAD_ID = "download_id"

    fun show(context: Context, params: Params, downloadId: Long? = null) {
      val intent = Intent(context, DownloadProgressDialog::class.java)
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      intent.putExtra(DOWNLOAD_ID, downloadId)
      context.startActivity(intent)
      this.params = params
    }
  }

  inner class DefaultProgressView(context: Context) : ProgressParams.IProgress {

    private val contentView: View = LayoutInflater.from(context).inflate(R.layout.install_dialog_download_progress, null)
    private val progressTv: TextView = contentView.findViewById(R.id.progressTv)
    private val progressBar: ProgressBar = contentView.findViewById(R.id.progressBar)

    override fun getProgressView(): View = contentView

    override fun onProgress(progress: Float) {
      progressTv.text = getString(R.string.install_download_msg, progress, "%")
      progressBar.progress = progress.roundToInt()
    }

    override fun onFinished(path: String?) {
      finish()
    }
  }
}