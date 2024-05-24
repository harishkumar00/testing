package com.rentlymeari.components

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import com.rentlymeari.ui.theme.LocalColor
import com.rentlymeari.ui.theme.LocalFont

@Composable
fun AnchorButton(
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
  // Font Weight
  light: Boolean = false,
  medium: Boolean = false,
  semiBold: Boolean = false,
  bold: Boolean = false,
  // Text Alignment
  left: Boolean = false,
  right: Boolean = false,
  center: Boolean = false,
  // Text Decoration
  underLine: Boolean = false,
  strikeThrough: Boolean = false,
  disabled: Boolean = false,
  onClick: () -> Unit = {}
) {

  val textColor = when {
    primary -> LocalColor.Primary.Light
    else -> LocalColor.Primary.Light
  }

  val textAlign = when {
    left -> TextAlign.Left
    right -> TextAlign.Right
    center -> TextAlign.Center
    else -> TextAlign.Start
  }

  val textDecoration = when {
    underLine -> TextDecoration.Underline
    strikeThrough -> TextDecoration.LineThrough
    else -> TextDecoration.None
  }

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


  TextButton(
    modifier = Modifier
      .semantics {
        contentDescription = id
      }
      .then(modifier),
    onClick = {
      onClick()
    },
    enabled = !disabled,
  ) {
    Text(
      text = title,
      fontSize = fontSize,
      fontFamily = fontFamily,
      textAlign = textAlign,
      textDecoration = textDecoration,
      color = textColor,
      maxLines = 1,
      overflow = TextOverflow.Visible,
      modifier = Modifier
        .then(textModifier)
    )
  }
}
