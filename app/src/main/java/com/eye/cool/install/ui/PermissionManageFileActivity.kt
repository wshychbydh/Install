package com.eye.cool.install.ui

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import com.eye.cool.install.support.complete
import kotlinx.coroutines.CancellableContinuation

/**
 *Created by ycb on 2021/1/22
 */
@TargetApi(Build.VERSION_CODES.R)
internal class PermissionManageFileActivity : Activity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    invasionStatusBar(this)
    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
    startActivityForResult(intent, REQUEST_FILE_SETTING_CODE)
  }

  override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
    return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == REQUEST_FILE_SETTING_CODE) {
      sRequestPermissionListener?.complete(Environment.isExternalStorageManager())
    }
    finish()
  }

  override fun onDestroy() {
    sRequestPermissionListener = null
    super.onDestroy()
  }

  companion object {
    private const val REQUEST_FILE_SETTING_CODE = 7012
    private var sRequestPermissionListener: CancellableContinuation<Boolean>? = null

    fun request(
        context: Context,
        callback: CancellableContinuation<Boolean>
    ) {
      sRequestPermissionListener = callback
      val intent = Intent(context, PermissionManageFileActivity::class.java)
      intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
      context.startActivity(intent)
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
}