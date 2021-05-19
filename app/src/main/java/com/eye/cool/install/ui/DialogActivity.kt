package com.eye.cool.install.ui

import android.app.Activity
import android.content.DialogInterface
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.eye.cool.install.params.WindowParams
import com.eye.cool.install.support.IWindowConfig

/**
 *Created by ycb on 2019/12/16 0016
 */
internal abstract class DialogActivity : AppCompatActivity(), DialogInterface, IWindowConfig {

  abstract val windowParams: WindowParams?

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    invasionStatusBar(this)

    setFinishOnTouchOutside(windowParams?.canceledOnTouchOutside ?: false)

    windowParams?.apply {
      val lp = configLayoutParams(this, window)
      onWindowAttributesChanged(lp)
    }
  }

  override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
    val handled = windowParams?.onKeyListener?.onKey(this, keyCode, event)
    return handled ?: super.onKeyDown(keyCode, event)
  }

  override fun cancel() {
    if (isFinishing) return
    windowParams?.onCancelListener?.onCancel(this)
    finish()
  }

  override fun dismiss() {
    if (isFinishing) return
    windowParams?.onDismissListener?.onDismiss(this)
    finish()
  }

  override fun onBackPressed() {
    if (windowParams?.cancelable == true) {
      dismiss()
    }
  }

  override fun finish() {
    super.finish()
    overridePendingTransition(0, 0)
  }

  companion object {

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
}