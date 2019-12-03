package com.eye.cool.install.support

/**
 *Created by ycb on 2019/12/3 0003
 */
interface SettingInvoker {

  /**
   * Installation permission invoker to request permissions.
   *
   * @param invoker call on install permission granted or denied
   */
  fun request(invoker: (Boolean) -> Unit)
}