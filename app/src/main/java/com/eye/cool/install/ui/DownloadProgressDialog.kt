package com.eye.cool.install.ui

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt


/**
 *Created by ycb on 2019/11/28 0028
 */
internal class DownloadProgressDialog : DialogActivity() {

  private var apkDownloader: FileDownloader? = null

  private lateinit var progressParams: ProgressParams
  private var downloadId = -1L

  private var defaultProgress: DefaultProgress? = null

  private var countDownTimer: CountDownTimer? = null

  private lateinit var downloadManager: DownloadManager

  @Volatile
  private var query: DownloadManager.Query? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    progressParams = params!!.progressParams

    setFinishOnTouchOutside(progressParams.cancelOnTouchOutside)

    var progressView = progressParams.progressView

    if (progressView == null) {
      defaultProgress = DefaultProgress()
      progressView = defaultProgress!!.getProgressView()
    }

    setContentView(progressView)
    setupWindow(progressParams)
    if (params!!.downloadParams.useDownloadManager) {
      downloadId = intent.getLongExtra(DOWNLOAD_ID, -1)
      DownloadLog.logE("Download id :$downloadId")
      if (downloadId > 0L) {
        progressParams.progressListener?.onStart()
        countDownTimer = DownloadCountDown(progressParams.progressTimeout) { time ->
          GlobalScope.launch {
            queryDownloadStatus()
          }
        }.start()
      }
    } else {
      progressParams.progressListener?.onStart()
      apkDownloader = FileDownloader(this, params!!, defaultProgress)
      apkDownloader!!.start()
    }
  }

  private suspend fun queryDownloadStatus() {
    val query = this.query ?: DownloadManager.Query().setFilterById(downloadId)
    val cursor = downloadManager.query(query)
    if (!cursor.moveToFirst()) return
    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
    val totalSizeBytes = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
    val bytesDownloadSoFar = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
    var progress = bytesDownloadSoFar.toFloat() / totalSizeBytes * 100f
    if (progress < 0.0) progress = 0.0f
    DownloadLog.logI("$downloadId-->Download status:$status; progress:$progress")
    when (status) {
      DownloadManager.STATUS_FAILED -> {
        finish()
      }
      DownloadManager.STATUS_SUCCESSFUL -> {
        val info = SharedHelper.getDownloadById(this, downloadId)
        val path = info?.path ?: InstallUtil.queryFileByDownloadId(this, downloadId)
        withContext(Dispatchers.Main) {
          progressParams.progressListener?.onFinished(path)
        }
        finish()
      }
      else -> {
        withContext(Dispatchers.Main) {
          defaultProgress?.onProgress(progress)
          progressParams.progressListener?.onProgress(progress)
        }
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
    countDownTimer?.cancel()
    countDownTimer = null
    super.finish()
    overridePendingTransition(0, 0)
  }

  override fun onDestroy() {
    apkDownloader?.stop()
    params = null
    countDownTimer?.cancel()
    super.onDestroy()
  }

  companion object {

    @Volatile
    internal var params: Params? = null

    private const val DOWNLOAD_ID = "download_id"

    fun show(context: Context, params: Params, downloadId: Long? = null) {
      val intent = Intent(context, DownloadProgressDialog::class.java)
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      intent.putExtra(DOWNLOAD_ID, downloadId)
      context.startActivity(intent)
      this.params = params
    }
  }

  inner class DownloadCountDown(
      val duration: Long,
      private val onTick: (Long) -> Unit
  ) : CountDownTimer(duration, 500L) {
    override fun onFinish() {
      finish()
    }

    override fun onTick(millisUntilFinished: Long) {
      onTick.invoke(millisUntilFinished)
    }
  }

  inner class DefaultProgress : ProgressParams.IProgressListener {

    private val contentView: View = LayoutInflater.from(this@DownloadProgressDialog)
        .inflate(R.layout.install_dialog_download_progress, null)
    private val progressTv: TextView = contentView.findViewById(R.id.progressTv)
    private val progressBar: ProgressBar = contentView.findViewById(R.id.progressBar)

    init {
      onProgress(0.0f)
    }

    fun getProgressView(): View = contentView

    override fun onStart() {

    }

    override fun onProgress(progress: Float) {
      progressTv.text = getString(R.string.install_download_msg, progress, "%")
      progressBar.progress = progress.roundToInt()
    }

    override fun onFinished(path: String?) {
      finish()
    }
  }
}