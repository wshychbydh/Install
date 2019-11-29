package com.eye.cool.install.util

import android.util.Log

/**
 *Created by ycb on 2019/11/28 0028
 */
internal object DownloadLog {

  private const val TAG = "download"

  internal var enableLog: Boolean = false

  fun logI(msg: String) {
    if (enableLog && msg.isNotEmpty()) {
      Log.i(TAG, msg)
    }
  }

  fun logE(error: String) {
    if (enableLog && error.isNotEmpty()) {
      Log.e(TAG, error)
    }
  }
}