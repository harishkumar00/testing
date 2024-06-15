package com.rentlymeari.dashboard

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
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
import com.meari.sdk.MeariDeviceController
import com.meari.sdk.MeariIotController
import com.meari.sdk.bean.CameraInfo
import com.ppstrong.ppsplayer.PPSGLSurfaceView
import com.rentlymeari.R
import com.rentlymeari.components.Button
import com.rentlymeari.components.LoadingIndicator
import com.rentlymeari.meari.Meari
import com.rentlymeari.ui.theme.LocalColor
import kotlinx.coroutines.launch
import java.io.OutputStream

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

    val isOnline = remember {
      mutableStateOf(MeariIotController.isConnected)
    }

    val isMicOn = remember {
      mutableStateOf(false)
    }

    val previewLoader = remember {
      mutableStateOf(true)
    }

    val isLoading = remember {
      mutableStateOf(false)
    }

    val videoType = remember {
      mutableIntStateOf(1)
    }

    val videoSurfaceView = remember {
      PPSGLSurfaceView(
        context,
        configuration.screenWidthDp,
        configuration.screenHeightDp
      ).apply {
        keepScreenOn = true
      }
    }

    LaunchedEffect(cameraInfo.value) {
      if (cameraInfo.value != null) {
        Meari.connectAndStartPreview(
          isOnline = isOnline,
          isLoading = previewLoader,
          controller = MeariDeviceController(),
          cameraInfo = cameraInfo.value!!,
          videoSurfaceView = videoSurfaceView,
          videoType = videoType
        )
        cameraInfo.value!!.deviceParams = Meari.getDeviceParams()
      }
    }

    Preview(
      previewLoader = previewLoader,
      isLoading = isLoading,
      isOnline = isOnline,
      videoSurfaceView = videoSurfaceView,
      videoType = videoType,
      cameraInfo = cameraInfo,
    )

    DoorbellControls(
      context = context,
      cameraInfo = cameraInfo.value,
      videoType = videoType,
      videoSurfaceView = videoSurfaceView,
      isMicOn = isMicOn,
      isLoading = isLoading
    )

    BottomNavigationBar(
      context = context,
      cameraInfo = cameraInfo.value,
      navController = navController,
      isMuted = isMuted,
      screenShotView = videoSurfaceView
    )
  }
}

@Composable
fun ColumnScope.Preview(
  cameraInfo: MutableState<CameraInfo?>,
  videoType: MutableState<Int>,
  previewLoader: MutableState<Boolean>,
  isLoading: MutableState<Boolean>,
  isOnline: MutableState<Boolean>,
  videoSurfaceView: PPSGLSurfaceView
) {
  val scope = rememberCoroutineScope()

  fun reConnect() {
    scope.launch {
      if (cameraInfo.value != null) {
        Meari.connectAndStartPreview(
          isOnline = isOnline,
          isLoading = previewLoader,
          controller = MeariDeviceController(),
          cameraInfo = cameraInfo.value!!,
          videoSurfaceView = videoSurfaceView,
          videoType = videoType
        )
        cameraInfo.value!!.deviceParams = Meari.getDeviceParams()
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
    if (previewLoader.value) {
      LoadingIndicator()
    } else if (!isOnline.value) {
      OfflineScreen(
        onReConnect = { reConnect() }
      )
    } else {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .fillMaxHeight(0.4f)
      ) {
        AndroidView(
          factory = { context ->
            val linearLayout = LinearLayout(context)

            if (videoSurfaceView.parent != null)
              (videoSurfaceView.parent as ViewGroup).removeView(videoSurfaceView)

            linearLayout.addView(
              videoSurfaceView
            )
            linearLayout
          },
        )
      }
    }
  }
}

@Composable
fun ColumnScope.DoorbellControls(
  context: Context,
  cameraInfo: CameraInfo?,
  videoSurfaceView: PPSGLSurfaceView,
  videoType: MutableState<Int>,
  isMicOn: MutableState<Boolean>,
  isLoading: MutableState<Boolean>
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
          .height(35.dp),
        id = "hd/sd",
        title = if (videoType.value == 1) "SD" else "HD",
        s = true,
        semiBold = true,
        grey = true,
        textColor = LocalColor.Monochrome.White,
        cornerRadius = 20.dp,
        onClick = {
          if (cameraInfo != null) {
            Meari.changeResolution(
              videoId = if (videoType.value == 0) {
                1
              } else {
                0
              },
              videoType = videoType,
              isLoading = isLoading,
              videoSurfaceView = videoSurfaceView
            )
          }
        }
      )
    }

    Button(
      modifier = Modifier
        .width(180.dp)
        .height(60.dp)
        .align(Alignment.Center),
      id = "tapToSpeak/Mute",
      title = if (isMicOn.value) {
        "Tap to mute"
      } else {
        "Tap to speak"
      },
      grey = !isMicOn.value,
      medium = isMicOn.value,
      semiBold = true,
      textColor = LocalColor.Monochrome.White,
      cornerRadius = 40.dp,
      onClick = {
        if (cameraInfo != null) {
          if (isMicOn.value) {
            Meari.stopVoiceTalk(
              isMicOn = isMicOn,
              isLoading = isLoading
            )
          } else {
            Meari.startVoiceTalk(
              context = context,
              cameraInfo = cameraInfo,
              isMicOn = isMicOn,
              isLoading = isLoading
            )
          }
        }
      }
    )
  }
}

@Composable
fun ColumnScope.BottomNavigationBar(
  context: Context,
  cameraInfo: CameraInfo?,
  screenShotView: View,
  navController: NavController,
  isMuted: MutableState<Boolean>
) {
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
      if (isMuted.value) {
        Meari.setMute(
          false,
          isMuted
        )
      } else {
        Meari.setMute(
          true,
          isMuted
        )
      }
    }

    SettingsButton(
      imageId = R.drawable.ic_camera
    ) {
      val bitmap = takeScreenshot(view = screenShotView)

      if (bitmap != null) {
        saveMediaToStorage(bitmap = bitmap, context = context)
      }

    }

    SettingsButton(
      imageId = R.drawable.ic_record
    ) {

    }

    SettingsButton(
      imageId = R.drawable.ic_play_back
    ) {
      navController.navigate("Playback")
    }

    SettingsButton(
      imageId = R.drawable.ic_message
    ) {
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
      .clickable {
        onClick()
      }
      .padding(10.dp),
    painter = painterResource(id = imageId),
    contentDescription = "settingsIcon",
  )
}

private fun takeScreenshot(view: View): Bitmap? {
  var screenshot: Bitmap? = null
  try {
    screenshot =
      Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)


    val canvas = Canvas(screenshot)
    view.draw(canvas)
  } catch (e: Exception) {
    Log.e("Error", "Failed to capture screenshot because:" + e.message)
  }
  return screenshot
}

private fun saveMediaToStorage(bitmap: Bitmap, context: Context) {

  val filename = "${System.currentTimeMillis()}.jpg"

  var fos: OutputStream? = null

  context.contentResolver?.also { resolver ->

    val contentValues = ContentValues().apply {

      // Putting file information in content values
      put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
      put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
      put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
    }

    val imageUri: Uri? =
      resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    fos = imageUri?.let { resolver.openOutputStream(it) }
  }

  fos?.use {
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
    Toast.makeText(context, "Captured View and saved to Gallery", Toast.LENGTH_SHORT).show()
  }
}
