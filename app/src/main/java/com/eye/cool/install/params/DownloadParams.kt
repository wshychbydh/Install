package com.eye.cool.install.params

import android.os.Environment

/**
 *Created by ycb on 2019/11/29 0029
 */
class DownloadParams private constructor() {

  internal var downloadUrl: String? = null
  internal var downloadDirType: String = Environment.DIRECTORY_DOWNLOADS
  internal var downloadSubPath: String? = null
  internal var downloadPath: String? = null
  internal var versionName: String? = null
  internal var versionCode: Int = 0

  class Builder {

    private val params = DownloadParams()

    /**
     * If you don't want to download the same apk twice, set the version of the apk to be downloaded
     *
     * @param versionCode The versionCode of apk
     * @param versionName The versionName of apk
     */
    fun setVersion(versionCode: Int, versionName: String): Builder {
      params.versionCode = versionCode
      params.versionName = versionName
      return this
    }

    /**
     * The download url of the apk to be downloaded
     *
     * @param downloadUrl The url must be valid , check by {@link URLUtil.isValidUrl()}
     */
    fun setDownloadUrl(downloadUrl: String): Builder {
      params.downloadUrl = downloadUrl
      return this
    }

    /**
     * The download dir where the apk will be download to
     *
     *@param dirType The directory type to pass to {@link Context#getExternalPubDir(String)} or {@link Context#getExternalFilesDir(String)}
     *@param subPath The path within the external directory, including the destination filename
     */
    fun setDownloadExternalDir(dirType: String, subPath: String): Builder {
      params.downloadDirType = dirType
      params.downloadSubPath = subPath
      return this
    }

    /**
     * The download path where the apk will be download to.
     * Make sure you have access to the path.
     *
     * @param downloadPath Default in external or file cache {download/${app_name}.apk}
     * {@link getExternalStoragePublicDirectory()} or {@link getExternalFilesDir()}
     */
    fun setDownloadPath(downloadPath: String): Builder {
      params.downloadPath = downloadPath
      return this
    }

    fun build(): DownloadParams {
      return params
    }
  }
}