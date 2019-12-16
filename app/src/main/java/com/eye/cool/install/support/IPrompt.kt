package com.eye.cool.install.support

import android.content.Context

/**
 *Created by ycb on 2019/12/16 0016
 */
interface IPrompt {

  fun show(context: Context, title: CharSequence?, content: CharSequence?, result: (update: Boolean) -> Unit)
}