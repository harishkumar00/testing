package com.rentlymeari.dashboard

import android.Manifest
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.meari.sdk.MeariIotController
import com.meari.sdk.bean.CameraInfo
import com.ppstrong.ppsplayer.PPSGLSurfaceView
import com.rentlymeari.R
import com.rentlymeari.components.Button
import com.rentlymeari.components.LoadingIndicator
import com.rentlymeari.components.PermissionAlert
import com.rentlymeari.components.SuccessAlert
import com.rentlymeari.meari.Meari
import com.rentlymeari.meari.ScreenCapture
import com.rentlymeari.ui.theme.LocalColor
import com.rentlymeari.util.PermissionHandler
import kotlinx.coroutines.launch

@Composable
fun Dashboard(
  modifier: Modifier,
  navController: NavController,
  isMuted: MutableState<Boolean>,
  cameraInfo: MutableState<CameraInfo?>
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .then(modifier)
  ) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    val isOnline = remember { mutableStateOf(MeariIotController.isConnected) }
    val isMicOn = remember { mutableStateOf(false) }
    val previewLoader = remember { mutableStateOf(true) }
    val isLoading = remember { mutableStateOf(false) }
    val videoType = remember { mutableIntStateOf(1) }
    val isPermissionDisclaimerAlertVisible = remember { mutableStateOf(false) }
    val isScreenShotSuccess = remember { mutableStateOf(false) }
    val isRecordingSuccess = remember { mutableStateOf(false) }

    val permissions = arrayOf(
      Manifest.permission.RECORD_AUDIO,
    )

    val videoSurfaceView = remember {
      PPSGLSurfaceView(
        context,
        configuration.screenWidthDp,
        configuration.screenHeightDp
      ).apply { keepScreenOn = true }
    }

    PermissionAlert(
      context = context,
      permissions = permissions,
      isPermissionDisclaimerAlertVisible = isPermissionDisclaimerAlertVisible
    )

    SuccessAlert(
      title = "Success",
      description = "A video has been saved to your photo gallery.",
      isVisible = isRecordingSuccess
    )

    SuccessAlert(
      title = "Success",
      description = "A screenshot has been saved to your photo gallery.",
      isVisible = isScreenShotSuccess
    )

    LaunchedEffect(cameraInfo.value) {
      cameraInfo.value?.let {
        Meari.connectAndStartPreview(
          isOnline = isOnline,
          isLoading = previewLoader,
          cameraInfo = it,
          videoSurfaceView = videoSurfaceView,
          videoType = videoType
        )
        it.deviceParams = Meari.getDeviceParams()
        Meari.setMute(false, isMuted)
      }
    }

    Preview(
      previewLoader = previewLoader,
      isOnline = isOnline,
      videoSurfaceView = videoSurfaceView,
      videoType = videoType,
      cameraInfo = cameraInfo,
    )

    DoorbellControls(
      context = context,
      cameraInfo = cameraInfo.value,
      permissions = permissions,
      videoType = videoType,
      videoSurfaceView = videoSurfaceView,
      isMicOn = isMicOn,
      isLoading = isLoading,
      isPermissionDisclaimerAlertVisible = isPermissionDisclaimerAlertVisible,
    )

    BottomNavigationBar(
      navController = navController,
      isMuted = isMuted,
      isRecordingSuccess = isRecordingSuccess,
      isScreenShotSuccess = isScreenShotSuccess
    )
  }
}

@Composable
fun ColumnScope.Preview(
  cameraInfo: MutableState<CameraInfo?>,
  videoType: MutableState<Int>,
  previewLoader: MutableState<Boolean>,
  isOnline: MutableState<Boolean>,
  videoSurfaceView: PPSGLSurfaceView
) {
  val scope = rememberCoroutineScope()

  fun reConnect() {
    scope.launch {
      cameraInfo.value?.let {
        Meari.connectAndStartPreview(
          isOnline = isOnline,
          isLoading = previewLoader,
          cameraInfo = it,
          videoSurfaceView = videoSurfaceView,
          videoType = videoType
        )
        it.deviceParams = Meari.getDeviceParams()
      }
    }
  }

  Row(
    modifier = Modifier
      .fillMaxSize()
      .weight(0.75f)
      .background(LocalColor.Monochrome.Black),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically
  ) {
    when {
      previewLoader.value -> LoadingIndicator()
      !isOnline.value -> OfflineScreen(onReConnect = { reConnect() })
      else -> Box(
        modifier = Modifier
          .fillMaxWidth()
          .fillMaxHeight(0.4f)
      ) {
        AndroidView(
          factory = { context ->
            LinearLayout(context).apply {
              if (videoSurfaceView.parent != null) {
                (videoSurfaceView.parent as ViewGroup).removeView(videoSurfaceView)
              }
              addView(videoSurfaceView)
            }
          }
        )
      }
    }
  }
}

@Composable
fun ColumnScope.DoorbellControls(
  context: Context,
  cameraInfo: CameraInfo?,
  permissions: Array<String>,
  videoSurfaceView: PPSGLSurfaceView,
  videoType: MutableState<Int>,
  isMicOn: MutableState<Boolean>,
  isLoading: MutableState<Boolean>,
  isPermissionDisclaimerAlertVisible: MutableState<Boolean>
) {

  Box(
    modifier = Modifier
      .fillMaxSize()
      .weight(0.15f)
      .background(LocalColor.Monochrome.Black)
  ) {

    Box(
      modifier = Modifier
        .align(Alignment.CenterStart)
        .padding(start = 25.dp)
    ) {

      Button(
        modifier = Modifier
          .width(68.dp)
          .height(33.dp),
        id = "hd/sd",
        title = if (videoType.value == 1) "SD" else "HD",
        s = true,
        semiBold = true,
        grey = true,
        textColor = LocalColor.Monochrome.White,
        cornerRadius = 20.dp,
        onClick = {
          cameraInfo?.let {
            Meari.changeResolution(
              videoId = if (videoType.value == 0) 1 else 0,
              videoType = videoType,
              isLoading = isLoading,
              videoSurfaceView = videoSurfaceView
            )
          }
        }
      )
    }

    val mainButtonModifier = Modifier
      .width(180.dp)
      .height(60.dp)
      .align(Alignment.Center)

    Button(
      modifier = mainButtonModifier,
      id = "tapToSpeak/Mute",
      title = if (isMicOn.value) "Tap to mute" else "Tap to speak",
      grey = !isMicOn.value,
      medium = isMicOn.value,
      semiBold = true,
      textColor = LocalColor.Monochrome.White,
      cornerRadius = 40.dp,
      onClick = {
        if (PermissionHandler.arePermissionsGranted(context, permissions)) {
          cameraInfo?.let {
            if (isMicOn.value) {
              Meari.stopVoiceTalk(
                isMicOn = isMicOn,
                isLoading = isLoading
              )
            } else {
              Meari.startVoiceTalk(
                context = context,
                cameraInfo = it,
                isMicOn = isMicOn,
                isLoading = isLoading
              )
            }
          }
        } else {
          isPermissionDisclaimerAlertVisible.value = true
        }
      }
    )
  }
}

@Composable
fun ColumnScope.BottomNavigationBar(
  navController: NavController,
  isMuted: MutableState<Boolean>,
  isScreenShotSuccess: MutableState<Boolean>,
  isRecordingSuccess: MutableState<Boolean>
) {

  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  val isRecording = remember {
    mutableStateOf(false)
  }

  Row(
    modifier = Modifier
      .fillMaxSize()
      .weight(0.1f)
      .background(LocalColor.Monochrome.Grey),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceEvenly
  ) {
    SettingsButton(
      modifier = if (!isMuted.value) {
        Modifier.background(LocalColor.Primary.Dark)
      } else {
        Modifier
      },
      imageId = if (isMuted.value) {
        R.drawable.ic_sound_off
      } else {
        R.drawable.ic_sound_on
      }
    ) {
      Meari.setMute(!isMuted.value, isMuted)
    }

    SettingsButton(imageId = R.drawable.ic_camera) {
      scope.launch {
        val status = ScreenCapture.screenShot(
          context = context
        )
        if (status) {
          isScreenShotSuccess.value = true
        }
      }
    }

    SettingsButton(
      if (isRecording.value) {
        Modifier.background(LocalColor.Danger.Primary)
      } else {
        Modifier
      },
      imageId = R.drawable.ic_record
    ) {
      if (!isRecording.value) {
        ScreenCapture.startRecording(
          context = context,
          isRecording = isRecording
        )
      } else {
        scope.launch {
          val status = ScreenCapture.stopRecording(
            isRecording = isRecording
          )
          if (status) {
            isRecordingSuccess.value = true
          }
        }
      }
    }

    SettingsButton(imageId = R.drawable.ic_play_back) {
      navController.navigate("Playback")
    }

    SettingsButton(imageId = R.drawable.ic_message) {
      navController.navigate("Messages")
    }
  }
}

@Composable
fun SettingsButton(
  modifier: Modifier = Modifier,
  imageId: Int,
  onClick: () -> Unit
) {
  Image(
    modifier = Modifier
      .padding(vertical = 5.dp)
      .clip(RoundedCornerShape(70.dp))
      .size(50.dp)
      .then(modifier)
      .clickable { onClick() }
      .padding(10.dp),
    painter = painterResource(id = imageId),
    contentDescription = "settingsIcon"
  )
}
