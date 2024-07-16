package com.rentlymeari.dashboard

import androidx.activity.compose.BackHandler
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.meari.sdk.bean.CameraInfo
import com.rentlymeari.R
import com.rentlymeari.components.Label
import com.rentlymeari.ui.theme.LocalColor

@Composable
fun NavController(
  cameraInfo: MutableState<CameraInfo?>
) {
  val navController = rememberNavController()

  val currentDestination = remember { mutableStateOf("Doorbell") }
  val isMuted = remember { mutableStateOf(false) }
  val isSettings = remember { mutableStateOf(false) }
  val isAdvancedSettings = remember { mutableStateOf(false) }
  val isStorageSettings = remember { mutableStateOf(false) }
  val isDatePickerVisible = remember { mutableStateOf(false) }

  BackHandler {
    if (isAdvancedSettings.value || isStorageSettings.value) {
      isAdvancedSettings.value = false
      isStorageSettings.value = false
      navController.navigate("Settings")
    } else {
      isSettings.value = false
      navController.navigate("Dashboard")
    }
  }

  Column(modifier = Modifier.fillMaxSize()) {
    TopBar(
      title = if (isAdvancedSettings.value) "Advanced Settings" else if (isStorageSettings.value) "Storage Settings" else currentDestination.value,
      onTrailingIconClick = {
        if (currentDestination.value == "Playback") {
          isDatePickerVisible.value = true
        } else {
          isSettings.value = true
          navController.navigate("Settings")
        }
      },
      onBack = {
        if (isAdvancedSettings.value || isStorageSettings.value) {
          isAdvancedSettings.value = false
          isStorageSettings.value = false
          navController.navigate("Settings")
        } else {
          isSettings.value = false
          navController.navigate("Dashboard")
        }
      }
    )

    NavHost(
      navController = navController,
      startDestination = "Dashboard",
      modifier = Modifier.fillMaxSize(),
      enterTransition = { slideInHorizontally() },
      exitTransition = { slideOutHorizontally() }
    ) {
      composable("Dashboard") {
        currentDestination.value = "Doorbell"
        Dashboard(
          modifier = Modifier.weight(if (!isSettings.value) 0.7f else 1f),
          cameraInfo = cameraInfo,
          navController = navController,
          isMuted = isMuted
        )
      }
      composable("Playback") {
        currentDestination.value = "Playback"
        Playback(isDatePickerVisible = isDatePickerVisible)
      }
      composable("Messages") {
        currentDestination.value = "Messages"
        Messages(cameraInfo = cameraInfo.value)
      }
      composable("Settings") {
        currentDestination.value = "Settings"
        DoorbellSettings(
          cameraInfo = cameraInfo.value,
          isAdvancedSettings = isAdvancedSettings,
          isStorageSettings = isStorageSettings
        )
      }
    }
  }
}

@Composable
fun TopBar(
  title: String,
  onBack: () -> Unit = {},
  onTrailingIconClick: () -> Unit = {}
) {
  val isMessagesOrSettings = title == "Settings" || title == "Messages"
  val isAdvancedSettingsOrStorageSettings = title == "Advanced Settings" || title == "Storage Settings"
  val isSpecialTitle = isMessagesOrSettings || isAdvancedSettingsOrStorageSettings

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight(0.075f)
  ) {
    Row(
      modifier = Modifier
        .fillMaxSize()
        .background(if (isSpecialTitle) LocalColor.Secondary.White else LocalColor.Monochrome.Grey),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        modifier = Modifier.padding(top = 3.dp, start = 5.dp, end = 10.dp),
        onClick = onBack
      ) {
        Image(
          modifier = Modifier.size(35.dp),
          painter = painterResource(
            id = if (isSpecialTitle) R.drawable.ic_action_arrow_backward else R.drawable.ic_arrow_backward_white
          ),
          contentDescription = "back"
        )
      }
      Label(
        id = "$title Id",
        modifier = Modifier.padding(18.dp),
        title = title,
        semiBold = true,
        xl20 = true,
        white = !isSpecialTitle,
        black = isSpecialTitle
      )
    }
    if (!isSpecialTitle) {
      IconButton(
        modifier = Modifier
          .align(Alignment.CenterEnd)
          .padding(top = 3.dp, start = 5.dp, end = 10.dp),
        onClick = onTrailingIconClick
      ) {
        Image(
          modifier = Modifier.size(30.dp),
          painter = painterResource(
            id = if (title == "Playback") R.drawable.ic_calendar else R.drawable.ic_setting
          ),
          contentDescription = "trailingIcon"
        )
      }
    }
  }
}

