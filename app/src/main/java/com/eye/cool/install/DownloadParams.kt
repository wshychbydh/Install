package com.eye.cool.install

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
     * @param versionCode the versionCode of apk
     * @param versionName the versionName of apk
     */
    fun setVersion(versionCode: Int, versionName: String): Builder {
      params.versionCode = versionCode
      params.versionName = versionName
      return this
    }

    /**
     * The downloadUrl of the apk to be downloaded
     * @param downloadUrl the url must be valid , check by {@link URLUtil.isValidUrl()}
     */
    fun setDownloadUrl(downloadUrl: String): Builder {
      params.downloadUrl = downloadUrl
      return this
    }

    /**
     *@param dirType the directory type to pass to {@link Context#getExternalPubDir(String)}
     *@param subPath the path within the external directory, including the destination filename
     */
    fun setDownloadExternalPubDir(dirType: String, subPath: String): Builder {
      params.downloadDirType = dirType
      params.downloadSubPath = subPath
      return this
    }

    /**
     * Where the apk will be downloaded
     * @param downloadPath default in external or file cache {download/${app_name}.apk}
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