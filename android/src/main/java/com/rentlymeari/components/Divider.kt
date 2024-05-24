package com.rentlymeari.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rentlymeari.ui.theme.LocalColor

enum class DividerDirection {
  Horizontal,
  Vertical
}

@Composable
fun Divider(
  modifier: Modifier = Modifier,
  thickness: Dp = 1.dp,
  direction: DividerDirection = DividerDirection.Horizontal,
  primary: Boolean = false,
) {
  val targetThickness = when (thickness) {
    Dp.Hairline -> (1f / LocalDensity.current.density).dp
    else -> thickness
  }

  val color = when {
    primary -> LocalColor.Secondary.Light
    else -> LocalColor.Secondary.Light
  }

  Box(
    when (direction) {
      DividerDirection.Vertical -> modifier
        .fillMaxHeight()
        .width(targetThickness)
        .background(color)
        .then(modifier)

      else -> modifier
        .fillMaxWidth()
        .height(targetThickness)
        .background(color)
        .then(modifier)
    }
  )
}
