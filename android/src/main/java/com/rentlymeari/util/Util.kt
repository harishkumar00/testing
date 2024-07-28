package com.rentlymeari.util

import java.util.Calendar
import java.util.Locale

object DateUtil {

  fun getDateComponents(dateLong: Long): Triple<Int, Int, Int> {
    val calendar = Calendar.getInstance().apply {
      timeInMillis = dateLong
    }

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH is zero-based
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    return Triple(year, month, day)
  }

  fun calculateDuration(
    startHour: Int,
    startMinute: Int,
    startSecond: Int,
    endHour: Int,
    endMinute: Int,
    endSecond: Int
  ): String {
    val startInSeconds = startHour * 3600 + startMinute * 60 + startSecond
    val endInSeconds = endHour * 3600 + endMinute * 60 + endSecond

    val durationInSeconds = endInSeconds - startInSeconds

    // Handle negative durations (if start time is after end time, which might indicate the next day)
    val totalSeconds = if (durationInSeconds < 0) {
      24 * 3600 + durationInSeconds
    } else {
      durationInSeconds
    }

    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 3600 % 60

    return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
  }
}
