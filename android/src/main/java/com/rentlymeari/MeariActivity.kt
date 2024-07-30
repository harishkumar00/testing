package com.rentlymeari

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.meari.sdk.BuildConfig
import com.meari.sdk.MeariSdk
import com.meari.sdk.MeariUser
import com.meari.sdk.bean.CameraInfo
import com.ppstrong.ppsplayer.meariLog
import com.rentlymeari.dashboard.NavController
import com.rentlymeari.meari.Meari
import com.rentlymeari.meari.MeariMQTTCallback
import com.rentlymeari.ui.theme.MeariTheme

class MeariActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    appContext = this.applicationContext

    initializeMeariSdk()
    connectMeariMqtt() // Connect Meari MQTT service

    val deviceId = intent.getStringExtra("deviceId")

    setContent {
      val cameraInfo = remember { mutableStateOf<CameraInfo?>(null) }

      LaunchedEffect(Unit) {
        fetchCameraInfo(deviceId = deviceId, cameraInfo = cameraInfo)
      }

      MeariTheme {
        NavController(cameraInfo = cameraInfo)
      }
    }
  }

  private fun initializeMeariSdk() {
    MeariSdk.init(this, MeariMQTTCallback())
    if (BuildConfig.DEBUG) {
      meariLog.createlibrarylog()
      meariLog.getInstance().setlevel(0)
      MeariSdk.getInstance().isDebug = true
    }
  }

  private fun connectMeariMqtt() {
    try {
      MeariUser.getInstance().connectMqttServer(application)
    } catch (e: Exception) {
      Log.e("MeariActivity", "Error connecting to MQTT server: ${e.message}")
    }
  }

  private suspend fun fetchCameraInfo(deviceId: String?, cameraInfo: MutableState<CameraInfo?>) {
    if (deviceId == null) {
      Log.i("Doorbell", "MeariActivity: Device ID is null")
      return
    }

    try {
      val doorbells = Meari.loginAndFetchDoorbells()
      cameraInfo.value = doorbells?.doorBells?.find { it.deviceID == deviceId }
    } catch (e: Exception) {
      Log.e("Doorbell", "MeariActivity: Error fetching camera info: ${e.message}")
    }
  }

  companion object {
    private var appContext: Context? = null

    fun getAppContext(): Context? {
      return appContext
    }
  }
}
