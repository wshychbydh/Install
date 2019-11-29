package com.eye.cool.install

/**
 *Created by ycb on 2019/11/28 0028
 */
class Params private constructor() {

  internal var enableLog: Boolean = false
  internal var authority: String? = null
  internal var useDownloadManager: Boolean = true
  internal var downloadParams: DownloadParams = DownloadParams.Builder().build()
  internal var dialogParams: DialogParams = DialogParams.Builder().build()
  internal var forceUpdate: Boolean = false

  class Builder {

    private val params = Params()

    /**
     * The download progress dialog related parameter settings
     * @param dialogParams
     */
    fun setDialogParams(dialogParams: DialogParams): Builder {
      params.dialogParams = dialogParams
      return this
    }

    /**
     * The download related parameter settings
     * @param downloadParams
     */
    fun setDownloadParams(downloadParams: DownloadParams): Builder {
      params.downloadParams = downloadParams
      return this
    }

    /**
     * Enable log, then you can see some download details
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
      params.forceUpdate = forceUpdate
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
     * @param useDownloadManager default true
     */
    fun useDownloadManager(useDownloadManager: Boolean): Builder {
      params.useDownloadManager = useDownloadManager
      return this
    }

    fun build(): Params {
      return params
    }
  }
}