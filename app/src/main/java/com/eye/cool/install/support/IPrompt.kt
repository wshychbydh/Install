package com.eye.cool.install.support

import android.content.Context
import android.view.View

/**
 *Created by ycb on 2019/12/16 0016
 */
interface IPrompt {

  /**
   * Make a view to display.
   * If you don't want to provide views, return null and then pop-up a dialog here
   *
   * @param context
   * @param title
   * @param content
   * @param promptListener User-selected callbacks
   * @return The view to display
   */
  fun createView(
      context: Context,
      title: CharSequence?,
      content: CharSequence?,
      promptListener: IPromptListener
  ): View?
}