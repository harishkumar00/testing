package com.rentlymeari.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import com.rentlymeari.ui.theme.LocalColor
import com.rentlymeari.ui.theme.LocalFont.FontFamily
import com.rentlymeari.ui.theme.LocalFont.FontSize

@Composable
fun Label(
  modifier: Modifier = Modifier,
  id: String = "",
  title: String,
  annotatedTitle: AnnotatedString = AnnotatedString(""),
  // Font Size
  xs: Boolean = false,
  s: Boolean = false,
  m: Boolean = false,
  l: Boolean = false,
  xl18: Boolean = false,
  xl20: Boolean = false,
  xl22: Boolean = false,
  xl24: Boolean = false,
  xl26: Boolean = false,
  xl28: Boolean = false,
  xl30: Boolean = false,
  xl34: Boolean = false,
  xl40: Boolean = false,
  xl50: Boolean = false,
  xl80: Boolean = false,
  xl200: Boolean = false,
  // Color
  primary: Boolean = false,
  secondary: Boolean = false,
  lightGrey: Boolean = false,
  grey: Boolean = false,
  white: Boolean = false,
  black: Boolean = false,
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
  maxLines: Int = 1,
  clip: Boolean = false,
  ellipsis: Boolean = false,
  //Letter Spacing
  letterSpacing: TextUnit = TextUnit.Unspecified,
  lineHeight: TextUnit = TextUnit.Unspecified
) {
  val textAlign = when {
    left -> TextAlign.Left
    right -> TextAlign.Right
    center -> TextAlign.Center
    else -> TextAlign.Start
  }

  val color = when {
    primary -> LocalColor.Primary.Light
    secondary -> LocalColor.Primary.Secondary
    lightGrey -> LocalColor.Monochrome.Regular
    grey -> LocalColor.Monochrome.Medium
    white -> LocalColor.Monochrome.White
    black -> LocalColor.Monochrome.Black
    else -> LocalColor.Primary.Light
  }

  val textDecoration = when {
    underLine -> TextDecoration.Underline
    strikeThrough -> TextDecoration.LineThrough
    else -> TextDecoration.None
  }

  val fontFamily = when {
    light -> FontFamily.light
    medium -> FontFamily.medium
    semiBold -> FontFamily.semiBold
    bold -> FontFamily.bold
    else -> FontFamily.regular
  }

  val fontSize = when {
    xs -> FontSize.XS
    s -> FontSize.S
    m -> FontSize.M
    l -> FontSize.L
    xl18 -> FontSize.XL18
    xl20 -> FontSize.XL20
    xl22 -> FontSize.XL22
    xl24 -> FontSize.XL24
    xl26 -> FontSize.XL26
    xl28 -> FontSize.XL28
    xl30 -> FontSize.XL30
    xl34 -> FontSize.XL34
    xl40 -> FontSize.XL40
    xl50 -> FontSize.XL50
    xl80 -> FontSize.XL80
    xl200 -> FontSize.XL200
    else -> FontSize.L
  }

  val overflow = when {
    clip -> TextOverflow.Clip
    ellipsis -> TextOverflow.Ellipsis
    else -> TextOverflow.Visible
  }

  val text = when {
    title.isNotEmpty() -> AnnotatedString(title)
    else -> annotatedTitle
  }

  Text(
    text = text,
    fontSize = fontSize,
    fontFamily = fontFamily,
    textAlign = textAlign,
    color = color,
    textDecoration = textDecoration,
    maxLines = maxLines,
    overflow = overflow,
    modifier = Modifier
      .semantics { contentDescription = id.ifEmpty { title } }
      .testTag(id.ifEmpty { title })
      .then(modifier),
    letterSpacing = letterSpacing,
    lineHeight = lineHeight
  )
}
