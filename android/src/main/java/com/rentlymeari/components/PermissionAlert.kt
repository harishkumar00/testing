package com.rentlymeari.components

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rentlymeari.ui.theme.LocalColor
import com.rentlymeari.util.PermissionHandler

@Composable
fun PermissionAlert(
  context: Context,
  permissions: Array<String>,
  isPermissionDisclaimerAlertVisible: MutableState<Boolean>
) {
  Alert(
    modifier = Modifier
      .fillMaxWidth(0.95f)
      .padding(top = 40.dp, bottom = 30.dp, start = 30.dp, end = 30.dp),
    isVisible = isPermissionDisclaimerAlertVisible,
  ) {
    Column(
      modifier = Modifier
    ) {
      Label(
        modifier = Modifier
          .padding(bottom = 20.dp),
        title = "This app uses your audio through your microphone to communicate with the visitor ringing the doorbell. Your audio data is not transferred to third parties.",
        medium = true,
        xl18 = true,
        black = true,
        maxLines = 6,
        lineHeight = 20.sp,
        center = true
      )

      Row(
        modifier = Modifier
          .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Button(
          modifier = Modifier
            .width(125.dp)
            .height(50.dp),
          id = "notNow",
          title = "NOT NOW",
          textColor = LocalColor.Monochrome.White,
          primary = true,
          semiBold = true
        ) {
          isPermissionDisclaimerAlertVisible.value = false
        }

        Button(
          modifier = Modifier
            .width(125.dp)
            .height(50.dp),
          id = "agree",
          title = "AGREE",
          textColor = LocalColor.Monochrome.White,
          primary = true,
          semiBold = true
        ) {
          isPermissionDisclaimerAlertVisible.value = false
          PermissionHandler.checkAndRequestPermissions(
            context as Activity,
            permissions,
            200
          )
        }
      }
    }
  }
}
