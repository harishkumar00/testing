package com.rentlymeari.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.meari.sdk.bean.CameraInfo
import com.meari.sdk.bean.SDCardInfo
import com.rentlymeari.components.Button
import com.rentlymeari.components.Divider
import com.rentlymeari.components.Label
import com.rentlymeari.components.LoadingIndicator
import com.rentlymeari.meari.SDCard
import com.rentlymeari.ui.theme.LocalColor

@Composable
fun AdvancedSettings(
  cameraInfo: CameraInfo?
) {

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(LocalColor.Secondary.White)
      .padding(top = 15.dp)
  ) {
    Column(
      modifier = Modifier
        .background(LocalColor.Monochrome.White)
        .padding(horizontal = 10.dp)
    ) {
      Item(title = "Device ID", value = cameraInfo?.deviceID)

      Item(title = "Device UUID", value = cameraInfo?.deviceUUID)

      Item(title = "Serial Number", value = cameraInfo?.deviceParams?.snNum)

      Item(title = "Firmware Version", value = "v" + cameraInfo?.deviceParams?.firmwareVersion)
    }
  }
}

@Composable
fun StorageSettings() {

  val sdCardInfo = remember { mutableStateOf(SDCardInfo()) }
  val isLoading = remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    SDCard.getSDCardInfo(
      info = sdCardInfo,
      isLoading = isLoading
    )
  }

  if (isLoading.value) {
    LoadingIndicator()
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(LocalColor.Secondary.White)
  ) {
    Column(
      modifier = Modifier
        .padding(horizontal = 10.dp)
        .background(LocalColor.Monochrome.White)
    ) {

      Heading(title = "STORAGE CAPACITY")

      sdCardInfo.value.let { info ->
        info.sdCapacity?.let {
          Item(title = "Total Capacity", value = it, background = LocalColor.Secondary.White)
        }
        Item(
          title = "Used",
          value = info.sdStatus.toString(),
          background = LocalColor.Secondary.White
        )
        info.sdRemainingCapacity?.let {
          Item(title = "Remaining Capacity", value = it, background = LocalColor.Secondary.White)
        }
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(LocalColor.Secondary.White)
          .padding(vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        Button(
          modifier = Modifier
            .width(110.dp)
            .height(50.dp),
          id = "formatSDCard",
          title = "Format",
          textColor = LocalColor.Monochrome.White,
          secondary = true,
          semiBold = true
        ) {
          SDCard.formatSDCard()
        }
      }
    }
  }
}

@Composable
fun Item(
  title: String,
  value: String?,
  background: Color = LocalColor.Monochrome.White
) {
  if (value != null) {
    Column(
      modifier = Modifier
        .background(background)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(15.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Label(
          modifier = Modifier
            .padding(end = 10.dp),
          id = title.lowercase() + "Title",
          title = title,
          l = true,
          black = true,
          medium = true,
        )

        Label(
          modifier = Modifier
            .padding(start = 10.dp),
          id = value.lowercase() + "Value",
          title = value,
          l = true,
          black = true,
          medium = true,
          maxLines = 3
        )
      }

      Divider(
        thickness = 1.dp
      )
    }
  }
}
