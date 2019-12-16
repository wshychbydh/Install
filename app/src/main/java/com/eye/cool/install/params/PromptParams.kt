package com.eye.cool.install.params

import com.eye.cool.install.support.IPrompt
import com.eye.cool.install.ui.DefaultPrompt

/**
 *Created by ycb on 2019/12/16 0016
 */
class PromptParams private constructor() {

  internal var title: CharSequence? = null
  internal var content: CharSequence? = null
  internal var prompt: IPrompt = DefaultPrompt()

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
  }
}