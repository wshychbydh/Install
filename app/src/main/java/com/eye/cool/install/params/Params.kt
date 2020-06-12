package com.eye.cool.install.params

import android.annotation.TargetApi
import android.os.Build

/**
 *Created by ycb on 2019/11/28 0028
 */
class Params private constructor() {

  internal var logTag: String = "download"
  internal var enableLog: Boolean = false
  internal var authority: String? = null
  internal var permissionInvoker: PermissionInvoker? = null
  internal var installPermissionInvoker: InstallPermissionInvoker? = null
  internal var promptParams: PromptParams? = null
  internal var downloadParams: DownloadParams = DownloadParams.Builder().build()
  internal var progressParams: ProgressParams = ProgressParams.Builder().build()
  internal var fileParams: FileParams = FileParams.Builder().build()
  internal var notifyParams: NotifyParams = NotifyParams.Builder().build()

  class Builder {

    private val params = Params()

    /**
     * The download progress dialog related parameter settings
     *
     * @param progressParams
     */
    fun setProgressParams(progressParams: ProgressParams): Builder {
      params.progressParams = progressParams
      return this
    }

    /**
     * The download related parameter settings
     *
     * @param downloadParams
     */
    fun setDownloadParams(downloadParams: DownloadParams): Builder {
      params.downloadParams = downloadParams
      return this
    }

    /**
     * Log tag {@link Log}
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     */
    fun setLogTag(tag: String): Builder {
      params.logTag = tag
      return this
    }

    /**
     * Enable log, then you can see some download details, log tag 'download'
     *
     * @param enable default false
     */
    fun enableLog(enable: Boolean): Builder {
      params.enableLog = enable
      return this
    }

    /**
     * If you specify a custom download path, you need to add a FileProvider above 7.0
     *
     * @param authority The authority of a {@link FileProvider} defined in a
     *            {@code <provider>} element in your app's manifest.
     */
    fun setAuthority(authority: String): Builder {
      params.authority = authority
      return this
    }

    /**
     * Callback the request result after requesting storage permission
     *
     * @param permissionInvoker Permission invoker callback after to request storage permissions
     */
    @TargetApi(Build.VERSION_CODES.M)
    fun setPermissionInvoker(permissionInvoker: PermissionInvoker?): Builder {
      params.permissionInvoker = permissionInvoker
      return this
    }

    /**
     * Callback the request result after requesting installation permission
     *
     * @param installPermissionInvoker Permission invoker callback after to request installation permissions
     */
    @TargetApi(Build.VERSION_CODES.O)
    fun setInstallPermissionInvoker(installPermissionInvoker: InstallPermissionInvoker?): Builder {
      params.installPermissionInvoker = installPermissionInvoker
      return this
    }

    /**
     * The prompt related parameter settings
     *
     * @param promptParams
     */
    fun setPromptParams(promptParams: PromptParams): Builder {
      params.promptParams = promptParams
      return this
    }

    /**
     *
     * @param fileParams
     */
    fun setFileParams(fileParams: FileParams): Builder {
      params.fileParams = fileParams
      return this
    }

    /**
     *
     * @param notifyParams
     */
    fun setNotifyParams(notifyParams: NotifyParams): Builder {
      params.notifyParams = notifyParams
      return this
    }

    fun build(): Params {
      return params
    }
  }

  interface InstallPermissionInvoker {

    /**
     * Installation permission invoker to request permissions.
     *
     * @param invoker call on com.eye.install permission granted or denied
     */
    fun request(invoker: (Boolean) -> Unit)
  }

  interface PermissionInvoker {

    /**
     *Permission invoker to request permissions.
     *
     * @param permissions Permissions are need to be granted, include {@WRITE_EXTERNAL_STORAGE} and {@READ_EXTERNAL_STORAGE} and maybe {@CAMERA}
     * @param invoker call on permission granted or denied
     */
    fun request(permissions: Array<String>, invoker: (Boolean) -> Unit)
  }
}