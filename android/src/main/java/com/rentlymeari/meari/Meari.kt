package com.rentlymeari.meari

import android.Manifest
import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import com.meari.sdk.MeariDeviceController
import com.meari.sdk.MeariUser
import com.meari.sdk.bean.CameraInfo
import com.meari.sdk.bean.DeviceParams
import com.meari.sdk.bean.MeariDevice
import com.meari.sdk.bean.UserInfo
import com.meari.sdk.callback.IDevListCallback
import com.meari.sdk.callback.IGetDeviceParamsCallback
import com.meari.sdk.callback.IGetTokenCallback
import com.meari.sdk.callback.ILoginCallback
import com.meari.sdk.callback.ILogoutCallback
import com.meari.sdk.callback.IResultCallback
import com.meari.sdk.callback.ISetDeviceParamsCallback
import com.meari.sdk.listener.MeariDeviceListener
import com.meari.sdk.listener.MeariDeviceTalkVolumeListener
import com.meari.sdk.mqtt.MqttMangerUtils
import com.ppstrong.ppsplayer.PPSGLSurfaceView
import com.rentlymeari.meari.CommonUtils.getTalkType
import com.rentlymeari.util.PermissionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

object Meari {

  val scope = CoroutineScope(Dispatchers.IO)

  suspend fun login(
    account: String,
    password: String,
    countryCode: String,
    phoneCode: String
  ): Boolean? = withTimeoutOrNull(8000) {
    suspendCancellableCoroutine { continuation ->
      if (MeariUser.getInstance().isLogin) {
        Log.i("Doorbell", "Already Logged in")
        MeariUser.getInstance().controller = MeariDeviceController()
        continuation.resume(true)
        return@suspendCancellableCoroutine
      }
      MeariUser.getInstance()
        .loginWithAccount(
          countryCode,
          phoneCode,
          account,
          password,
          object : ILoginCallback {
            override fun onError(p0: Int, p1: String?) {
              Log.i("Doorbell", "Login Failed, Error: $p0 $p1")
              continuation.resume(false)
            }

            override fun onSuccess(userInfo: UserInfo) {
              Log.i("Doorbell", "Login Successful, User Info: $userInfo")
              MeariUser.getInstance().controller = MeariDeviceController()
              continuation.resume(true)
            }
          })
    }
  }

  private suspend fun getDeviceList(): MeariDevice? = withTimeoutOrNull(8000) {
    suspendCancellableCoroutine { continuation ->
      if (MeariUser.getInstance().isLogin) {
        Log.i("Doorbell", "Fetching devices...")
        MeariUser.getInstance().getDeviceList(object : IDevListCallback {
          override fun onError(p0: Int, p1: String?) {
            Log.i("Doorbell", "Device list fetching failed, Error: $p0 $p1")
            continuation.resume(null)
          }

          override fun onSuccess(p0: MeariDevice?) {
            Log.i("Doorbell", "Device list fetching success, $p0")
            continuation.resume(p0)
          }
        })
      } else {
        Log.i("Doorbell", "Please login")
      }
    }
  }

  suspend fun getDeviceParams(): DeviceParams? =
    suspendCancellableCoroutine { continuation ->
      MeariUser.getInstance().getDeviceParams(object : IGetDeviceParamsCallback {
        override fun onSuccess(params: DeviceParams) {
          continuation.resume(params)
          Log.i("Doorbell", "Get device params success")
        }

        override fun onFailed(i: Int, s: String) {
          Log.i("Doorbell", "Error in Get Params $i $s")
          continuation.resume(null)
        }
      })
    }

  suspend fun loginAndFetchDoorbells(
    account: String = "elango@mailinator.com",
    password: String = "12345678A",
    countryCode: String = "CN",
    phoneCode: String = "86"
  ): MeariDevice? = withTimeoutOrNull(10000) {
    val isLoggedIn = login(
      account = account,
      password = password,
      countryCode = countryCode,
      phoneCode = phoneCode
    )
    var meariDevice: MeariDevice? = null
    if (isLoggedIn == true) {
      meariDevice = getDeviceList()
    }

    meariDevice
  }

  private suspend fun preview(
    cameraInfo: CameraInfo,
    videoType: MutableState<Int>,
    videoSurfaceView: PPSGLSurfaceView,
  ): Boolean? = withTimeoutOrNull(8000) {
    suspendCancellableCoroutine { continuation ->

      // If using Meari doorbells that do not support the HD option, then use this.
      val defaultStreamId: String = CommonUtils.getDefaultStreamId(cameraInfo)

      MeariUser.getInstance().controller.startPreview(
        videoSurfaceView,
        videoType.value,
        object : MeariDeviceListener {
          override fun onSuccess(successMsg: String) {
            Log.i("Doorbell", "Preview Success: $successMsg")
            continuation.resume(true)
          }

          override fun onFailed(errorMsg: String) {
            Log.i("Doorbell", "Preview Failed $errorMsg")
            continuation.resume(false)
          }
        }) {
      }
    }
  }

  suspend fun connectAndStartPreview(
    isLoading: MutableState<Boolean>,
    isOnline: MutableState<Boolean>,
    cameraInfo: CameraInfo,
    videoSurfaceView: PPSGLSurfaceView,
    videoType: MutableState<Int>
  ) {
    // Resolution: 1-SD 0-HD
    isLoading.value = true
    MeariUser.getInstance().controller.cameraInfo = cameraInfo
    MeariUser.getInstance().cameraInfo = cameraInfo

    MeariUser.getInstance().controller.startConnect(object : MeariDeviceListener {
      override fun onSuccess(successMsg: String) {
        val mBitRate = MeariUser.getInstance().controller.bitRate.toString() + "KB/s"
        Log.i("Doorbell", "Connection Successful: $successMsg")
        isOnline.value = true
        scope.launch {
          preview(
            cameraInfo,
            videoType,
            videoSurfaceView
          )
          launch(Dispatchers.Main) {
            isLoading.value = false
          }
        }
      }

      override fun onFailed(errorMsg: String) {
        Log.i("Doorbell", "Connection Failed $errorMsg")
        isLoading.value = false
        isOnline.value = false
      }
    })
  }

  suspend fun disconnect(
    isLoading: MutableState<Boolean>
  ): Boolean? = withTimeoutOrNull(8000) {
    suspendCancellableCoroutine { continuation ->
      // Disconnect, you must disconnect when exiting preview and playback.
      isLoading.value = true
      MeariUser.getInstance().controller.stopConnect(object : MeariDeviceListener {
        override fun onSuccess(successMsg: String) {
          Log.i("Doorbell", "Disconnect Success: $successMsg")
          continuation.resume(true)
          isLoading.value = false
        }

        override fun onFailed(errorMsg: String) {
          Log.i("Doorbell", "Disconnect Failed: $errorMsg")
          continuation.resume(false)
          isLoading.value = false
        }
      })
    }
  }

  fun setMute(
    mute: Boolean,
    isMuted: MutableState<Boolean>
  ) {
    try {
      MeariUser.getInstance().controller.setMute(mute)
      isMuted.value = mute
      Log.i("Doorbell", "Set to mute: $mute")
    } catch (e: Exception) {
      Log.i("Doorbell", "Error in Set Mute $e")
    }
  }

  fun startVoiceTalk(
    context: Context,
    cameraInfo: CameraInfo,
    isMicOn: MutableState<Boolean>,
    isLoading: MutableState<Boolean>
  ) {
    val talkType: Int = getTalkType(cameraInfo)

    val permissions = arrayOf(
      Manifest.permission.RECORD_AUDIO,
    )

    if (PermissionHandler.arePermissionsGranted(context, permissions)) {
      isLoading.value = true
      MeariUser.getInstance().controller.startVoiceTalk(
        talkType,
        object : MeariDeviceListener {
          override fun onSuccess(p0: String?) {
            Log.i("Doorbell", "Start talking Successful: $p0")
            isMicOn.value = true
            isLoading.value = false
          }

          override fun onFailed(p0: String?) {
            Log.i("Doorbell", "Start talking Successful: $p0")
            isMicOn.value = false
            isLoading.value = false
          }

        },
        object : MeariDeviceTalkVolumeListener {
          override fun onTalkVolume(p0: Int) {
            isLoading.value = false
            Log.i("Doorbell", "On Talk Volume: $p0")
          }

          override fun onFailed(p0: String?) {
            isLoading.value = false
            Log.i("Doorbell", "No Microphone found: $p0")
          }
        })
    } else {
      PermissionHandler.checkAndRequestPermissions(
        context as Activity,
        permissions,
        200
      )
    }
  }

  fun stopVoiceTalk(
    isMicOn: MutableState<Boolean>,
    isLoading: MutableState<Boolean>
  ) {
    isLoading.value = true
    MeariUser.getInstance().controller.stopVoiceTalk(object : MeariDeviceListener {
      override fun onSuccess(p0: String?) {
        isMicOn.value = false
        isLoading.value = false
        Log.i("Doorbell", "Stop Voice Success: $p0")
      }

      override fun onFailed(p0: String?) {
        isMicOn.value = true
        isLoading.value = false
        Log.i("Doorbell", "Stop Voice Failure: $p0")
      }
    })
  }

  // To control Doorbell volume
  fun volumeControl(
    volume: Int
  ) {
    MeariUser.getInstance().setSpeakVolume(volume, object : ISetDeviceParamsCallback {
      override fun onSuccess() {
        Log.i("Doorbell", "Volume set to: $volume")
      }

      override fun onFailed(errorCode: Int, errorMsg: String) {
        Log.i("Doorbell", "Volume set failure: $errorCode $errorMsg")
      }
    })
  }

  fun logout() {
    MeariUser.getInstance().logout(object : ILogoutCallback {
      override fun onSuccess(p0: Int) {
        // Clear user information, disconnect mqtt connection.
        MeariUser.getInstance().removeUserInfo()
        MqttMangerUtils.getInstance().disConnectService()
        Log.i("Doorbell", "Doorbell logout Successful: $p0")
      }

      override fun onError(code: Int, error: String) {
        Log.i("Doorbell", "Failed in Doorbell logout: $code $error")
      }
    })
  }

  fun changeResolution(
    videoId: Int,
    videoType: MutableState<Int>,
    isLoading: MutableState<Boolean>,
    videoSurfaceView: PPSGLSurfaceView,
  ) {
    isLoading.value = true
    MeariUser.getInstance().controller.changeVideoResolution(
      videoSurfaceView,
      videoId,
      object : MeariDeviceListener {
        override fun onSuccess(successMsg: String) {
          videoType.value = videoId
          isLoading.value = false
          Log.i("Doorbell", "Change Resolution Success: $successMsg")
        }

        override fun onFailed(errorMsg: String) {
          isLoading.value = false
          Log.i("Doorbell", "Change Resolution Failed: $errorMsg")
        }
      }) { }
  }

  fun stopPreview() {
    MeariUser.getInstance().controller.stopPreview(object : MeariDeviceListener {
      override fun onSuccess(successMsg: String) {
        Log.i("Doorbell", "Stop Preview Failure: $successMsg")
      }

      override fun onFailed(errorMsg: String) {
        Log.i("Doorbell", "Stop Preview Failure: $errorMsg")
      }
    })
  }

  suspend fun removeDoorbell(
    cameraInfo: CameraInfo?,
    isLoading: MutableState<Boolean>
  ): Boolean? =
    suspendCancellableCoroutine { continuation ->
      cameraInfo?.let {
        isLoading.value = true
        MeariUser.getInstance()
          .deleteDevice(
            cameraInfo.deviceID,
            cameraInfo.devTypeID,
            object : IResultCallback {
              override fun onError(p0: Int, p1: String?) {
                isLoading.value = false
                continuation.resume(false)
                Log.i("Doorbell", "Failed in Remove Doorbell: $p0 $p1")
              }

              override fun onSuccess() {
                isLoading.value = false
                continuation.resume(true)
                Log.i("Doorbell", "Doorbell Remove Success")
              }
            })
      }
    }

  suspend fun getToken(): String? = withTimeoutOrNull(10000) {
    suspendCancellableCoroutine { continuation ->
      MeariUser.getInstance().getToken(object : IGetTokenCallback {
        override fun onSuccess(s: String, i: Int, i1: Int) {
          Log.i("Doorbell", "Get token success: $s, $i, $i1")
          continuation.resume(s)
        }

        override fun onError(i: Int, s: String) {
          Log.i("Doorbell", "Get token failure: $s, $i")
          continuation.resume(null)
        }
      })
    }
  }

  suspend fun setupPushNotification(
    type: Int = 1,
    token: String
  ): Boolean? = withTimeoutOrNull(80000)  {
    suspendCancellableCoroutine { continuation ->
      MeariUser.getInstance().postPushToken(
        type,
        token,
        object : IResultCallback {
          override fun onError(p0: Int, p1: String?) {
            continuation.resume(false)
            Log.i("Doorbell", "Failed in settings Push Notification Token: $p0 $p1")
          }

          override fun onSuccess() {
            continuation.resume(true)
            Log.i("Doorbell", "Push Notification Token setup success")
          }
        })
    }
  }
}
