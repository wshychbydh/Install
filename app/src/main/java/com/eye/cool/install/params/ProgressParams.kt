package com.eye.cool.install.params

import android.view.View
import androidx.annotation.UiThread

/**
 *Created by ycb on 2019/11/28 0028
 */
class ProgressParams private constructor(
    internal val progressView: View?,
    internal val progressListener: IProgressListener?,
    internal val progressTimeout: Long,
    internal val window: WindowParams?
) {

  companion object {
    inline fun build(block: Builder.() -> Unit) = Builder().apply(block).build()
  }

  data class Builder(
      var progressView: View? = null,
      var progressListener: IProgressListener? = null,
      var progressTimeout: Long = 10 * 60 * 1000L,
      var window: WindowParams = WindowParams.Builder().build()
  ) {

    /**
     * The progress view to be shown
     *
     * @param [view]
     */
    fun progressView(view: View) = apply { this.progressView = view }

    /**
     * Download listener
     *
     * @param [listener]
     */
    fun progressListener(listener: IProgressListener) = apply {
      this.progressListener = listener
    }

    /**
     * The maximum duration of time the progress dialog displays
     *
     * The download will not be cancelled after timeout
     *
     * @param [timeout] default 10 minutes
     */
    fun progressTimeout(timeout: Long) = apply { this.progressTimeout = timeout }

    /**
     * Prompt window's styles
     */
    fun windowParams(window: WindowParams) = apply { this.window = window }

    fun build() = ProgressParams(
        progressView = progressView,
        progressListener = progressListener,
        progressTimeout = progressTimeout,
        window = window
    )
  }

  interface IProgressListener {

    /**
     * Download started
     */
    @UiThread
    fun onStart()

    /**
     * Download progress
     *
     * @param [progress] 0~100
     */
    @UiThread
    fun onProgress(progress: Float)

    /**
     * Download finished
     *
     * @param [path] the file path download completed
     */
    @UiThread
    fun onFinished(path: String?)
  }
}