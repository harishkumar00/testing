package com.rentlymeari

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.meari.sdk.BuildConfig
import com.meari.sdk.MeariSdk
import com.meari.sdk.MeariUser
import com.meari.sdk.bean.CameraInfo
import com.ppstrong.ppsplayer.meariLog
import com.rentlymeari.meari.Meari
import com.rentlymeari.meari.MeariMQTTCallback
import com.rentlymeari.ui.theme.MeariTheme
import kotlinx.coroutines.delay

class MeariActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    MeariSdk.init(this, MeariMQTTCallback())
    if (BuildConfig.DEBUG) {
      meariLog.createlibrarylog()
      meariLog.getInstance().setlevel(0)
      MeariSdk.getInstance().isDebug = true
      Log.i("harish", "here")
    }

    // Connect Meari MQTT service
    MeariUser.getInstance().connectMqttServer(application)

    val deviceId = intent.getStringExtra("deviceId")

    setContent {

      val cameraInfo = remember {
        mutableStateOf<CameraInfo?>(null)
      }

      LaunchedEffect(Unit) {
        val doorbells = Meari.loginAndFetchDoorbells()
        delay(10000)
        cameraInfo.value = doorbells?.doorBells?.find {
          it.deviceID == deviceId
        }
      }

      MeariTheme {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(Color.Red)
        ){

        }
      }
    }
  }
}
