package com.eye.cool.install.support

import android.app.Dialog
import android.view.Window
import android.view.WindowManager
import com.eye.cool.install.params.WindowParams

/**
 * Created by cool on 18-3-9
 */
internal interface IWindowConfig {

  fun setupDialog(params: WindowParams, dialog: Dialog) {

    if (params.cancelable != null) {
      dialog.setCancelable(params.cancelable)
    }

    if (params.canceledOnTouchOutside != null) {
      dialog.setCanceledOnTouchOutside(params.canceledOnTouchOutside)
    }

    dialog.setOnShowListener(params.onShowListener)
    dialog.setOnKeyListener(params.onKeyListener)

    dialog.onWindowAttributesChanged(configLayoutParams(params, dialog.window ?: return))
  }

  fun configLayoutParams(
      params: WindowParams,
      window: Window
  ): WindowManager.LayoutParams {

    if (params.windowAnimations != null) {
      window.setWindowAnimations(params.windowAnimations!!)
    }

    val layoutParams = window.attributes

    if (params.alpha != null) {
      layoutParams.alpha = params.alpha
    }

    if (params.x != null) {
      layoutParams.x = params.x
    }

    if (params.y != null) {
      layoutParams.y = params.y
    }

    if (params.gravity != null) {
      layoutParams.gravity = params.gravity
    }

    if (params.dimAmount != null) {
      layoutParams.dimAmount = params.dimAmount
    }

    if (params.horizontalMargin != null) {
      layoutParams.horizontalMargin = params.horizontalMargin
    }

    if (params.verticalMargin != null) {
      layoutParams.verticalMargin = params.verticalMargin
    }

    if (params.width == null || (params.width <= 0 && params.width != -1)) {
      layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
    } else {
      layoutParams.width = params.width
    }

    if (params.height == null || (params.height <= 0 && params.height != -1)) {
      layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
    } else {
      layoutParams.height = params.height
    }

    if (params.systemUiVisibility != null) {
      layoutParams.systemUiVisibility = params.systemUiVisibility
    }

    if (params.softInputMode != null) {
      layoutParams.softInputMode = params.softInputMode!!
    }

    if (params.flags != null) {
      layoutParams.flags = params.flags
    }

    return layoutParams
  }
}