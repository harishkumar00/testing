package com.rentlymeari.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rentlymeari.components.AnchorButton
import com.rentlymeari.components.Button
import com.rentlymeari.components.Label
import com.rentlymeari.ui.theme.LocalColor

@Composable
fun OfflineScreen(
  onReConnect: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 30.dp),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.Bottom
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Label(
        modifier = Modifier
          .padding(bottom = 15.dp),
        title = "Connection failed!",
        white = true,
        xl18 = true,
        center = true
      )

      Label(
        modifier = Modifier
          .padding(bottom = 10.dp),
        title = "If you're experiencing an issue, please check the following:",
        white = true,
        maxLines = 2
      )

      Label(
        modifier = Modifier
          .padding(start = 10.dp, bottom = 10.dp),
        title = "Move your Wi-Fi access point closer to the doorbell.",
        white = true,
        maxLines = 2
      )

      Label(
        modifier = Modifier
          .padding(start = 10.dp, bottom = 25.dp),
        title = "Check power source to doorbell, by pressing the button on the front of the doorbell. If the power is connected properly, you will hear a chime. If you do not hear a chime, recharge the doorbell.",
        white = true,
        maxLines = 6
      )

      Label(
        title = "First time using the doorbell?",
        white = true
      )

      AnchorButton(
        modifier = Modifier
          .padding(bottom = 20.dp),
        id = "connectDoorbellToWiFi",
        title = "Connect Doorbell to Wi-Fi",
        primary = true,
        underLine = true
      )

      Button(
        modifier = Modifier
          .width(180.dp)
          .height(70.dp)
          .padding(bottom = 20.dp),
        id = "tapToRetry",
        title = "Tap to retry",
        xl20 = true,
        black = true,
        textColor = LocalColor.Primary.Light,
        borderColor = LocalColor.Primary.Light
      ) {
        onReConnect()
      }

      Label(
        modifier = Modifier
          .padding(bottom = 5.dp),
        title = "Contact Support for further assistance.",
        white = true
      )
    }
  }
}
