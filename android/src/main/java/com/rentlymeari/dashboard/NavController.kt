package com.rentlymeari.dashboard

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

  val isMuted = remember {
    mutableStateOf(false)
  }

  val currentDestination = remember {
    mutableStateOf("Doorbell")
  }

  val isSettings = remember {
    mutableStateOf(false)
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
  ) {
    TopBar(
      title = currentDestination.value,
      withBack = true,
      withSettings = true,
      isSettings = isSettings,
      onSettingsClick = {
        isSettings.value = true
        navController.navigate("Settings")
      },
      onBack = {
        isSettings.value = false
        navController.navigate("Dashboard")
      }
    )

    NavHost(
      navController = navController,
      startDestination = "Dashboard",
      modifier = Modifier
        .fillMaxSize(),
      enterTransition = { slideInHorizontally() },
      exitTransition = { slideOutHorizontally() }
    ) {
      composable(
        "Dashboard",
      ) {
        currentDestination.value = "Doorbell"
        Column {
          Dashboard(
            modifier = Modifier
              .weight(
                if (!isSettings.value) {
                  0.7f
                } else {
                  1f
                }
              ),
            cameraInfo = cameraInfo,
            navController = navController,
            isMuted = isMuted
          )
        }
      }

      composable(
        "Playback",
      ) {
        currentDestination.value = "Playback"
        Playback()
      }

      composable(
        "Messages",
      ) {
        currentDestination.value = "Messages"
        Messages()
      }

      composable(
        "Settings",
      ) {
        currentDestination.value = "Settings"
        DoorbellSettings()
      }
    }
  }
}

@Composable
fun TopBar(
  title: String,
  withBack: Boolean,
  withSettings: Boolean,
  isSettings: MutableState<Boolean>,
  onBack: () -> Unit = {},
  onSettingsClick: () -> Unit = {}
) {

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight(0.075f)
  ) {
    Row(
      modifier = Modifier
        .fillMaxSize()
        .background(
          if (isSettings.value) {
            LocalColor.Secondary.White
          } else {
            LocalColor.Monochrome.Grey
          }
        ),
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (withBack) {
        IconButton(
          modifier = Modifier
            .padding(top = 3.dp, start = 5.dp, end = 10.dp),
          onClick = {
            onBack()
          }
        ) {
          Image(
            modifier = Modifier
              .size(35.dp),
            painter = if (isSettings.value) {
              painterResource(id = R.drawable.ic_action_arrow_backward)
            } else {
              painterResource(id = R.drawable.ic_arrow_backward_white)
            },
            contentDescription = "back",
          )
        }
      }

      Label(
        id = title + "Id",
        modifier = Modifier
          .padding(
            if (withBack) {
              0.dp
            } else {
              18.dp
            }
          ),
        title = if (isSettings.value) {
          "Settings"
        } else {
          title
        },
        semiBold = true,
        xl20 = true,
        white = !isSettings.value,
        black = isSettings.value
      )
    }
    if (withSettings && !isSettings.value) {
      IconButton(
        modifier = Modifier
          .align(Alignment.CenterEnd)
          .padding(top = 3.dp, start = 5.dp, end = 10.dp),
        onClick = {
          onSettingsClick()
        }
      ) {
        Image(
          modifier = Modifier
            .size(30.dp),
          painter = painterResource(id = R.drawable.ic_setting),
          contentDescription = "doorbellSettings",
        )
      }
    }
  }
}
