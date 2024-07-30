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
import com.meari.sdk.bean.CameraInfo
import com.rentlymeari.meari.Meari
import kotlinx.coroutines.launch

@Composable
fun RemoveDoorbellAlert(
  isRemoveDoorbellAlertVisible: MutableState<Boolean>,
  isLoading: MutableState<Boolean>,
  cameraInfo: CameraInfo,
) {

  val scope = rememberCoroutineScope()
  val activity = LocalContext.current as Activity

  Alert(
    modifier = Modifier
      .padding(15.dp),
    isVisible = isRemoveDoorbellAlertVisible,
  ) {

    Label(
      modifier = Modifier
        .padding(bottom = 5.dp),
      title = "Remove Doorbell",
      bold = true,
      xl20 = true,
      black = true
    )

    Label(
      modifier = Modifier
        .padding(vertical = 5.dp),
      title = "After doorbell is disconnected, all the doorbell related settings and data will be deleted.",
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
        id = "no",
        title = "NO",
        semiBold = true,
        black = true,
        m = true,
        onClick = {
          isRemoveDoorbellAlertVisible.value = false
        }
      )

      AnchorButton(
        modifier = Modifier,
        id = "remove",
        title = "REMOVE",
        semiBold = true,
        black = true,
        m = true,
        onClick = {
          scope.launch {
            val success = Meari.removeDoorbell(
              cameraInfo = cameraInfo
            )
            if (!MeariUser.getInstance().controller.isConnected || success == true) {
              activity.finish()
            }
          }
        }
      )
    }
  }
}

