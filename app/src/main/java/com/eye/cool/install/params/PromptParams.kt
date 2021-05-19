package com.eye.cool.install.params

import android.content.Context
import android.view.View
import com.eye.cool.install.ui.DefaultPrompt

/**
 *Created by ycb on 2019/12/16 0016
 */
class PromptParams private constructor(
    internal val title: CharSequence?,
    internal val content: CharSequence?,
    internal val prompt: IPrompt?,
    internal val window: WindowParams?
) {


  companion object {
    inline fun build(block: Builder.() -> Unit) = Builder().apply(block).build()
  }

  fun isValid(): Boolean {
    return (!title.isNullOrEmpty() || !content.isNullOrEmpty()) || prompt != null
  }

  class Builder(
      var title: CharSequence? = null,
      var content: CharSequence? = null,
      var prompt: IPrompt? = DefaultPrompt(),
      var window: WindowParams = WindowParams.Builder().build()
  ) {

    /**
     * Set the [title] displayed in the prompt.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    fun title(title: CharSequence) = apply { this.title = title }

    /**
     * Set the [content] displayed in the prompt.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    fun content(content: CharSequence) = apply { this.content = content }

    /**
     * Set the [prompt] to display. null will not prompt
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    fun prompt(prompt: IPrompt?) = apply { this.prompt = prompt }

    /**
     * Prompt window's styles
     */
    fun windowParams(window: WindowParams) = apply { this.window = window }

    fun build() = PromptParams(
        title = title,
        content = content,
        prompt = prompt,
        window = window
    )
  }

  interface IPrompt {

    /**
     * Make a view to display.
     * If you don't want to provide views, return null and pop-up a custom dialog then callback promptListener
     *
     * @param [context]
     * @param [title]
     * @param [content]
     * @param [listener] User-selected callbacks
     * @return The view to display or null
     */
    fun createView(
        context: Context,
        title: CharSequence?,
        content: CharSequence?,
        listener: IPromptListener
    ): View?
  }

  interface IPromptListener {

    /**
     * User clicks cancel
     */
    fun onCancel()

    /**
     * User clicks upgrade
     */
    fun onUpgrade()
  }
}