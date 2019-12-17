package com.eye.cool.install.params

import android.view.Gravity
import com.eye.cool.install.support.IPrompt
import com.eye.cool.install.ui.DefaultPrompt

/**
 *Created by ycb on 2019/12/16 0016
 */
class PromptParams private constructor() {

  internal var title: CharSequence? = null
  internal var content: CharSequence? = null
  internal var prompt: IPrompt = DefaultPrompt()
  internal var cancelAble: Boolean = false
  internal var cancelOnTouchOutside: Boolean = false
  internal var dimAmount: Float = 0.6f
  internal var windowAnim: Int = 0
  internal var gravity: Int = Gravity.CENTER
  internal var width: Int = 0
  internal var height: Int = 0
  internal var x: Int = -1
  internal var y: Int = -1

  fun isValid(): Boolean {
    return (!title.isNullOrEmpty() || !content.isNullOrEmpty()) && prompt != null
  }

  class Builder {

    private val params = PromptParams()

    /**
     * Set the title displayed in the prompt.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    fun setTitle(title: CharSequence): Builder {
      params.title = title
      return this
    }

    /**
     * Set the content displayed in the prompt.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    fun setContent(content: CharSequence): Builder {
      params.content = content
      return this
    }

    /**
     * Set the prompt to display.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    fun setPrompt(prompt: IPrompt): Builder {
      params.prompt = prompt
      return this
    }

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
     * @see Gravity
     */
    fun gravity(gravity: Int): Builder {
      params.gravity = gravity
      return this
    }

    /**
     * Sets whether this dialog is dismissed when onBackPressed().
     *
     * @param cancelAble default false
     */
    fun cancelAble(cancelAble: Boolean): Builder {
      params.cancelAble = cancelAble
      return this
    }

    /**
     * Sets whether this dialog is dismissed when touched outside its window's bounds.
     *
     * @param cancelOnTouchOutside default false
     */
    fun cancelOnTouchOutside(cancelOnTouchOutside: Boolean): Builder {
      params.cancelOnTouchOutside = cancelOnTouchOutside
      return this
    }

    /**
     * This is the amount of dimming to apply.  Range is from 1.0 for completely opaque to 0.0 for no dim.
     *
     * @param dimAmount default 0.0
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
     * @param windowAnim default none
     */
    fun windowAnim(windowAnim: Int): Builder {
      params.windowAnim = windowAnim
      return this
    }

    /**
     * The size of the progress dialog
     *
     * @param width default 80%
     * @param height default wrap_content
     */
    fun size(width: Int, height: Int): Builder {
      this.params.width = width
      this.params.height = height
      return this
    }

    /**
     * The location that the prompt dialog will be shown
     *
     * @param x
     * @param y
     */
    fun setCoordinate(x: Int, y: Int): Builder {
      this.params.x = x
      this.params.y = y
      return this
    }

    fun build() = params
  }
}