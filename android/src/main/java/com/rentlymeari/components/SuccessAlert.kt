package com.rentlymeari.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SuccessAlert(
  title: String,
  description: String,
  isVisible: MutableState<Boolean>
) {
  Alert(
    modifier = Modifier
      .fillMaxWidth(0.95f)
      .padding(top = 20.dp, start = 25.dp, end = 25.dp),
    isVisible = isVisible,
  ) {
    Column(
      modifier = Modifier
    ) {
      Label(
        modifier = Modifier
          .padding(bottom = 10.dp),
        title = title,
        bold = true,
        xl20 = true,
        black = true,
      )

      Label(
        modifier = Modifier
          .padding(bottom = 10.dp),
        title = description,
        medium = true,
        l = true,
        black = true,
        maxLines = 3,
        lineHeight = 20.sp,
      )

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 5.dp),
        horizontalArrangement = Arrangement.End
      ) {
        AnchorButton(
          modifier = Modifier
            .padding(5.dp),
          id = "ok",
          title = "OK",
          black = true,
          semiBold = true
        ) {
          isVisible.value = false
        }
      }
    }
  }
}
