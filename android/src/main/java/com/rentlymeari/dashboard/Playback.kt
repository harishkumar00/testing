package com.rentlymeari.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rentlymeari.R
import com.rentlymeari.components.DatePicker
import com.rentlymeari.components.Label
import com.rentlymeari.ui.theme.LocalColor
import java.util.Date

@Composable
fun Playback(
  isDatePickerVisible: MutableState<Boolean>,
) {
  Column(
    modifier = Modifier
      .fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {

    val selectedDate = remember {
      mutableLongStateOf(Date().time)
    }

    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Red)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .fillMaxHeight(0.3f)
          .background(Color.Blue)
      ) {

      }

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .fillMaxHeight(0.85f)
          .background(LocalColor.Monochrome.White)
      ) {
        Label(
          modifier = Modifier
            .padding(horizontal = 30.dp),
          id = "noPlaybackVideos",
          title = "No Playback videos available at the moment.",
          s = true,
          black = true,
          medium = true,
        )
      }

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .fillMaxHeight()
          .background(Color.Cyan)
      ) {
        BottomBar()
      }
    }

    DatePicker(isVisible = isDatePickerVisible, selectedDate = selectedDate)
  }
}

@Composable
fun BottomBar() {

  Row(
    modifier = Modifier
      .fillMaxSize()
      .background(LocalColor.Monochrome.Grey),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceEvenly
  ) {

    SettingsButton(
      imageId = R.drawable.ic_sound_off
    ) {

    }

    SettingsButton(
      imageId = R.drawable.ic_record
    ) {

    }

    SettingsButton(
      imageId = R.drawable.ic_record
    ) {

    }

    SettingsButton(
      imageId = R.drawable.ic_camera
    ) {

    }
  }
}
