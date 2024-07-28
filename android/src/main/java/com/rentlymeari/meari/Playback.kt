package com.rentlymeari.meari

import android.util.Log
import androidx.compose.runtime.MutableState
import com.meari.sdk.MeariUser
import com.meari.sdk.bean.VideoTimeRecord
import com.meari.sdk.listener.MeariDeviceListener
import com.meari.sdk.listener.MeariDeviceVideoSeekListener
import com.meari.sdk.utils.MeariDeviceUtil
import com.ppstrong.ppsplayer.PPSGLSurfaceView

object Playback {

  fun getAllTheClipsOfTheDay(
    year: Int,
    month: Int,
    day: Int,
    videoId: Int = 0, // TODO::
    isLoading: MutableState<Boolean>,
    videos: MutableState<List<VideoTimeRecord>>
  ) {
    isLoading.value = true
    MeariUser.getInstance().controller.getPlaybackVideoTimesInDay(
      year,
      month,
      day,
      videoId,
      object : MeariDeviceListener {
        override fun onSuccess(successMsg: String) {
          if (successMsg == "-500") {
            videos.value = listOf()
            isLoading.value = false
            Log.i("Doorbell", "There is no videos for the selected date")
          } else {
            val list = MeariDeviceUtil.getSDCardVideoRecords(successMsg)
            videos.value = list
          }
          Log.i("Doorbell", "Final videos: ${videos.value}")
          isLoading.value = false
        }

        override fun onFailed(errorMsg: String) {
          Log.i("Doorbell", "getAllTheClipsOfTheDay success: $errorMsg")
          isLoading.value = false
        }
      })
  }

  fun startSDCardPlayback(
    videoId: Int,
    ppsGLSurfaceView: PPSGLSurfaceView,
    startTime: String,
    isPlaying: MutableState<Boolean>
  ) {
    if (MeariUser.getInstance().controller.isConnected) {
      MeariUser.getInstance().controller.startPlaybackSDCard(
        ppsGLSurfaceView,
        videoId,
        startTime,
        object : MeariDeviceListener {
          override fun onSuccess(successMsg: String) {
            Log.i("Doorbell", "startSDCardPlayback success: $successMsg")
            isPlaying.value = true
          }

          override fun onFailed(errorMsg: String) {
            Log.i("Doorbell", "startSDCardPlayback error: $errorMsg")
            isPlaying.value = false
          }
        }
      ) { p0 -> Log.i("Doorbell", "startSDCardPlayback onVideoClosed: $p0") }
    } else {
      Log.i("Doorbell", "Device is not connected")
    }
  }

  fun seekSDCardPlaybackTime(
    seekTime: String
  ) {
    MeariUser.getInstance().controller.seekPlaybackSDCard(seekTime, object : MeariDeviceListener {
      override fun onSuccess(successMsg: String) {
        Log.i("Doorbell", "seekSDCardPlaybackTime success: $successMsg")
      }

      override fun onFailed(errorMsg: String) {
        Log.i("Doorbell", "seekSDCardPlaybackTime error: $errorMsg")
      }
    }, MeariDeviceVideoSeekListener { })
  }

  fun pauseSDCardPlayback(
    isPlaying: MutableState<Boolean>
  ) {
    MeariUser.getInstance().controller.pausePlaybackSDCard(object : MeariDeviceListener {
      override fun onSuccess(successMsg: String) {
        Log.i("Doorbell", "pauseSDCardPlayback success: $successMsg")
        isPlaying.value = false
      }

      override fun onFailed(errorMsg: String) {
        Log.i("Doorbell", "pauseSDCardPlayback error: $errorMsg")
        isPlaying.value = true
      }
    })
  }

  fun resumeSDCardPlayback(
    isPlaying: MutableState<Boolean>
  ) {
    MeariUser.getInstance().controller.resumePlaybackSDCard(object : MeariDeviceListener {
      override fun onSuccess(successMsg: String) {
        Log.i("Doorbell", "resumeSDCardPlayback success: $successMsg")
        isPlaying.value = true
      }

      override fun onFailed(errorMsg: String) {
        Log.i("Doorbell", "resumeSDCardPlayback error: $errorMsg")
        isPlaying.value = false
      }
    })
  }

  fun stopSDCardPlayback() {
    MeariUser.getInstance().controller.stopPlaybackSDCard(object : MeariDeviceListener {
      override fun onSuccess(successMsg: String) {
        Log.i("Doorbell", "stopSDCardPlayback success: $successMsg")
      }

      override fun onFailed(errorMsg: String) {
        Log.i("Doorbell", "stopSDCardPlayback failed: $errorMsg")
      }
    })
  }
}
