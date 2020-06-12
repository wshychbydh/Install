package com.eye.cool.install.ui

import android.app.AlertDialog
import android.content.Context
import android.view.View
import com.eye.cool.install.R
import com.eye.cool.install.params.PromptParams

/**
 *Created by ycb on 2019/12/16 0016
 */
internal class DefaultPrompt : PromptParams.IPrompt {

  override fun createView(
      context: Context,
      title: CharSequence?,
      content: CharSequence?,
      listener: PromptParams.IPromptListener
  ): View? {
    AlertDialog.Builder(context)
        .setCancelable(false)
        .setTitle(title)
        .setMessage(content)
        .setNegativeButton(R.string.install_prompt_cancel) { _, _ ->
          listener.onCancel()
        }
        .setPositiveButton(R.string.install_prompt_update) { _, _ ->
          listener.onUpgrade()
        }
        .show()
    return null
  }
}