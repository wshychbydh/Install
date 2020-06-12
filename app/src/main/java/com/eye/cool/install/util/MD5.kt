package com.eye.cool.install.util

import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

/**
 * Created by ycb on 2020/6/10 0010
 */
object MD5 {

  fun getFileMD5(file: File): String? {
    if (!file.isFile) {
      return null
    }
    var digest: MessageDigest?
    var fis: FileInputStream?
    val buffer = ByteArray(1024)
    var len: Int
    try {
      digest = MessageDigest.getInstance("MD5")
      fis = FileInputStream(file)
      while (fis.read(buffer, 0, 1024).also { len = it } != -1) {
        digest.update(buffer, 0, len)
      }
      fis.close()
    } catch (e: Exception) {
      e.printStackTrace()
      return null
    }
    return bytesToHexString(digest.digest())?.toUpperCase() ?: return null
  }

  private fun bytesToHexString(src: ByteArray?): String? {
    val stringBuilder = StringBuilder("")
    if (src == null || src.isEmpty()) {
      return null
    }
    for (i in src.indices) {
      val v: Int = src[i].toInt() and 0xFF
      val hv = Integer.toHexString(v)
      if (hv.length < 2) {
        stringBuilder.append(0)
      }
      stringBuilder.append(hv)
    }
    return stringBuilder.toString()
  }
}