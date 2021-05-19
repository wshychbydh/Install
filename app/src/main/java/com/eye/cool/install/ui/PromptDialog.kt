package com.eye.cool.install.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.eye.cool.install.params.PromptParams
import com.eye.cool.install.params.WindowParams
import com.eye.cool.install.support.complete
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine


/**
 *Created by ycb on 2019/12/16 0016
 */
internal class PromptDialog : DialogActivity(), PromptParams.IPromptListener {

  override val windowParams: WindowParams? = promptParams?.window

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    promptParams?.apply {
      prompt?.createView(
          this@PromptDialog,
          title,
          content,
          this@PromptDialog
      )?.apply {
        setContentView(this)
      }
    }
  }

  override fun onUpgrade() {
    callback?.complete(true)
    finish()
  }

  override fun onCancel() {
    callback?.complete(false)
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
    callback = null
  }

  companion object {

    private var promptParams: PromptParams? = null
    private var callback: CancellableContinuation<Boolean>? = null

    suspend fun show(
        context: Context,
        promptParams: PromptParams
    ) = suspendCancellableCoroutine<Boolean> {
      this.promptParams = promptParams
      this.callback = it
      val intent = Intent(context, PromptDialog::class.java)
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      context.startActivity(intent)
    }
  }
}