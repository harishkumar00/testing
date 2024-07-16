package com.rentlymeari.meari

import android.util.Log
import androidx.compose.runtime.MutableState
import com.meari.sdk.MeariUser
import com.meari.sdk.callback.ISetDeviceParamsCallback

object Settings {

  fun setFlipScreen(
    mirrorEnable: Int,
    isLoading: MutableState<Boolean>,
    isFlipEnabled: MutableState<Boolean>,
  ) {
    // 0 - Normal, 1 - Flip
    isLoading.value = true
    MeariUser.getInstance().setMirror(mirrorEnable, object : ISetDeviceParamsCallback {
      override fun onSuccess() {
        Log.i("harish", "flip success")
        isFlipEnabled.value = when (mirrorEnable) {
          1 -> true
          else -> false
        }
        isLoading.value = false
      }

      override fun onFailed(errorCode: Int, errorMsg: String) {
        isLoading.value = false
        Log.i("harish", "flip fail$errorCode$errorMsg")
      }
    })
  }

  fun setMotionEnable(
    enable: Int,
    sensitivityLevel: Int?,
    isLoading: MutableState<Boolean>,
    isMotionDetectionEnabled: MutableState<Boolean>,
    motionSensitivityLevel: MutableState<Int?>
  ) {
    // Enable: 0-Off, 1-On
    if (sensitivityLevel != null) {
      isLoading.value = true
      MeariUser.getInstance().setMotionDetection(
        enable,
        sensitivityLevel,
        object : ISetDeviceParamsCallback {
          override fun onSuccess() {
            isMotionDetectionEnabled.value = when (enable) {
              1 -> true
              else -> false
            }
            motionSensitivityLevel.value = sensitivityLevel
            isLoading.value = false
            Log.i("Doorbell", "Motion detection set Success")
          }

          override fun onFailed(i: Int, s: String) {
            isLoading.value = false
            Log.i("Doorbell", "Failure in Motion Detection set: $i $s")
          }
        })
    }
  }

  fun setDayNightMode(
    mode: MutableState<Int?>,
    newMode: Int?,
    isLoading: MutableState<Boolean>,
  ) {
    // Mode: 0-Auto, 1-Day, 2-Night
    if (newMode != null) {
      isLoading.value = true
      MeariUser.getInstance()
        .setDayNightMode(newMode, object : ISetDeviceParamsCallback {
          override fun onSuccess() {
            mode.value = newMode
            isLoading.value = false
            Log.i("Doorbell", "Day/Night Mode set success")
          }

          override fun onFailed(i: Int, s: String) {
            isLoading.value = false
            Log.i("Doorbell", "Failure in Day/Night Mode set: $i $s")
          }
        })
    }
  }

  fun changeChime(
    enable: Int
  ) {
    MeariUser.getInstance().setMechanicalChimeEnable(enable, object : ISetDeviceParamsCallback {
      override fun onSuccess() {
      }

      override fun onFailed(errorCode: Int, errorMsg: String) {
      }
    })
  }
}
