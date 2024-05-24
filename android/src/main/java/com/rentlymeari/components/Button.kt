package com.rentlymeari.components

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rentlymeari.ui.theme.LocalColor
import com.rentlymeari.ui.theme.LocalFont
import androidx.compose.material3.Button as MaterialButton

@Composable
fun Button(
  modifier: Modifier = Modifier,
  id: String,
  textModifier: Modifier = Modifier,
  title: String,
  // Font Size
  xs: Boolean = false,
  s: Boolean = false,
  m: Boolean = false,
  l: Boolean = false,
  xl18: Boolean = false,
  xl20: Boolean = false,
  xl22: Boolean = false,
  xl24: Boolean = false,
  // Color
  primary: Boolean = false,
  white: Boolean = false,
  grey: Boolean = false,
  black: Boolean = false,
  textColor: Color?,
  borderColor: Color? = null,
  // Font Weight
  light: Boolean = false,
  medium: Boolean = false,
  semiBold: Boolean = false,
  bold: Boolean = false,
  cornerRadius: Dp = 30.dp,
  disabled: Boolean = false,
  elevation: Boolean = true,
  onClick: () -> Unit = {}
) {

  val buttonColor = when {
    primary -> LocalColor.Primary.Medium
    white -> LocalColor.Monochrome.White
    grey -> LocalColor.Monochrome.LightGrey
    black -> LocalColor.Monochrome.Black
    else -> LocalColor.Primary.Light
  }

  val borderModifier = borderColor?.let { color ->
    Modifier.border(1.dp, color, RoundedCornerShape(cornerRadius))
  } ?: Modifier

  val fontFamily = when {
    light -> LocalFont.FontFamily.light
    medium -> LocalFont.FontFamily.medium
    semiBold -> LocalFont.FontFamily.semiBold
    bold -> LocalFont.FontFamily.bold
    else -> LocalFont.FontFamily.regular
  }

  val fontSize = when {
    xs -> LocalFont.FontSize.XS
    s -> LocalFont.FontSize.S
    m -> LocalFont.FontSize.M
    l -> LocalFont.FontSize.L
    xl18 -> LocalFont.FontSize.XL18
    xl20 -> LocalFont.FontSize.XL20
    xl22 -> LocalFont.FontSize.XL22
    xl24 -> LocalFont.FontSize.XL24
    else -> LocalFont.FontSize.L
  }

  MaterialButton(
    modifier = Modifier
      .semantics { contentDescription = "${id}Button" }
      .then(modifier)
      .then(borderModifier),
    enabled = !disabled,
    onClick = {
      onClick()
    },
    shape = RoundedCornerShape(cornerRadius),
    colors = ButtonDefaults.buttonColors(
      containerColor = buttonColor,
      disabledContainerColor = buttonColor.copy(alpha = 0.5F)
    ),
    elevation = when {
      elevation -> ButtonDefaults.elevatedButtonElevation()
      else -> null
    }
  ) {
    Text(
      text = title,
      fontSize = fontSize,
      fontFamily = fontFamily,
      textAlign = TextAlign.Center,
      color = textColor!!,
      maxLines = 1,
      overflow = TextOverflow.Clip,
      modifier = Modifier
        .semantics { contentDescription = "${id}Text" }
        .then(textModifier)
    )
  }
}
