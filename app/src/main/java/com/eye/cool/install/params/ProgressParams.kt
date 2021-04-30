package com.eye.cool.install.params

import android.view.Gravity
import android.view.View
import androidx.annotation.UiThread

/**
 *Created by ycb on 2019/11/28 0028
 */
class ProgressParams private constructor() {

  internal var progressView: View? = null
  internal var listener: IProgressListener? = null
  internal var progressTimeout: Long = 10 * 60 * 1000L
  internal var cancelAble: Boolean = false
  internal var cancelOnTouchOutside: Boolean = false
  internal var dimAmount: Float = 0.6f
  internal var windowAnim: Int = 0
  internal var gravity: Int = Gravity.CENTER
  internal var width: Int = 0
  internal var height: Int = 0
  internal var x: Int = -1
  internal var y: Int = -1

  class Builder {

    private val params = ProgressParams()

    /**
     * Placement of window within the screen as per {@link Gravity}.  Both
     * {@link Gravity#apply(int, int, int, android.graphics.Rect, int, int,
     * android.graphics.Rect) Gravity.apply} and
     * {@link Gravity#applyDisplay(int, android.graphics.Rect, android.graphics.Rect)
     * Gravity.applyDisplay} are used during window layout, with this value
     * given as the desired gravity.  For example you can specify
     * {@link Gravity#DISPLAY_CLIP_HORIZONTAL Gravity.DISPLAY_CLIP_HORIZONTAL} and
     * {@link Gravity#DISPLAY_CLIP_VERTICAL Gravity.DISPLAY_CLIP_VERTICAL} here
     * to control the behavior of
     * {@link Gravity#applyDisplay(int, android.graphics.Rect, android.graphics.Rect)
     * Gravity.applyDisplay}.
     *
     * @see [Gravity]
     */
    fun gravity(gravity: Int): Builder {
      params.gravity = gravity
      return this
    }

    /**
     * Download listener
     *
     * @param [listener]
     */
    fun progress(listener: IProgressListener): Builder {
      params.listener = listener
      return this
    }

    /**
     * The progress view to be shown
     *
     * @param [view]
     */
    fun progressView(view: View): Builder {
      params.progressView = view
      return this
    }

    /**
     * The maximum duration of time the progress dialog displays
     *
     * The download will not be cancelled after timeout
     *
     * @param [timeout] default 10 minutes
     */
    fun progressTimeout(timeout: Long): Builder {
      params.progressTimeout = timeout
      return this
    }

    /**
     * Sets whether this dialog is dismissed when onBackPressed().
     * Only dismiss dialog, never stop task
     *
     * @param [cancelAble] default false
     */
    fun cancelAble(cancelAble: Boolean): Builder {
      params.cancelAble = cancelAble
      return this
    }

    /**
     * Sets whether this dialog is dismissed when touched outside its window's bounds.
     * Only dismiss dialog, never stop task
     *
     * @param [cancelOnTouchOutside] default false
     */
    fun cancelOnTouchOutside(cancelOnTouchOutside: Boolean): Builder {
      params.cancelOnTouchOutside = cancelOnTouchOutside
      return this
    }

    /**
     * This is the amount of dimming to apply.  Range is from 1.0 for completely opaque to 0.0 for no dim.
     *
     * @param [dimAmount] default 0.0
     */
    fun dimAmount(dimAmount: Float): Builder {
      params.dimAmount = dimAmount
      return this
    }

    /**
     * A style resource defining the animations to use for this window.
     * This must be a system resource; it can not be an application resource
     * because the window manager does not have access to applications.
     *
     * @param [windowAnim] default none
     */
    fun windowAnim(windowAnim: Int): Builder {
      params.windowAnim = windowAnim
      return this
    }

    /**
     * The size of the progress dialog
     *
     * @param [width] default match_parent, min_width 260dp
     * @param [height] default 80dp
     */
    fun size(width: Int, height: Int): Builder {
      this.params.width = width
      this.params.height = height
      return this
    }

    /**
     * The location that the progress dialog will be shown
     *
     * @param [x]
     * @param [y]
     */
    fun setCoordinate(x: Int, y: Int): Builder {
      this.params.x = x
      this.params.y = y
      return this
    }

    fun build(): ProgressParams {
      return params
    }
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