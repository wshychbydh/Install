package com.eye.cool.install.params

import android.annotation.TargetApi
import android.os.Build
import android.webkit.URLUtil

/**
 *Created by ycb on 2019/11/28 0028
 */
class Params private constructor(
    internal val logTag: String,
    internal val enableLog: Boolean,
    internal val authority: String?,
    internal val permissionInvoker: PermissionInvoker?,
    internal val installPermissionInvoker: InstallPermissionInvoker?,
    internal val promptParams: PromptParams?,
    internal val downloadParams: DownloadParams,
    internal val progressParams: ProgressParams,
    internal val fileParams: FileParams,
    internal val notifyParams: NotifyParams
) {

  companion object {
    inline fun build(
        downloadParams: DownloadParams,
        block: Builder.() -> Unit
    ) = Builder(downloadParams = downloadParams).apply(block).build()
  }

  data class Builder(
      var downloadParams: DownloadParams,
      var logTag: String = "download",
      var enableLog: Boolean = false,
      var authority: String? = null,
      var permissionInvoker: PermissionInvoker? = null,
      var installPermissionInvoker: InstallPermissionInvoker? = null,
      var promptParams: PromptParams? = null,
      var progressParams: ProgressParams = ProgressParams.Builder().build(),
      var fileParams: FileParams = FileParams.Builder().build(),
      var notifyParams: NotifyParams = NotifyParams.Builder().build()
  ) {

    /**
     * The download progress dialog related parameter settings
     *
     * @param [progressParams]
     */
    fun progressParams(progressParams: ProgressParams) = apply {
      this.progressParams = progressParams
    }

    /**
     * The download related parameter settings
     *
     * @param [downloadParams]
     */
    fun downloadParams(downloadParams: DownloadParams) = apply {
      this.downloadParams = downloadParams
    }

    /**
     * Log tag {@link Log}
     * @param [tag] Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     */
    fun logTag(tag: String) = apply { this.logTag = tag }

    /**
     * Enable log, then you can see some download details, log tag 'download'
     *
     * @param [enable] default false
     */
    fun enableLog(enable: Boolean) = apply { this.enableLog = enable }

    /**
     * If you specify a custom download path, you need to add a FileProvider above 7.0
     *
     * @param [authority] The authority of a {@link FileProvider} defined in a
     *            {@code <provider>} element in your app's manifest.
     */
    fun authority(authority: String) = apply { this.authority = authority }

    /**
     * Callback the request result after requesting installation permission
     *
     * @param [installPermissionInvoker] Permission invoker callback after to request installation permissions
     */
    @TargetApi(Build.VERSION_CODES.O)
    fun installPermissionInvoker(installPermissionInvoker: InstallPermissionInvoker?) = apply {
      this.installPermissionInvoker = installPermissionInvoker
    }

    /**
     * The prompt related parameter settings
     *
     * @param [promptParams]
     */
    fun promptParams(promptParams: PromptParams) = apply { this.promptParams = promptParams }

    /**
     *
     * @param [fileParams]
     */
    fun fileParams(fileParams: FileParams) = apply { this.fileParams = fileParams }

    /**
     *
     * @param [notifyParams]
     */
    fun notifyParams(notifyParams: NotifyParams) = apply { this.notifyParams = notifyParams }

    fun build() = Params(
        logTag = logTag,
        enableLog = enableLog,
        authority = authority,
        permissionInvoker = permissionInvoker,
        installPermissionInvoker = installPermissionInvoker,
        promptParams = promptParams,
        downloadParams = downloadParams,
        progressParams = progressParams,
        fileParams = fileParams,
        notifyParams = notifyParams
    )
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