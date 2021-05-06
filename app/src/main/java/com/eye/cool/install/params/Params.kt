package com.eye.cool.install.params

import android.annotation.TargetApi
import android.os.Build
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.plus
import kotlin.coroutines.CoroutineContext

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
     * @param [progressParams]
     */
    fun progressParams(progressParams: ProgressParams): Builder {
      params.progressParams = progressParams
      return this
    }

    /**
     * The download related parameter settings
     *
     * @param [downloadParams]
     */
    fun downloadParams(downloadParams: DownloadParams): Builder {
      params.downloadParams = downloadParams
      return this
    }

    /**
     * Log tag {@link Log}
     * @param [tag] Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     */
    fun logTag(tag: String): Builder {
      params.logTag = tag
      return this
    }

    /**
     * Enable log, then you can see some download details, log tag 'download'
     *
     * @param [enable] default false
     */
    fun enableLog(enable: Boolean): Builder {
      params.enableLog = enable
      return this
    }

    /**
     * If you specify a custom download path, you need to add a FileProvider above 7.0
     *
     * @param [authority] The authority of a {@link FileProvider} defined in a
     *            {@code <provider>} element in your app's manifest.
     */
    fun authority(authority: String): Builder {
      params.authority = authority
      return this
    }

    /**
     * Callback the request result after requesting installation permission
     *
     * @param [installPermissionInvoker] Permission invoker callback after to request installation permissions
     */
    @TargetApi(Build.VERSION_CODES.O)
    fun installPermissionInvoker(installPermissionInvoker: InstallPermissionInvoker?): Builder {
      params.installPermissionInvoker = installPermissionInvoker
      return this
    }

    /**
     * The prompt related parameter settings
     *
     * @param [promptParams]
     */
    fun promptParams(promptParams: PromptParams): Builder {
      params.promptParams = promptParams
      return this
    }

    /**
     *
     * @param [fileParams]
     */
    fun fileParams(fileParams: FileParams): Builder {
      params.fileParams = fileParams
      return this
    }

    /**
     *
     * @param [notifyParams]
     */
    fun notifyParams(notifyParams: NotifyParams): Builder {
      params.notifyParams = notifyParams
      return this
    }

    fun build(): Params {
      if (params.downloadParams.downloadUrl.isNullOrEmpty()) {
        throw IllegalArgumentException("@link {DownloadParams.downloadUrl} can not be empty.")
      }
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
     * Permission invoker to request [android.Manifest.permission.READ_EXTERNAL_STORAGE]
     * and [android.Manifest.permission.WRITE_EXTERNAL_STORAGE].
     * or [android.Manifest.permission.MANAGE_EXTERNAL_STORAGE] on [Build.VERSION_CODES.R]
     *
     * @param invoker call on permission granted or denied
     */
    fun requestPermission(permissions: Array<String>, invoker: (Boolean) -> Unit)
  }
}