package com.rentlymeari.meari

import android.util.Log
import com.meari.sdk.MeariUser
import com.meari.sdk.bean.CameraInfo
import com.meari.sdk.bean.DeviceAlarmMessage
import com.meari.sdk.bean.DeviceMessageStatus
import com.meari.sdk.bean.SystemMessage
import com.meari.sdk.callback.IDeviceAlarmMessagesCallback
import com.meari.sdk.callback.IDeviceMessageStatusCallback
import com.meari.sdk.callback.ISystemMessageCallback
import com.meari.sdk.utils.SdkUtils


object Messages {
  fun getMessages(
    deviceId: Long,
    day: String,
    direction: Int = 0,
    index: String,
    eventType: Int = 3, // TODO: Add support for other event types
    aiType: IntArray = intArrayOf(0, 1, 2, 3, 4, 5, 6, 7),
    cameraInfo: CameraInfo
  ) {
    Log.i("Harish messa", "inside get messages")
    if (cameraInfo.evt < 1) {
      Log.i("Harish messa", "inside < 1")
      MeariUser.getInstance().getAlertMsg(deviceId, day, object : IDeviceAlarmMessagesCallback {
        override fun onSuccess(
          deviceAlarmMessages: List<DeviceAlarmMessage>,
          cameraInfo: CameraInfo
        ) {
          Log.i("Doorbell getMessages success for evt < 1", deviceAlarmMessages.toString())
        }

        override fun onError(code: Int, error: String) {
          Log.i("Doorbell getMessages error for evt < 1", "$code $error")
        }
      })
    } else if (cameraInfo.evt == 1) {
      Log.i("Harish messa", "inside == 1")
      Log.i("Harish deviceId", deviceId.toString())
      Log.i("Harish day", day)
      Log.i("Harish index", index)
      Log.i("Harish eventType", eventType.toString())
      Log.i("Harish aiType", aiType.toString())
      Log.i("Harish direction", direction.toString())

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
            Log.i("Harish message sucess", deviceAlarmMessages.toString())
            Log.i("Doorbell getMessages success for evt = 1", "success")
          }

          override fun onError(code: Int, error: String) {
            Log.i("Harish message", "failure $code $error")
            Log.i("Doorbell getMessages Error for evt = 1", "$code $error")
          }
        })
    }
  }

  fun systemMessages () {
    MeariUser.getInstance().getSystemMessage(object : ISystemMessageCallback {
      override fun onSuccess(systemMessageList: List<SystemMessage>) {
        Log.i("Harish system message sucess", systemMessageList.toString())
      }

      override fun onError(code: Int, error: String) {
        Log.i("Harish", "system message error $code $error")
      }
    })
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
