package com.eye.cool.install.params

import android.content.DialogInterface
import android.content.res.Resources
import android.view.Gravity
import android.view.WindowManager
import androidx.annotation.StyleRes

/**
 * Created by ycb on 2019/11/28 0028
 */
class WindowParams private constructor(
    internal val themeStyle: Int?,

    internal val width: Int?,
    internal val height: Int?,

    internal val gravity: Int?,
    internal val x: Int?,
    internal val y: Int?,

    val windowFlags: Int?,
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
      var themeStyle: Int? = null,

      var width: Int? = null,
      var height: Int? = null,

      var gravity: Int? = null,
      var x: Int? = null,
      var y: Int? = null,

      var windowFlags: Int? = WindowManager.LayoutParams.FLAG_DIM_BEHIND,
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
    fun gravity(gravity: Int) = apply { this.gravity = gravity }

    /**
     * Sets whether this dialog is dismissed when onBackPressed().
     * Only dismiss dialog, never stop task
     *
     * @param [cancelAble] default false
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
     * This is the amount of dimming to apply.  Range is from 1.0 for completely opaque to 0.0 for no dim.
     *
     * @param [dimAmount] default 0.0
     */
    fun dimAmount(dimAmount: Float) = apply { this.dimAmount = dimAmount }

    /**
     * A style resource defining the animations to use for this window.
     * This must be a system resource; it can not be an application resource
     * because the window manager does not have access to applications.
     *
     * @param [windowAnim] default none
     */
    fun windowAnim(windowAnim: Int) = apply { this.windowAnimations = windowAnim }

    /**
     * The size of the progress dialog
     *
     * @param [width] default match_parent, min_width 260dp
     * @param [height] default min_height 80dp
     */
    fun size(width: Int, height: Int) = apply {
      this.width = width
      this.height = height
    }

    /**
     * The location that the progress dialog will be shown
     *
     * @param [x]
     * @param [y]
     */
    fun setCoordinate(x: Int, y: Int) = apply {
      this.x = x
      this.y = y
    }

    /**
     * Window flag
     * @param [flags] @link{WindowManager.flags}, default FLAG_DIM_BEHIND
     */
    fun windowFlags(flags: Int) = apply { this.windowFlags = flags }

    fun horizontalMargin(margin: Float) = apply { this.horizontalMargin = margin }

    fun verticalMargin(margin: Float) = apply { this.verticalMargin = margin }

    fun systemUiVisibility(visibility: Int) = apply { this.systemUiVisibility = visibility }

    fun softInputMode(softInputMode: Int) = apply { this.softInputMode = softInputMode }

    fun position(x: Int, y: Int) = apply {
      this.x = x
      this.y = y
    }

    fun alpha(alpha: Float) = apply { this.alpha = alpha }

    fun widthRatio(ratio: Float) = apply {
      if (ratio <= 0.0 && ratio > 1.0) throw IllegalArgumentException("Invalid ratio")
      val width = Resources.getSystem().displayMetrics.widthPixels
      this.width = (width * ratio).toInt()
    }

    fun heightRatio(ratio: Float) = apply {
      if (ratio <= 0.0 && ratio > 1.0) throw IllegalArgumentException("Invalid ratio")
      val height = Resources.getSystem().displayMetrics.heightPixels
      this.height = (height * ratio).toInt()
    }

    fun width(width: Int) = apply { this.width = width }

    fun height(height: Int) = apply { this.height = height }

    fun setWindowAnimations(@StyleRes anim: Int) = apply { this.windowAnimations = anim }

    fun onShowListener(listener: DialogInterface.OnShowListener) = apply {
      this.onShowListener = listener
    }

    fun onDismissListener(listener: DialogInterface.OnDismissListener) = apply {
      this.onDismissListener = listener
    }

    fun onCancelListener(listener: DialogInterface.OnCancelListener) = apply {
      this.onCancelListener = listener
    }

    fun onKeyListener(listener: DialogInterface.OnKeyListener) = apply {
      this.onKeyListener = listener
    }

    fun build() = WindowParams(
        themeStyle = themeStyle,
        width = width,
        height = height,
        gravity = gravity,
        x = x,
        y = y,
        windowFlags = windowFlags,
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