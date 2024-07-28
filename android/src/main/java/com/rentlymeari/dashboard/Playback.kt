package com.rentlymeari.dashboard

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.meari.sdk.bean.VideoTimeRecord
import com.ppstrong.ppsplayer.PPSGLSurfaceView
import com.rentlymeari.R
import com.rentlymeari.components.DatePicker
import com.rentlymeari.components.Label
import com.rentlymeari.components.LoadingIndicator
import com.rentlymeari.meari.Meari
import com.rentlymeari.meari.Playback
import com.rentlymeari.meari.ScreenCapture
import com.rentlymeari.ui.theme.LocalColor
import com.rentlymeari.util.DateUtil
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Locale

@Composable
fun Playback(
  isDatePickerVisible: MutableState<Boolean>,
  isMuted: MutableState<Boolean>
) {
  val context = LocalContext.current
  val configuration = LocalConfiguration.current

  val videoSurfaceView = remember {
    PPSGLSurfaceView(
      context,
      configuration.screenWidthDp,
      configuration.screenHeightDp
    ).apply { keepScreenOn = true }
  }

  val selectedDate = remember { mutableLongStateOf(Date().time) }
  val isLoading = remember { mutableStateOf(false) }
  val videos = remember { mutableStateOf(emptyList<VideoTimeRecord>()) }
  val isPlaying = remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    Meari.stopPreview()
  }

  LaunchedEffect(selectedDate.longValue) {
    val (year, month, day) = DateUtil.getDateComponents(selectedDate.longValue)

    Playback.getAllTheClipsOfTheDay(
      year = year,
      month = month,
      day = day,
      isLoading = isLoading,
      videos = videos
    )
  }

  DisposableEffect(Unit) {
    onDispose {
      Playback.stopSDCardPlayback()
    }
  }

  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    if (isLoading.value) {
      LoadingIndicator()
    }

    Column(modifier = Modifier.fillMaxSize()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .fillMaxHeight(0.3f)
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

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .fillMaxHeight(0.85f)
          .background(LocalColor.Monochrome.White)
      ) {
        if (videos.value.isEmpty()) {
          Label(
            modifier = Modifier.padding(horizontal = 30.dp),
            id = "noPlaybackVideos",
            title = "No Playback videos available at the moment.",
            s = true,
            black = true,
            medium = true
          )
        } else {
          LazyColumn(
            modifier = Modifier
              .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            items(videos.value) { video ->
              Row(
                modifier = Modifier
                  .padding(top = 25.dp)
              ) {
                VideoItem(
                  video = video,
                  selectedDate = selectedDate,
                  videoSurfaceView = videoSurfaceView,
                  isPlaying = isPlaying
                )
              }
            }
          }
        }
      }

      BottomBar(
        isPlaying = isPlaying,
        isMuted = isMuted
      )
    }

    DatePicker(isVisible = isDatePickerVisible, selectedDate = selectedDate)
  }
}

@Composable
fun VideoItem(
  video: VideoTimeRecord,
  selectedDate: MutableLongState,
  videoSurfaceView: PPSGLSurfaceView,
  isPlaying: MutableState<Boolean>
) {

  val (year, month, day) = DateUtil.getDateComponents(selectedDate.longValue)

  Row(
    modifier = Modifier
      .fillMaxWidth(0.875f)
      .height(70.dp)
      .shadow(5.dp)
      .background(LocalColor.Monochrome.White)
      .clickable {
        Playback.stopSDCardPlayback()

        if (!isPlaying.value) {
          Playback.resumeSDCardPlayback(isPlaying = isPlaying)
        }

        val playtime = String.format(
          Locale.CHINA,
          "%04d%02d%02d%02d%02d%02d",
          year,
          month,
          day,
          video.StartHour,
          video.StartMinute,
          video.StartSecond
        )

        Playback.startSDCardPlayback(
          videoId = 0,
          ppsGLSurfaceView = videoSurfaceView,
          startTime = playtime,
          isPlaying = isPlaying
        )
      },
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Column(
      modifier = Modifier
        .padding(10.dp)
    ) {
      Label(
        title = video.StartHour.toString() + ":" + video.StartMinute.toString(),
        primary = true,
        xl18 = true,
        center = true,
        semiBold = true
      )
      Label(
        title = "Duration: ${
          DateUtil.calculateDuration(
            startHour = video.StartHour,
            startMinute = video.StartMinute,
            startSecond = video.StartSecond,
            endHour = video.EndHour,
            endMinute = video.EndMinute,
            endSecond = video.EndSecond
          )
        }",
        secondary = true,
        center = true,
        m = true,
      )
    }
  }
}

@Composable
fun BottomBar(
  isPlaying: MutableState<Boolean>,
  isMuted: MutableState<Boolean>
) {

  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  val isRecording = remember {
    mutableStateOf(false)
  }

  Row(
    modifier = Modifier
      .fillMaxSize()
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

    SettingsButton(
      if (isRecording.value) {
        Modifier.background(LocalColor.Danger.Primary)
      } else {
        Modifier
      },
      imageId = R.drawable.ic_record
    )
    {
      if (!isRecording.value) {
        ScreenCapture.startRecording(
          context = context,
          isRecording = isRecording
        )
      } else {
        scope.launch {
          ScreenCapture.stopRecording(
            isRecording = isRecording
          )
        }
      }
    }
    SettingsButton(
      imageId = if (!isPlaying.value) {
        R.drawable.ic_play
      } else {
        R.drawable.ic_pause
      }
    ) {
      if (isPlaying.value) {
        Playback.pauseSDCardPlayback(isPlaying = isPlaying)
      } else {
        scope.launch {
          Playback.resumeSDCardPlayback(isPlaying = isPlaying)
        }
      }
    }
    SettingsButton(imageId = R.drawable.ic_camera) {
      scope.launch {
        ScreenCapture.screenShot(
          context = context
        )
      }
    }
  }
}
