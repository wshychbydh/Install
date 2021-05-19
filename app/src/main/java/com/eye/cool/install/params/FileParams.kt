package com.eye.cool.install.params

class FileParams private constructor(
    internal val isApk: Boolean,
    internal val versionName: String?,
    internal val versionCode: Int?,
    internal val length: Long?,
    internal val md5: String?,
) {

  companion object {
    inline fun build(block: Builder.() -> Unit) = Builder().apply(block).build()
  }

  data class Builder(
      var isApk: Boolean = true,
      var versionName: String? = null,
      var versionCode: Int? = null,
      var length: Long? = null,
      var md5: String? = null,
  ) {

    /**
     * If you don't want to download the same apk twice, set the version of the apk to be downloaded
     *
     * @param [versionCode] The versionCode of apk
     * @param [versionName] The versionName of apk
     */
    fun version(versionCode: Int, versionName: String) = apply {
      this.versionCode = versionCode
      this.versionName = versionName
      return this
    }

    /**
     * Whether the downloaded file is executable for installation, default true
     * @param isApk
     */
    fun isApk(isApk: Boolean) = apply { this.isApk = isApk }

    /**
     * @param [length] The length of the download file used to determine if a new download is required
     */
    fun length(length: Long) = apply { this.length = length }

    /**
     * @param [md5] The md5 of the download file used to determine if a new download is required
     */
    fun md5(md5: String) = apply { this.md5 = md5 }

    fun build() = FileParams(
        isApk = isApk,
        versionName = versionName,
        versionCode = versionCode,
        length = length,
        md5 = md5
    )
  }
}