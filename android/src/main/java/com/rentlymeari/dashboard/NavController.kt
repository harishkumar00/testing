package com.rentlymeari.dashboard

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.meari.sdk.MeariUser
import com.meari.sdk.bean.CameraInfo
import com.rentlymeari.R
import com.rentlymeari.components.Label
import com.rentlymeari.components.LoadingIndicator
import com.rentlymeari.meari.Meari
import com.rentlymeari.ui.theme.LocalColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun NavController(
  cameraInfo: MutableState<CameraInfo?>
) {
  val scope = rememberCoroutineScope()
  val navController = rememberNavController()
  val activity = LocalContext.current as Activity

  val currentDestination = remember { mutableStateOf("Doorbell") }
  val isMuted = remember { mutableStateOf(false) }
  val isSettings = remember { mutableStateOf(false) }
  val isAdvancedSettings = remember { mutableStateOf(false) }
  val isStorageSettings = remember { mutableStateOf(false) }
  val isDatePickerVisible = remember { mutableStateOf(false) }
  val isLoading = remember { mutableStateOf(false) }

  BackHandler {
    handleBackNavigation(
      navController = navController,
      isAdvancedSettings = isAdvancedSettings,
      isStorageSettings = isStorageSettings,
      isSettings = isSettings,
      currentDestination = currentDestination,
      activity = activity,
      scope = scope,
      isLoading = isLoading
    )
  }

  Column(modifier = Modifier.fillMaxSize()) {
    TopBar(
      title = if (isAdvancedSettings.value) "Advanced Settings" else if (isStorageSettings.value) "SD Card" else currentDestination.value,
      onTrailingIconClick = {
        if (currentDestination.value == "Playback") {
          isDatePickerVisible.value = true
        } else {
          isSettings.value = true
          navController.navigate("Settings")
        }
      },
      onBack = {
        handleBackNavigation(
          navController = navController,
          isAdvancedSettings = isAdvancedSettings,
          isStorageSettings = isStorageSettings,
          isSettings = isSettings,
          currentDestination = currentDestination,
          activity = activity,
          scope = scope,
          isLoading = isLoading
        )
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
        if (isLoading.value) {
          LoadingIndicator(title = "Closing Connection...")
        }
        Dashboard(
          modifier = Modifier.weight(if (!isSettings.value) 0.7f else 1f),
          cameraInfo = cameraInfo,
          navController = navController,
          isMuted = isMuted
        )
      }
      composable("Playback") {
        currentDestination.value = "Playback"
        Playback(
          isDatePickerVisible = isDatePickerVisible,
          isMuted = isMuted
        )
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

  val isSpecialTitle = title in listOf("Settings", "Messages", "Advanced Settings", "SD Card")

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

private fun handleBackNavigation(
  navController: NavController,
  isAdvancedSettings: MutableState<Boolean>,
  isStorageSettings: MutableState<Boolean>,
  isSettings: MutableState<Boolean>,
  currentDestination: MutableState<String>,
  activity: Activity,
  scope: CoroutineScope,
  isLoading: MutableState<Boolean>
) {
  if (isAdvancedSettings.value || isStorageSettings.value) {
    isAdvancedSettings.value = false
    isStorageSettings.value = false
    navController.navigate("Settings")
  } else if (currentDestination.value == "Doorbell") {
    scope.launch {
      val success = Meari.disconnect(isLoading = isLoading) == true
      if (!MeariUser.getInstance().controller.isConnected || success) {
        activity.finish()
      }
    }
  } else {
    isSettings.value = false
    navController.navigate("Dashboard")
  }
}

