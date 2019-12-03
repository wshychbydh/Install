package com.eye.cool.install.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.eye.cool.install.support.IProgress
import com.eye.cool.install.params.Params
import com.eye.cool.install.R
import com.eye.cool.install.support.ApkDownloader
import kotlinx.android.synthetic.main.dialog_download_progress.view.*
import kotlin.math.roundToInt

/**
 *Created by ycb on 2019/11/28 0028
 */
internal class ProgressActivity : AppCompatActivity() {

  private var apkDownloader: ApkDownloader? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    requestedOrientation = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
      ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    } else {
      ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    invasionStatusBar(this)

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

    fun launch(context: Context, updateParams: Params) {
      val intent = Intent(context, ProgressActivity::class.java)
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      context.startActivity(intent)
      Companion.updateParams = updateParams
    }

    /**
     * Set the content layout full the StatusBar, but do not hide StatusBar.
     */
    private fun invasionStatusBar(activity: Activity) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val window = activity.window
        val decorView = window.decorView
        decorView.systemUiVisibility = (
            decorView.systemUiVisibility
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            )
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
      }
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