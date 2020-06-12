package com.eye.cool.install.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import com.eye.cool.install.params.PromptParams


/**
 *Created by ycb on 2019/12/16 0016
 */
internal class PromptDialog : DialogActivity(), PromptParams.IPromptListener {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setFinishOnTouchOutside(false)

    promptParams?.apply {
      val view = prompt?.createView(this@PromptDialog, title, content, this@PromptDialog)
      if (view != null) {
        setContentView(view)
        setupWindow(this)
      }
    }
  }

  private fun setupWindow(params: PromptParams) {

    window.decorView.setPadding(0, 0, 0, 0)

    val lp = window.attributes

    if (params.width > 0) {
      lp.width = params.width
    }
    if (params.height > 0) {
      lp.height = params.height
    }

    lp.windowAnimations = params.windowAnim

    if (params.x > 0) {
      lp.x = params.x
    }
    if (params.y > 0) {
      lp.y = params.y
    }
    lp.gravity = params.gravity
    lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
    lp.dimAmount = params.dimAmount
    window.attributes = lp
  }

  override fun onUpgrade() {
    promptListener?.onUpgrade()
    finish()
  }

  override fun onCancel() {
    promptListener?.onCancel()
    finish()
  }

  override fun finish() {
    super.finish()
    overridePendingTransition(0, 0)
  }

  override fun onBackPressed() {
  }

  override fun onDestroy() {
    super.onDestroy()
    promptParams = null
    promptListener = null
  }

  companion object {

    private var promptParams: PromptParams? = null
    private var promptListener: PromptParams.IPromptListener? = null

    fun show(
        context: Context,
        promptParams: PromptParams,
        promptListener: PromptParams.IPromptListener
    ) {
      this.promptParams = promptParams
      this.promptListener = promptListener
      val intent = Intent(context, PromptDialog::class.java)
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      context.startActivity(intent)
    }
  }
}