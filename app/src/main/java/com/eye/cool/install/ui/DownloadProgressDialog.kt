package com.eye.cool.install.ui

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.eye.cool.install.R
import com.eye.cool.install.params.Params
import com.eye.cool.install.params.ProgressParams
import com.eye.cool.install.params.WindowParams
import com.eye.cool.install.support.FileDownloader
import com.eye.cool.install.util.DownloadLog
import com.eye.cool.install.util.InstallUtil
import kotlinx.coroutines.*
import kotlin.math.roundToInt


/**
 *Created by ycb on 2019/11/28 0028
 */
internal class DownloadProgressDialog : DialogActivity() {

  private var apkDownloader: FileDownloader? = null

  private val progressParams: ProgressParams = sParams!!.progressParams
  private var downloadId = -1L

  private var defaultProgress: DefaultProgress? = null

  private var countDownTimer: CountDownTimer? = null

  override val windowParams: WindowParams? = progressParams.window

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    var progressView = progressParams.progressView

    if (progressView == null) {
      defaultProgress = DefaultProgress()
      progressView = defaultProgress!!.getProgressView()
    }

    setContentView(progressView)

    if (sParams!!.downloadParams.useDownloadManager) {
      downloadId = intent.getLongExtra(DOWNLOAD_ID, -1)
      DownloadLog.logE("Download id :$downloadId")
      if (downloadId > 0L) {
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query().setFilterById(downloadId)
        progressParams.progressListener?.onStart()
        countDownTimer = DownloadCountDown(progressParams.progressTimeout) {
          lifecycleScope.launch(Dispatchers.IO) {
            try {
              queryDownloadStatus(downloadManager, query)
            } catch (ignore: Exception) {
            }
          }
        }.start()
      }
    } else {
      progressParams.progressListener?.onStart()
      apkDownloader = FileDownloader(this, sParams!!, defaultProgress)
      apkDownloader!!.start()
    }
  }

  private suspend fun queryDownloadStatus(
      downloadManager: DownloadManager,
      query: DownloadManager.Query
  ) {
    val cursor = downloadManager.query(query)
    if (!cursor.moveToFirst()) return
    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
    val totalSizeBytes = cursor
        .getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
    val bytesDownloadSoFar = cursor
        .getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
    var progress = bytesDownloadSoFar.toFloat() / totalSizeBytes * 100f
    if (progress < 0.0) progress = 0.0f
    DownloadLog.logI("$downloadId-->Download status:$status; progress:$progress")
    when (status) {
      DownloadManager.STATUS_FAILED -> {
        countDownTimer?.cancel()
        finish()
      }
      DownloadManager.STATUS_SUCCESSFUL -> {
        countDownTimer?.cancel()
        val path = InstallUtil.queryFileByDownloadId(this, downloadId)
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

  override fun onDestroy() {
    countDownTimer?.cancel()
    sParams = null
    super.onDestroy()
  }

  companion object {

    @Volatile
    internal var sParams: Params? = null

    private const val DOWNLOAD_ID = "download_id"

    fun show(context: Context, params: Params, downloadId: Long? = null) {
      val intent = Intent(context, DownloadProgressDialog::class.java)
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      intent.putExtra(DOWNLOAD_ID, downloadId)
      context.startActivity(intent)
      this.sParams = params
    }
  }

  inner class DownloadCountDown(
      duration: Long,
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