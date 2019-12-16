package com.eye.cool.install.support

/**
 *Created by ycb on 2019/12/16 0016
 */
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