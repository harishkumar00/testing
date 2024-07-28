package com.rentlymeari.components

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meari.sdk.MeariUser
import com.rentlymeari.meari.Meari
import kotlinx.coroutines.launch

@Composable
fun ResetWifiAlert(
  isResetWifiAlertVisible: MutableState<Boolean>,
  isLoading: MutableState<Boolean>
) {

  val scope = rememberCoroutineScope()
  val activity = LocalContext.current as Activity

  Alert(
    modifier = Modifier
      .padding(top = 20.dp, start = 20.dp, end = 15.dp),
    isVisible = isResetWifiAlertVisible,
  ) {

    Label(
      modifier = Modifier
        .padding(bottom = 5.dp),
      title = "Reset WiFi",
      bold = true,
      xl20 = true,
      black = true
    )

    Label(
      modifier = Modifier
        .padding(vertical = 5.dp),
      title = "Please go to Manage tab > Add Doorbell then follow the reset instruction video to re-add your doorbell.",
      medium = true,
      l = true,
      black = true,
      maxLines = 6,
      lineHeight = 18.sp
    )

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 5.dp),
      horizontalArrangement = Arrangement.End
    ) {
      AnchorButton(
        modifier = Modifier,
        id = "cancel",
        title = "CANCEL",
        semiBold = true,
        black = true,
        m = true,
        onClick = {
          isResetWifiAlertVisible.value = false
        }
      )

      AnchorButton(
        modifier = Modifier,
        id = "resetWifi",
        title = "RESET WIFI",
        semiBold = true,
        black = true,
        m = true,
        onClick = {
          scope.launch {
            val success = Meari.disconnect(isLoading = isLoading) == true
            if (!MeariUser.getInstance().controller.isConnected || success) {
              activity.finish()
            }
          }
        }
      )
    }
  }
}
