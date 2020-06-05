package com.eye.cool.install.params

import android.annotation.TargetApi
import android.os.Build
import com.eye.cool.install.support.PermissionInvoker
import com.eye.cool.install.support.SettingInvoker

/**
 *Created by ycb on 2019/11/28 0028
 */
class Params private constructor() {

  internal var enableLog: Boolean = false
  internal var authority: String? = null
  internal var useDownloadManager: Boolean = true
  internal var downloadParams: DownloadParams = DownloadParams.Builder().build()
  internal var progressParams: ProgressParams = ProgressParams.Builder().build()
  internal var promptParams: PromptParams? = null
  internal var forceDownload: Boolean = false
  internal var permissionInvoker: PermissionInvoker? = null
  internal var settingInvoker: SettingInvoker? = null

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
     * Enable log, then you can see some download details
     *
     * @param enable default false
     */
    fun enableLog(enable: Boolean): Builder {
      params.enableLog = enable
      return this
    }

    /**
     * Forced to upgrade. The upgrade progress box displays within the application
     *
     * @param forceUpdate  default false
     */
    fun forceUpdate(forceUpdate: Boolean): Builder {
      params.forceDownload = forceUpdate
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
     * Use DownloadManager to download, but sometimes there are problems, then you can set it to false
     *
     * @param useDownloadManager default true
     */
    fun useDownloadManager(useDownloadManager: Boolean): Builder {
      params.useDownloadManager = useDownloadManager
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
     * @param settingInvoker Permission invoker callback after to request installation permissions
     */
    @TargetApi(Build.VERSION_CODES.O)
    fun setSettingInvoker(settingInvoker: SettingInvoker?): Builder {
      params.settingInvoker = settingInvoker
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

    fun build(): Params {
      return params
    }
  }
}