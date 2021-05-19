package com.eye.cool.install.params

import android.app.Notification
import android.app.NotificationChannel
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi

/**
 * Created by ycb on 2020/6/11 0011
 */
@RequiresApi(Build.VERSION_CODES.O)
class NotifyParams private constructor(
    internal val notifyId: Int,
    internal val channelId: String,
    internal val notifyChannel: NotificationChannel?,
    internal val notification: Notification?,
) : Parcelable {

  constructor(parcel: Parcel) : this(
      parcel.readInt(),
      parcel.readString() ?: "install",
      parcel.readParcelable(NotificationChannel::class.java.classLoader),
      parcel.readParcelable(Notification::class.java.classLoader)) {
  }

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeInt(notifyId)
    parcel.writeString(channelId)
    parcel.writeParcelable(notifyChannel, flags)
    parcel.writeParcelable(notification, flags)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object {

    inline fun build(block: Builder.() -> Unit) = Builder().apply(block).build()

    @JvmField
    val CREATOR = object : Parcelable.Creator<NotifyParams> {
      override fun createFromParcel(parcel: Parcel): NotifyParams {
        return NotifyParams(parcel)
      }

      override fun newArray(size: Int): Array<NotifyParams?> {
        return arrayOfNulls(size)
      }
    }
  }

  data class Builder(
      var notifyId: Int = "install".hashCode(),
      var channelId: String = "install",
      var notifyChannel: NotificationChannel? = null,
      var notification: Notification? = null,
  ) {

    /**
     * @param [notifyId] The identifier for this notification as per
     * {@link NotificationManager#notify(int, Notification)
     * NotificationManager.notify(int, Notification)}; must not be 0.
     */
    fun notifyId(notifyId: Int) = apply { this.notifyId = notifyId }

    /**
     * {@link NotificationChannel#ChannelId and Notification#ChannelId}
     *
     * @param [channelId] The constructed Notification will be posted on this NotificationChannel.
     */
    fun channelId(channelId: String) = apply { this.channelId = channelId }

    /**
     * Creates a notification channel that notifications can be posted to.
     *
     * @param [channel] The channel to create.  Note that the created channel may differ from this
     *                 value. If the provided channel is malformed, a RemoteException will be
     *                 thrown.
     */
    fun notificationChannel(channel: NotificationChannel) = apply { this.notifyChannel }

    /**
     * @param [notification] The Notification to be displayed.
     */
    fun notification(notification: Notification) = apply { this.notification = notification }

    fun build() = NotifyParams(
        notifyId = notifyId,
        channelId = channelId,
        notifyChannel = notifyChannel,
        notification = notification
    )
  }
}