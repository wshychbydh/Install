package com.eye.cool.install.ui

import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import com.eye.cool.install.R
import com.eye.cool.install.support.complete
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Request install permissions.
 * Created cool on 2018/4/16.
 */
@TargetApi(Build.VERSION_CODES.O)
internal class InstallPermissionActivity : Activity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val requested = savedInstanceState?.getString(TAG_REQUEST_INSTALL_PACKAGES)
    if (requested.isNullOrEmpty()) {
      showInstallSettingDialog()
      invasionStatusBar(this)
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    outState.putString(TAG_REQUEST_INSTALL_PACKAGES, "requested")
    super.onSaveInstanceState(outState)
  }

  private fun showInstallSettingDialog() {
    val message = getString(R.string.install_permission_install_packages_setting_rationale, getAppName(this))
    AlertDialog.Builder(this)
        .setCancelable(false)
        .setTitle(R.string.install_permission_title_rationale)
        .setMessage(message)
        .setPositiveButton(R.string.install_permission_setting) { _, _ ->
          toInstallSetting()
        }
        .setNegativeButton(R.string.install_permission_no) { _, _ ->
          sRequestInstallPackageCallback?.complete(false)
          finish()
        }
        .show()
  }

  private fun toInstallSetting() {
    val intent = Intent(
        Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
        Uri.parse("package:$packageName")
    )
    startActivityForResult(intent, REQUEST_INSTALL_PACKAGES_CODE)
  }

  private fun getAppName(context: Context): String {
    val appInfo = context.packageManager.getApplicationInfo(context.packageName, 0) ?: return ""
    return context.packageManager.getApplicationLabel(appInfo) as? String ?: ""
  }

  override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
    return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == REQUEST_INSTALL_PACKAGES_CODE) {
      sRequestInstallPackageCallback?.complete(resultCode == RESULT_OK)
    }
    finish()
  }

  override fun onDestroy() {
    sRequestInstallPackageCallback = null
    super.onDestroy()
  }

  companion object {

    private const val TAG_REQUEST_INSTALL_PACKAGES = "request_install_packages"
    private const val REQUEST_INSTALL_PACKAGES_CODE = 4001

    private var sRequestInstallPackageCallback: CancellableContinuation<Boolean>? = null

    suspend fun requestInstallPermission(
        context: Context
    ) = suspendCancellableCoroutine<Boolean> {
      sRequestInstallPackageCallback = it
      val intent = Intent(context, InstallPermissionActivity::class.java)
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
