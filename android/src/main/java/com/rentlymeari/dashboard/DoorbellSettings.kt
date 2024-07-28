package com.rentlymeari.dashboard

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.meari.sdk.bean.CameraInfo
import com.rentlymeari.R
import com.rentlymeari.components.Button
import com.rentlymeari.components.Divider
import com.rentlymeari.components.Label
import com.rentlymeari.components.LoadingIndicator
import com.rentlymeari.components.ResetWifiAlert
import com.rentlymeari.components.Switch
import com.rentlymeari.meari.Meari
import com.rentlymeari.meari.SDCard
import com.rentlymeari.meari.Settings
import com.rentlymeari.ui.theme.LocalColor
import kotlinx.coroutines.launch

enum class MotionSensitivityLevel(val level: String) {
  LOW("Low"),
  MEDIUM("Medium"),
  HIGH("High")
}

enum class NightVision(val mode: String) {
  AUTO("Auto"),
  ON("On"),
  OFF("Off")
}

enum class SDCardRecordType(val type: String) {
  ALL_DAY_RECORDING("All Day Recording"),
  EVENT_RECORDING("Event Recording")
}

enum class ChimeType(val type: String) {
  WIRELESS("Wireless"),
  MECHANICAL("Mechanical")
}

@Composable
fun DoorbellSettings(
  cameraInfo: CameraInfo?,
  isAdvancedSettings: MutableState<Boolean>,
  isStorageSettings: MutableState<Boolean>
) {

  val isFlipEnabled = remember { mutableStateOf(cameraInfo?.deviceParams?.mirrorEnable == 1) }
  val nightVisionLevel = remember { mutableStateOf(cameraInfo?.deviceParams?.dayNightMode) }
  val isMotionDetectionEnabled =
    remember { mutableStateOf(cameraInfo?.deviceParams?.motionDetEnable == 1) }
  val motionSensitivityLevel =
    remember { mutableStateOf(cameraInfo?.deviceParams?.motionDetSensitivity) }
  val sdCardRecordingType = remember { mutableStateOf(cameraInfo?.deviceParams?.sdRecordType) }
  val isWirelessChimeEnabled =
    remember { mutableStateOf(cameraInfo?.deviceParams?.wirelessChimeEnable) }
  val isMechanicalChimeEnabled =
    remember { mutableStateOf(cameraInfo?.deviceParams?.mechanicalChimeEnable) }
  val isPowerType = remember { mutableStateOf(cameraInfo?.deviceParams?.powerType) }
  val isResetWifiAlertVisible = remember { mutableStateOf(false) }
  val isLoading = remember { mutableStateOf(false) }

  val scope = rememberCoroutineScope()

  Log.i(
    "Doorbell", """
        Power Type: ${cameraInfo?.deviceParams?.powerType}
        Battery Percent: ${cameraInfo?.deviceParams?.batteryPercent}
        Battery Remaining: ${cameraInfo?.deviceParams?.batteryRemaining}
        Charge Status: ${cameraInfo?.deviceParams?.chargeStatus}
    """.trimIndent()
  )


  if (isLoading.value) {
    LoadingIndicator()
  }

  ResetWifiAlert(
    isResetWifiAlertVisible = isResetWifiAlertVisible,
    isLoading = isLoading
  )

  when {
    isAdvancedSettings.value -> AdvancedSettings(cameraInfo = cameraInfo)
    isStorageSettings.value -> StorageSettings()
    else -> {
      LazyColumn(
        modifier = Modifier.fillMaxSize()
      ) {
        item {
          Heading(title = "Basic Settings")
        }
        item {
          SettingsItem(
            title = "Flip Screen",
            description = "Flip the doorbell camera preview screen.",
            withSwitch = true,
            checked = isFlipEnabled.value,
            onToggle = {
              Settings.setFlipScreen(
                mirrorEnable = if (it) 1 else 0,
                isLoading = isLoading,
                isFlipEnabled = isFlipEnabled
              )
            }
          )
        }
        item {
          SettingsItem(
            title = "IR Night Vision",
            description = "Doorbell camera's night vision settings.",
            withDropDown = true,
            dropDownTitle = "Night Vision",
            dropDownList = enumValues<NightVision>().map { it.mode },
            selectedOption = when (nightVisionLevel.value) {
              0 -> {
                NightVision.AUTO.mode
              }

              1 -> {
                NightVision.OFF.mode
              }

              2 -> {
                NightVision.ON.mode
              }

              else -> {
                ""
              }
            },
            onDropDownItemSelected = {
              val newMode = when (it) {
                NightVision.AUTO.mode -> 0

                NightVision.OFF.mode -> 1

                NightVision.ON.mode -> 2

                else -> null
              }

              if (cameraInfo != null) {
                Settings.setDayNightMode(
                  mode = nightVisionLevel,
                  newMode = newMode,
                  isLoading = isLoading
                )
              }
            }
          )
        }

        item {
          Heading(title = "Motion Detection")
        }
        item {
          SettingsItem(
            title = "Motion Detection",
            description = "Triggered motion will send push notifications.",
            withSwitch = true,
            checked = isMotionDetectionEnabled.value,
            onToggle = {
              if (cameraInfo != null) {
                Settings.setMotionEnable(
                  enable = if (it) 1 else 0,
                  sensitivityLevel = cameraInfo.deviceParams?.motionDetSensitivity,
                  isLoading = isLoading,
                  isMotionDetectionEnabled = isMotionDetectionEnabled,
                  motionSensitivityLevel = motionSensitivityLevel
                )
              }
            }
          )
        }

        item {
          AnimatedVisibility(visible = isMotionDetectionEnabled.value) {
            SettingsItem(
              title = "Motion Sensitivity Level",
              withDropDown = true,
              dropDownTitle = "Motion Sensitivity Level",
              dropDownList = enumValues<MotionSensitivityLevel>().map { it.level },
              selectedOption = when (motionSensitivityLevel.value) {
                0 -> {
                  MotionSensitivityLevel.LOW.level
                }

                1 -> {
                  MotionSensitivityLevel.MEDIUM.level
                }

                2 -> {
                  MotionSensitivityLevel.HIGH.level
                }

                else -> {
                  ""
                }
              },
              onDropDownItemSelected = {
                val newMode = when (it) {
                  MotionSensitivityLevel.LOW.level -> 0

                  MotionSensitivityLevel.MEDIUM.level -> 1

                  MotionSensitivityLevel.HIGH.level -> 2

                  else -> null
                }

                if (cameraInfo != null) {
                  Settings.setMotionEnable(
                    enable = 1,
                    sensitivityLevel = newMode,
                    isLoading = isLoading,
                    isMotionDetectionEnabled = isMotionDetectionEnabled,
                    motionSensitivityLevel = motionSensitivityLevel
                  )
                }
              }
            ) {

            }
          }
        }


        item {
          Heading(title = "Storage Settings")
        }
        item {
          SettingsItem(
            title = "Storage Setting",
            description = "Functioning as expected.",
            onClick = {
              isStorageSettings.value = true
            }
          )
        }

        item {
          SettingsItem(
            title = "Recording Mode",
            description = "Event will be recorded continuously.",
            withDropDown = true,
            dropDownTitle = "Recording Mode",
            dropDownList = enumValues<SDCardRecordType>().map { it.type },
            selectedOption = when (sdCardRecordingType.value) {
              0 -> {
                SDCardRecordType.EVENT_RECORDING.type
              }

              1 -> {
                SDCardRecordType.ALL_DAY_RECORDING.type
              }

              else -> {
                ""
              }
            },
            onDropDownItemSelected = {
              val newMode = when (it) {
                SDCardRecordType.EVENT_RECORDING.type -> 0

                SDCardRecordType.ALL_DAY_RECORDING.type -> 1

                else -> null
              }

              if (cameraInfo != null) {
                SDCard.setSDCardRecordType(
                  type = sdCardRecordingType,
                  newMode = newMode,
                  isLoading = isLoading
                )
              }
            }
          )
        }

        item {
          SettingsItem(
            title = "Reset WiFi",
            onClick = {
              isResetWifiAlertVisible.value = true
            }
          )
        }

        if (isPowerType.value != 1) {
          item {
            Heading(title = "Power Information")
          }
          item {
            SettingsItem(
              title = "Power Source",
            )
          }
        }

        item {
          Heading(title = "Bell/Chime Connection Settings")
        }

        item {
          SettingsItem(
            title = "Chime Type",
            description = "Mechanical chime type requires the doorbell to be wired to the home doorbell chime system. Digital chime doesn’t require to be wired.",
            withDropDown = true,
            dropDownTitle = "Chime Type",
            dropDownList = enumValues<ChimeType>().map { it.type },
            selectedOption = when {
              isWirelessChimeEnabled.value == 1 && isMechanicalChimeEnabled.value == 0 -> ChimeType.WIRELESS.type
              isWirelessChimeEnabled.value == 0 && isMechanicalChimeEnabled.value == 1 -> ChimeType.MECHANICAL.type
              else -> ""
            },
            onDropDownItemSelected = {
              val newMode = when (it) {
                ChimeType.WIRELESS.type -> "wireless"

                ChimeType.MECHANICAL.type -> "mechanical"

                else -> null
              }

              Log.i("Doorbell new mode", newMode.toString())

              if (cameraInfo != null) {
                Settings.changeChime(
                  newMode = newMode,
                  isWirelessChimeEnabled = isWirelessChimeEnabled,
                  isMechanicalChimeEnabled = isMechanicalChimeEnabled,
                  isLoading = isLoading
                )
              }
            }
          )
        }

        item {
          Heading(title = "Advanced Settings")
        }
        item {
          SettingsItem(
            title = "Device Info",
            onClick = {
              isAdvancedSettings.value = true
            }
          )
        }

        item {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 15.dp, bottom = 30.dp),
            horizontalArrangement = Arrangement.Center
          ) {
            Button(
              modifier = Modifier
                .width(180.dp)
                .height(50.dp),
              id = "removeDoorbell",
              title = "Remove Doorbell",
              textColor = LocalColor.Monochrome.White,
              secondary = true,
              semiBold = true
            ) {
              if (cameraInfo != null) {
                scope.launch {
                  Meari.removeDoorbell(
                    cameraInfo = cameraInfo
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun Heading(title: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(40.dp)
      .background(LocalColor.Monochrome.White),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Label(
      modifier = Modifier
        .padding(start = 20.dp),
      title = title,
      black = true,
      bold = true,
      l = true
    )
  }
}

@Composable
fun SettingsItem(
  title: String,
  description: String = "",
  withSwitch: Boolean = false,
  withDropDown: Boolean = false,
  checked: Boolean = false,
  dropDownTitle: String = "",
  dropDownList: List<String> = listOf(),
  selectedOption: String = "",
  onDropDownItemSelected: (item: String) -> Unit = {},
  onToggle: (state: Boolean) -> Unit = {},
  onClick: () -> Unit = {}
) {

  Column(modifier = Modifier.background(LocalColor.Secondary.White)) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .clickable(
          interactionSource = remember { MutableInteractionSource() },
          indication = if (withSwitch || withDropDown) {
            null
          } else {
            rememberRipple()
          }
        ) {
          onClick()
        },
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth(0.6f)
          .padding(vertical = 10.dp)
      ) {
        Label(
          modifier = Modifier,
          id = title.lowercase(),
          title = title,
          m = true,
          grey = true,
          semiBold = true,
        )

        if (description.isNotBlank()) {
          Label(
            modifier = Modifier
              .padding(top = 5.dp),
            id = title.lowercase() + "Description",
            title = description,
            maxLines = 6,
            s = true,
            lightGrey = true,
            medium = true,
            lineHeight = 15.sp
          )
        }
      }

      Row(
        modifier = Modifier
          .fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        when {
          withSwitch -> {
            Switch(
              id = title + "Switch",
              checked = checked,
              onChange = onToggle
            )
          }

          withDropDown -> {
            PopUp(
              dropDownTitle = dropDownTitle,
              dropDownList = dropDownList,
              selectedOption = selectedOption,
              onDropDownItemSelected = onDropDownItemSelected,
            )
          }

          else -> {
            Image(
              modifier = Modifier
                .size(12.dp),
              painter = painterResource(id = R.drawable.ic_arrow_blue_right),
              contentDescription = "rightArrow",
            )
          }
        }
      }
    }

    Divider(
      modifier = Modifier
        .padding(horizontal = 10.dp),
      thickness = 1.dp
    )
  }
}

@Composable
fun PopUp(
  dropDownTitle: String = "",
  dropDownList: List<String> = listOf(),
  selectedOption: String = "",
  onDropDownItemSelected: (item: String) -> Unit = {},
) {

  val isVisible = remember {
    mutableStateOf(false)
  }

  Row(
    modifier = Modifier
      .fillMaxHeight()
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null
      ) {
        isVisible.value = true
      },
    horizontalArrangement = Arrangement.End,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Label(
      modifier = Modifier
        .padding(end = 5.dp),
      title = selectedOption,
      black = true,
      semiBold = true,
      m = true
    )
    Image(
      modifier = Modifier
        .size(12.dp),
      painter = painterResource(id = R.drawable.ic_arrow_blue_right),
      contentDescription = "rightArrow",
    )
  }

  if (isVisible.value) {
    Dialog(
      onDismissRequest = {
        isVisible.value = false
      },
      properties = DialogProperties(
        usePlatformDefaultWidth = false
      )
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth(0.9f)
          .background(LocalColor.Monochrome.White)
          .padding(vertical = 10.dp)
      ) {
        Label(
          modifier = Modifier
            .padding(vertical = 10.dp, horizontal = 20.dp),
          title = dropDownTitle,
          bold = true,
          xl20 = true,
          black = true
        )
        dropDownList.forEach {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                onDropDownItemSelected(it)
                isVisible.value = false
              }
          ) {
            Label(
              modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 20.dp),
              title = it,
              black = true,
              semiBold = true,
              l = true
            )
          }
        }
      }
    }
  }
}
