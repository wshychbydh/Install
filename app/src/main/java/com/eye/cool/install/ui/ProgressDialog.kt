package com.eye.cool.install.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.eye.cool.install.R
import com.eye.cool.install.params.Params
import com.eye.cool.install.support.ApkDownloader
import com.eye.cool.install.support.IProgress
import kotlinx.android.synthetic.main.dialog_download_progress.view.*
import kotlin.math.roundToInt

/**
 *Created by ycb on 2019/11/28 0028
 */
internal class ProgressDialog : DialogActivity() {

  private var apkDownloader: ApkDownloader? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val params = updateParams!!.progressParams

    setFinishOnTouchOutside(params.cancelOnTouchOutside)

    if (params.progress == null) {
      params.progress = DefaultProgressView(this)
    }

    params.progress!!.getProgressView().apply {
      val layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
      if (params.width > 0) {
        layoutParams.width = params.width
      }
      if (params.height > 0) {
        layoutParams.height = params.height
      }
      if (params.backgroundDrawable != null) {
        setBackgroundDrawable(params.backgroundDrawable)
      }
      setContentView(this, layoutParams)
      window.decorView.setPadding(0, 0, 0, 0)

      val lp = window.attributes
      if (params.windowAnim != 0) {
        lp.windowAnimations = params.windowAnim
      }
      if (params.x > 0) {
        lp.x = params.x
      }
      if (params.y > 0) {
        lp.y = params.y
      }
      lp.gravity = params.gravity
      lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
      lp.dimAmount = params.dimAmount
      window.attributes = lp
    }
    apkDownloader = ApkDownloader(this, updateParams!!)
    apkDownloader!!.start()
  }

  override fun onBackPressed() {
    if (updateParams!!.progressParams.cancelAble) {
      super.onBackPressed()
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    updateParams = null
    apkDownloader?.stop()
  }

  companion object {

    private var updateParams: Params? = null

    fun show(context: Context, updateParams: Params) {
      val intent = Intent(context, ProgressDialog::class.java)
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      context.startActivity(intent)
      Companion.updateParams = updateParams
    }
  }

  inner class DefaultProgressView(context: Context) : IProgress {

    private val contentView: View = LayoutInflater.from(context).inflate(R.layout.dialog_download_progress, null)
    private val progressTv: TextView = contentView.progressTv
    private val progressBar: ProgressBar = contentView.progressBar

    override fun getProgressView(): View = contentView

    override fun onProgress(progress: Float) {
      progressTv.text = getString(R.string.download_msg, progress, "%")
      progressBar.progress = progress.roundToInt()
    }

    override fun onFinished(path: String): Boolean {
      finish()
      return false
    }
  }
}