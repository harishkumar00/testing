package com.rentlymeari.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.rentlymeari.ui.theme.LocalColor

@Composable
fun Alert(
  modifier: Modifier = Modifier,
  isVisible: MutableState<Boolean>,
  content: @Composable () -> Unit
) {

  if (isVisible.value) {
    Dialog(
      onDismissRequest = {
        isVisible.value = false
      },
      properties = DialogProperties(
        usePlatformDefaultWidth = false
      )
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth(0.9f)
          .background(LocalColor.Monochrome.White)
          .then(modifier),
      ) {
        content()
      }
    }
  }
}
