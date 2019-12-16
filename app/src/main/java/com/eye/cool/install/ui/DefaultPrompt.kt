package com.eye.cool.install.ui

import android.app.AlertDialog
import android.content.Context
import com.eye.cool.install.R
import com.eye.cool.install.support.IPrompt

/**
 *Created by ycb on 2019/12/16 0016
 */
internal class DefaultPrompt : IPrompt {

  override fun show(context: Context, title: CharSequence?, content: CharSequence?, result: (update: Boolean) -> Unit) {

    AlertDialog.Builder(context)
        .setCancelable(false)
        .setTitle(title)
        .setMessage(content)
        .setNegativeButton(R.string.prompt_cancel) { _, _ ->
          result.invoke(false)
        }
        .setPositiveButton(R.string.prompt_update) { _, _ ->
          result.invoke(true)
        }
        .show()
  }

}