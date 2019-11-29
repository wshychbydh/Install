package com.eye.cool.install

import android.view.View

/**
 *Created by ycb on 2019/11/28 0028
 */
interface IProgress {

  /**
   * The progress view to be shown
   *
   * @return view
   */
  fun getProgressView(): View

  /**
   * Download begin to start
   */
  fun onStart() {}

  /**
   * Download progress
   *
   * @param progress
   */
  fun onProgress(progress: Float)

  /**
   * Download finished
   *
   * @param path the apk path download completed
   * @return handled install, default false
   */
  fun onFinished(path: String): Boolean
}