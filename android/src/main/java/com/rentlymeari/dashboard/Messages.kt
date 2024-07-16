package com.rentlymeari.dashboard

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.meari.sdk.bean.CameraInfo
import com.rentlymeari.components.Divider
import com.rentlymeari.components.Label
import com.rentlymeari.components.LoadingIndicator
import com.rentlymeari.meari.Messages
import com.rentlymeari.ui.theme.LocalColor

@Composable
fun Messages(
  cameraInfo: CameraInfo?
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(LocalColor.Secondary.White),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    val isLoading = remember {
      mutableStateOf(false)
    }

    Log.i("Harish mesage", cameraInfo?.evt.toString())

    LaunchedEffect(Unit) {
      if (cameraInfo != null) {
        Log.i("Harish messa", "above device id check")
        cameraInfo.deviceID?.toLong()?.let {
          Log.i("Harish messa", "called")
          Messages.getMessages(
            deviceId = it,
            day = "20240713",
            index = "0",
            cameraInfo = cameraInfo
          )
        }

        Messages.systemMessages()
      } else {
        Log.i("Harish messa", "in else")
      }
    }

    if (isLoading.value) {
      LoadingIndicator()
    } else {
      LazyColumn {
        item {
          MessageItem(date = "2024-7-15 22:07", text = "Motion has been detected by your Doorbell.")
        }

        item {
          MessageItem(date = "2024-7-15 22:07", text = "Someone rang the Doorbell.")
        }
      }
      Label(title = "No messages available at the moment.")
    }
  }
}

@Composable
fun MessageItem(
  date: String,
  text: String,
) {
  Column(
    modifier = Modifier
      .background(LocalColor.Secondary.White)
      .padding(horizontal = 10.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {

    Column (
      modifier = Modifier
        .fillMaxWidth(0.85f),
      horizontalAlignment = Alignment.Start
    ) {
      Label(
        modifier = Modifier
          .padding(top = 15.dp),
        id = date,
        title = date,
        xs = true,
        black = true,
        medium = true,
      )
      Label(
        modifier = Modifier
          .padding(bottom = 15.dp),
        id = date,
        title = text,
        m = true,
        primary = true,
        semiBold = true,
      )

      Row (
        modifier = Modifier
          .height(170.dp)
          .fillMaxWidth()
          .padding(bottom = 15.dp)
          .background(Color.Red),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {

      }
    }

    Divider(
      thickness = 1.dp
    )
  }
}
