package com.eye.cool.install.support

/**
 *Created by ycb on 2019/12/3 0003
 */
interface PermissionInvoker {

  /**
   *Permission invoker to request permissions.
   *
   * @param permissions Permissions are need to be granted, include {@WRITE_EXTERNAL_STORAGE} and {@READ_EXTERNAL_STORAGE} and maybe {@CAMERA}
   * @param invoker call on permission granted or denied
   */
  fun request(permissions: Array<String>, invoker: (Boolean) -> Unit)
}