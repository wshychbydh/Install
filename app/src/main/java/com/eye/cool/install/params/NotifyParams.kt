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
class NotifyParams private constructor() : Parcelable {

  internal var notifyId: Int = "install".hashCode()
  internal var channelId: String = "install"
  internal var notifyChannel: NotificationChannel? = null
  internal var notification: Notification? = null

  class Builder {
    private val params = NotifyParams()

    /**
     * @param notifyId The identifier for this notification as per
     * {@link NotificationManager#notify(int, Notification)
     * NotificationManager.notify(int, Notification)}; must not be 0.
     */
    fun setNotifyId(notifyId: Int): Builder {
      params.notifyId = notifyId
      return this
    }

    /**
     * {@link NotificationChannel#ChannelId and Notification#ChannelId}
     *
     * @param channelId The constructed Notification will be posted on this NotificationChannel.
     */
    fun setChannelId(channelId: String): Builder {
      params.channelId = channelId
      return this
    }

    /**
     * Creates a notification channel that notifications can be posted to.
     *
     * @param channel  the channel to create.  Note that the created channel may differ from this
     *                 value. If the provided channel is malformed, a RemoteException will be
     *                 thrown.
     */
    fun setNotificationChannel(channel: NotificationChannel): Builder {
      params.notifyChannel
      return this
    }

    /**
     * @param notification The Notification to be displayed.
     */
    fun setNotification(notification: Notification): Builder {
      params.notification = notification
      return this
    }

    fun build() = params
  }

  constructor(parcel: Parcel) : this() {
    notifyId = parcel.readInt()
    notifyChannel = parcel.readParcelable(NotificationChannel::class.java.classLoader)
    notification = parcel.readParcelable(Notification::class.java.classLoader)
  }

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeInt(notifyId)
    parcel.writeParcelable(notifyChannel, flags)
    parcel.writeParcelable(notification, flags)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<NotifyParams> {
    override fun createFromParcel(parcel: Parcel): NotifyParams {
      return NotifyParams(parcel)
    }

    override fun newArray(size: Int): Array<NotifyParams?> {
      return arrayOfNulls(size)
    }
  }
}