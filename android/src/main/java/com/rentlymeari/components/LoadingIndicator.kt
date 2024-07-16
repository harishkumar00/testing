package com.rentlymeari.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.rentlymeari.ui.theme.LocalColor

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoadingIndicator(
  modifier: Modifier = Modifier,
  color: Color = LocalColor.Primary.Dark,
  size: Dp = 4.dp
) {
  val context = LocalContext.current

  Dialog(
    onDismissRequest = {

    },
    properties = DialogProperties(
      usePlatformDefaultWidth = false
    )
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .zIndex(1F)
        .alpha(1f)
        .background(Color.Transparent)
        .pointerInteropFilter { true },
      contentAlignment = Alignment.Center
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth(0.9f)
          .fillMaxHeight(0.1f)
          .background(LocalColor.Monochrome.White),
        verticalAlignment = Alignment.CenterVertically
      ) {
        CircularProgressIndicator(
          modifier = Modifier
            .padding(horizontal = 20.dp),
          color = color,
          strokeWidth = size
        )
        Label(
          title = "Loading..",
          semiBold = true,
          black = true
        )
      }
    }
  }
}
