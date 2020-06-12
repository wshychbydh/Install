package com.eye.cool.install.ui

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager

/**
 * Request permissions.
 * Created cool on 2018/4/16.
 */
@TargetApi(Build.VERSION_CODES.M)
internal class PermissionActivity : Activity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    invasionStatusBar(this)
    val permissions = intent.getStringArrayExtra(REQUEST_PERMISSIONS)
    requestPermissions(permissions, REQUEST_PERMISSION_CODE)
  }

  override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
    return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event)
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    if (requestCode == REQUEST_PERMISSION_CODE) {
      val denied = grantResults.filter { it == PackageManager.PERMISSION_DENIED }
      sRequestPermissionListener?.invoke(denied.isEmpty())
    }
    finish()
  }

  override fun onDestroy() {
    sRequestPermissionListener = null
    super.onDestroy()
  }

  companion object {

    private const val REQUEST_PERMISSION_CODE = 3001
    private const val REQUEST_PERMISSIONS = "request_permissions"

    private var sRequestPermissionListener: ((Boolean) -> Unit)? = null

    @TargetApi(Build.VERSION_CODES.M)
    fun requestPermission(context: Context, permissions: Array<String>, callback: ((Boolean) -> Unit)? = null) {
      sRequestPermissionListener = callback
      val intent = Intent(context, PermissionActivity::class.java)
      intent.putExtra(REQUEST_PERMISSIONS, permissions)
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
        decorView.systemUiVisibility = (decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
      }
    }
  }
}
