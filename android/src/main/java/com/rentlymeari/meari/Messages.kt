package com.rentlymeari.meari

import android.util.Log
import androidx.compose.runtime.MutableState
import com.meari.sdk.MeariUser
import com.meari.sdk.bean.CameraInfo
import com.meari.sdk.bean.DeviceAlarmMessage
import com.meari.sdk.callback.IDeviceAlarmMessagesCallback
import com.meari.sdk.utils.SdkUtils


object Messages {
  fun getMessages(
    deviceId: Long,
    day: String,
    direction: Int = 0,
    index: String,
    eventType: Int = 3, // TODO: Add support for other event types
    aiType: IntArray = intArrayOf(0, 1, 2, 3, 4, 5, 6, 7),
    cameraInfo: CameraInfo,
    isLoading: MutableState<Boolean>,
  ) {
    if (cameraInfo.evt < 1) {
      isLoading.value = true
      MeariUser.getInstance().getAlertMsg(deviceId, day, object : IDeviceAlarmMessagesCallback {
        override fun onSuccess(
          deviceAlarmMessages: List<DeviceAlarmMessage>,
          cameraInfo: CameraInfo
        ) {
          isLoading.value = false
          Log.i("Doorbell", "getMessages success for evt < 1: $deviceAlarmMessages")
        }

        override fun onError(code: Int, error: String) {
          isLoading.value = false
          Log.i("Doorbell", "getMessages error for evt < 1: $code $error")
        }
      })
    } else if (cameraInfo.evt == 1) {
      isLoading.value = true
      MeariUser.getInstance().getAlertMsgWithVideo(
        deviceId,
        day,
        index,
        direction,
        eventType,
        aiType,
        object : IDeviceAlarmMessagesCallback {
          override fun onSuccess(
            deviceAlarmMessages: List<DeviceAlarmMessage>,
            cameraInfo: CameraInfo
          ) {
            isLoading.value = false
            Log.i("Doorbell", "getMessages success for evt = 1: $deviceAlarmMessages")
          }

          override fun onError(code: Int, error: String) {
            isLoading.value = false
            Log.i("Doorbell", "getMessages Error for evt = 1: $code $error")
          }
        })
    } else {
      Log.i("Doorbell", "Camera info .evt != 1 or < 1")
    }
  }

  fun decryptImage(
    url: String,
    img: ByteArray,
    serialNumber: String,
    allPwd: Set<String> = emptySet()
  ): Array<Any?>? {
    return SdkUtils.handleEncodedImage(url, img, serialNumber, allPwd)
  }
}
