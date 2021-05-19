package com.eye.cool.install.params

import android.content.DialogInterface
import android.content.res.Resources
import android.view.WindowManager
import androidx.annotation.StyleRes

/**
 * Created by ycb on 2019/11/28 0028
 */
class WindowParams private constructor(

    internal val width: Int?,
    internal val height: Int?,

    internal val gravity: Int?,
    internal val x: Int?,
    internal val y: Int?,

    val flags: Int?,
    @StyleRes
    internal val windowAnimations: Int?,
    internal val dimAmount: Float?,
    internal val alpha: Float?,


    internal val canceledOnTouchOutside: Boolean?,
    internal val cancelable: Boolean?,

    internal val horizontalMargin: Float?,
    internal val verticalMargin: Float?,

    internal val systemUiVisibility: Int?,
    internal val softInputMode: Int?,

    internal val onShowListener: DialogInterface.OnShowListener?,
    internal val onDismissListener: DialogInterface.OnDismissListener?,
    internal val onCancelListener: DialogInterface.OnCancelListener?,
    internal val onKeyListener: DialogInterface.OnKeyListener?
) {

  companion object {
    inline fun build(block: Builder.() -> Unit) = Builder().apply(block).build()
  }

  data class Builder(
      var width: Int? = null,
      var height: Int? = null,

      var gravity: Int? = null,
      var x: Int? = null,
      var y: Int? = null,

      var flags: Int = WindowManager.LayoutParams.FLAG_DIM_BEHIND,
      @StyleRes
      var windowAnimations: Int? = null,
      var dimAmount: Float? = null,
      var alpha: Float? = null,

      var canceledOnTouchOutside: Boolean = false,
      var cancelable: Boolean = false,

      var horizontalMargin: Float? = null,
      var verticalMargin: Float? = null,

      var systemUiVisibility: Int? = null,
      var softInputMode: Int? = null,

      var onShowListener: DialogInterface.OnShowListener? = null,
      var onDismissListener: DialogInterface.OnDismissListener? = null,
      var onCancelListener: DialogInterface.OnCancelListener? = null,
      var onKeyListener: DialogInterface.OnKeyListener? = null
  ) {

    /**
     * @see WindowManager.LayoutParams.gravity
     */
    fun gravity(gravity: Int) = apply { this.gravity = gravity }

    /**
     * Sets whether this dialog is dismissed when onBackPressed().
     * Only dismiss dialog, never stop task
     *
     * @param [cancelable] default false
     */
    fun cancelable(cancelable: Boolean) = apply { this.cancelable = cancelable }

    /**
     * Sets whether this dialog is dismissed when touched outside its window's bounds.
     * Only dismiss dialog, never stop task
     *
     * @param [cancelOnTouchOutside] default false
     */
    fun cancelOnTouchOutside(cancelOnTouchOutside: Boolean) = apply {
      this.canceledOnTouchOutside = cancelOnTouchOutside
    }

    /**
     * @see WindowManager.LayoutParams.dimAmount
     */
    fun dimAmount(dimAmount: Float) = apply { this.dimAmount = dimAmount }

    /**
     * @see WindowManager.LayoutParams.windowAnimations
     */
    fun windowAnimations(windowAnimations: Int) = apply {
      this.windowAnimations = windowAnimations
    }

    /**
     * @see WindowManager.LayoutParams.flags
     */
    fun flags(flags: Int) = apply { this.flags = flags }

    /**
     * @see WindowManager.LayoutParams.horizontalMargin
     */
    fun horizontalMargin(margin: Float) = apply { this.horizontalMargin = margin }

    /**
     * @see WindowManager.LayoutParams.verticalMargin
     */
    fun verticalMargin(margin: Float) = apply { this.verticalMargin = margin }

    /**
     * @see WindowManager.LayoutParams.systemUiVisibility
     */
    fun systemUiVisibility(visibility: Int) = apply { this.systemUiVisibility = visibility }

    /**
     * @see WindowManager.LayoutParams.softInputMode
     */
    fun softInputMode(softInputMode: Int) = apply { this.softInputMode = softInputMode }

    /**
     *  @see WindowManager.LayoutParams.x
     *  @see WindowManager.LayoutParams.y
     */
    fun position(x: Int, y: Int) = apply {
      this.x = x
      this.y = y
    }

    /**
     *  @see WindowManager.LayoutParams.alpha
     */
    fun alpha(alpha: Float) = apply { this.alpha = alpha }

    /**
     *  Width ratio of window and screen
     *  @param [ratio] 0.0~1.0
     */
    fun widthRatio(ratio: Float) = apply {
      if (ratio <= 0.0 && ratio > 1.0) throw IllegalArgumentException("Invalid ratio")
      val width = Resources.getSystem().displayMetrics.widthPixels
      this.width = (width * ratio).toInt()
    }

    /**
     *  Height ratio of window and screen
     *  @param [ratio] 0.0~1.0
     */
    fun heightRatio(ratio: Float) = apply {
      if (ratio <= 0.0 && ratio > 1.0) throw IllegalArgumentException("Invalid ratio")
      val height = Resources.getSystem().displayMetrics.heightPixels
      this.height = (height * ratio).toInt()
    }

    /**
     *  @see WindowManager.LayoutParams.width
     *  @param [width] default match_parent, min_width 260dp
     */
    fun width(width: Int) = apply { this.width = width }

    /**
     *  @see WindowManager.LayoutParams.height
     *  @param [height] default min_height 80dp
     */
    fun height(height: Int) = apply { this.height = height }

    /**
     *  Callback when window is showing
     */
    fun onShowListener(listener: DialogInterface.OnShowListener) = apply {
      this.onShowListener = listener
    }

    /**
     *  Callback when window is dismissed
     */
    fun onDismissListener(listener: DialogInterface.OnDismissListener) = apply {
      this.onDismissListener = listener
    }

    /**
     *  Callback when window is cancelled
     */
    fun onCancelListener(listener: DialogInterface.OnCancelListener) = apply {
      this.onCancelListener = listener
    }

    /**
     * Sets the callback that will be called if a key is dispatched to the window.
     */
    fun onKeyListener(listener: DialogInterface.OnKeyListener) = apply {
      this.onKeyListener = listener
    }

    fun build() = WindowParams(
        width = width,
        height = height,
        gravity = gravity,
        x = x,
        y = y,
        flags = flags,
        windowAnimations = windowAnimations,
        dimAmount = dimAmount,
        alpha = alpha,
        canceledOnTouchOutside = canceledOnTouchOutside,
        cancelable = cancelable,
        horizontalMargin = horizontalMargin,
        verticalMargin = verticalMargin,
        systemUiVisibility = systemUiVisibility,
        softInputMode = softInputMode,
        onShowListener = onShowListener,
        onDismissListener = onDismissListener,
        onCancelListener = onCancelListener,
        onKeyListener = onKeyListener
    )
  }
}