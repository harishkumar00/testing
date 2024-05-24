package com.rentlymeari.components

import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.rentlymeari.ui.theme.LocalColor
import androidx.compose.material3.Switch as MaterialSwitch

@Composable
fun Switch(
  modifier: Modifier = Modifier,
  id: String,
  disabled: Boolean = false,
  checked: Boolean,
  // Color
  primary: Boolean = false,
  onChange: (state: Boolean) -> Unit,
) {
  val color = when {
    primary -> LocalColor.Primary.Dark
    else -> LocalColor.Primary.Dark
  }

  MaterialSwitch(
    modifier = Modifier
      .semantics { contentDescription = id }
      .then(modifier),
    enabled = !disabled,
    checked = checked,
    onCheckedChange = {
      onChange(it)
    },
    // interactionSource = remember { NoRippleInteractionSource() },
    colors = SwitchDefaults.colors(
      checkedThumbColor = LocalColor.Monochrome.White,
      disabledCheckedThumbColor = LocalColor.Monochrome.White,
      checkedTrackColor = color,
      disabledCheckedTrackColor = color.copy(alpha = 0.5f),

      uncheckedThumbColor = LocalColor.Monochrome.White,
      disabledUncheckedThumbColor = LocalColor.Monochrome.White,
      uncheckedTrackColor = LocalColor.Monochrome.Regular,
      disabledUncheckedTrackColor = LocalColor.Monochrome.White.copy(alpha = 0.5f),
      uncheckedBorderColor = LocalColor.Monochrome.Regular
    )
  )
}
