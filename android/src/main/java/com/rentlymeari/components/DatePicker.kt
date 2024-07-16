package com.rentlymeari.components

import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.MutableState
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(
  isVisible: MutableState<Boolean>,
  initialDate: Long = Calendar.getInstance().timeInMillis,
  selectedDate: MutableLongState
) {

  val datePickerState = rememberDatePickerState(initialDate)

  if (isVisible.value) {
    DatePickerDialog(
      onDismissRequest = {
        isVisible.value = false
      },
      confirmButton = {
        TextButton(
          onClick = {
            selectedDate.longValue = datePickerState.selectedDateMillis!!
            isVisible.value = false
          }
        ) {
          Label(
            title = "OK",
            black = true,
            bold = true,
            id = "datePickerOk"
          )
        }
      },
      dismissButton = {
        TextButton(
          onClick = {
            isVisible.value = false
          }
        ) {
          Label(
            title = "Cancel",
            black = true,
            bold = true,
            id = "datePickerCancel"
          )
        }
      }
    ) {
      androidx.compose.material3.DatePicker(
        state = datePickerState
      )
    }
  }
}
