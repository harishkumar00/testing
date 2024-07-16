package com.rentlymeari.meari

import android.util.Log
import androidx.compose.runtime.MutableState
import com.meari.sdk.MeariUser
import com.meari.sdk.bean.SDCardInfo
import com.meari.sdk.callback.ISDCardFormatCallback
import com.meari.sdk.callback.ISDCardInfoCallback
import com.meari.sdk.callback.ISetDeviceParamsCallback

object SDCard {

  fun getSDCardInfo(
    info: MutableState<SDCardInfo>,
    isLoading: MutableState<Boolean>,
  ) {
    isLoading.value = true
    MeariUser.getInstance().getSDCardInfo(object : ISDCardInfoCallback {
      override fun onSuccess(sdCardInfo: SDCardInfo) {
        info.value = sdCardInfo
        isLoading.value = false
      }

      override fun onFailed(errorCode: Int, errorMsg: String) {
        isLoading.value = false
      }
    })
  }

  fun setSDCardRecordType(
    type: MutableState<Int?>,
    newMode: Int?,
    isLoading: MutableState<Boolean>,
    duration: Int = 30
  ) {
    // type: 0-Event Recording, 1-All Day Recording
    if (newMode != null) {
      isLoading.value = true
      MeariUser.getInstance()
        .setPlaybackRecordVideo(newMode, duration, object : ISetDeviceParamsCallback {
          override fun onSuccess() {
            type.value = newMode
            isLoading.value = false
            Log.i("Doorbell", "Set Playback Record Video success")
          }

          override fun onFailed(errorCode: Int, errorMsg: String) {
            isLoading.value = false
            Log.i("Doorbell", "Set Playback Record Video failure $errorCode $errorMsg")
          }
        })
    }
  }

  fun formatSDCard() {
    MeariUser.getInstance().startSDCardFormat(object : ISDCardFormatCallback {
      override fun onSuccess() {
        Log.i("Doorbell", "Format SD Card success")
      }

      override fun onFailed(errorCode: Int, errorMsg: String) {
        Log.i("Doorbell", "Format SD Card failed $errorCode $errorMsg")
      }
    })
  }
}
