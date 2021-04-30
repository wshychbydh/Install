package com.eye.cool.install.params

class FileParams private constructor() {
  internal var isApk: Boolean = true
  internal var versionName: String? = null
  internal var versionCode: Int = 0
  internal var length: Long? = null
  internal var md5: String? = null

  class Builder {

    private val info = FileParams()

    /**
     * If you don't want to download the same apk twice, set the version of the apk to be downloaded
     *
     * @param [versionCode] The versionCode of apk
     * @param [versionName] The versionName of apk
     */
    fun version(versionCode: Int, versionName: String): Builder {
      info.versionCode = versionCode
      info.versionName = versionName
      return this
    }

    /**
     * Whether the downloaded file is executable for installation, default true
     * @param isApk
     */
    fun isApk(isApk: Boolean): Builder {
      info.isApk = isApk
      return this
    }

    /**
     * @param [length] The length of the download file used to determine if a new download is required
     */
    fun length(length: Long): Builder {
      info.length = length
      return this
    }

    /**
     * @param [md5] The md5 of the download file used to determine if a new download is required
     */
    fun md5(md5: String): Builder {
      info.md5 = md5
      return this
    }

    fun build() = info
  }
}