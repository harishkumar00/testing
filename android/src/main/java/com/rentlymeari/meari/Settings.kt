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
        isFlipEnabled.value = when (mirrorEnable) {
          1 -> true
          else -> false
        }
        isLoading.value = false
        Log.i("Doorbell", "Flip Set Success")
      }

      override fun onFailed(errorCode: Int, errorMsg: String) {
        isLoading.value = false
        Log.i("Doorbell", "Flip Set Error: $errorCode $errorMsg")
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

  // TODO:: Need to confirm
  fun changeChime(
    newMode: String?,
    isWirelessChimeEnabled: MutableState<Int?>,
    isMechanicalChimeEnabled: MutableState<Int?>,
    isLoading: MutableState<Boolean>
  ) {
    // Enable -> 0 - Off, 1 - On
    if (newMode != null) {
      isLoading.value = true
      if (newMode == "wireless") {
        MeariUser.getInstance().setMechanicalChimeEnable(0, object : ISetDeviceParamsCallback {
          override fun onSuccess() {
            MeariUser.getInstance().setWirelessChimeEnable(1, object : ISetDeviceParamsCallback {
              override fun onSuccess() {
                isWirelessChimeEnabled.value = 1
                isMechanicalChimeEnabled.value = 0
                isLoading.value = false
                Log.i("Doorbell", "Wireless chime set Success")
              }

              override fun onFailed(errorCode: Int, errorMsg: String) {
                isLoading.value = false
                Log.i("Doorbell", "Failed to set wireless chime: $errorCode $errorMsg")
              }
            })
          }

          override fun onFailed(errorCode: Int, errorMsg: String) {
            isLoading.value = false
            Log.i("Doorbell", "Failed to set mechanical chime: $errorCode $errorMsg")
          }
        })
      } else {
        MeariUser.getInstance().setWirelessChimeEnable(0, object : ISetDeviceParamsCallback {
          override fun onSuccess() {
            MeariUser.getInstance().setMechanicalChimeEnable(1, object : ISetDeviceParamsCallback {
              override fun onSuccess() {
                isWirelessChimeEnabled.value = 0
                isMechanicalChimeEnabled.value = 1
                isLoading.value = false
                Log.i("Doorbell", "Mechanical chime set Success")
              }

              override fun onFailed(errorCode: Int, errorMsg: String) {
                isLoading.value = false
                Log.i("Doorbell", "Failed to set mechanical chime: $errorCode $errorMsg")
              }
            })
          }

          override fun onFailed(errorCode: Int, errorMsg: String) {
            isLoading.value = false
            Log.i("Doorbell", "Failed to set wireless chime: $errorCode $errorMsg")
          }
        })
      }
    }
  }
}
