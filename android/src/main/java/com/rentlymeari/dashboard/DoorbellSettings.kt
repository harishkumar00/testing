package com.rentlymeari.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rentlymeari.components.Divider
import com.rentlymeari.components.Label
import com.rentlymeari.components.Switch
import com.rentlymeari.ui.theme.LocalColor

@Composable
fun DoorbellSettings() {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
  ) {
    item {
      Heading(title = "Basic Settings")
    }
    item {
      SettingsItem(
        title = "Flip Screen",
        description = "Flip the doorbell camera preview screen.",
        withSwitch = true
      )
    }
    item {
      SettingsItem(
        title = "IR Night Vision",
        description = "Doorbell camera's night vision settings."
      )
    }

    item {
      Heading(title = "Motion Detection")
    }
    item {
      SettingsItem(
        title = "Motion Detection",
        description = "Triggered motion will send push notifications.",
        withSwitch = true
      )
    }
    item {
      SettingsItem(
        title = "Motion Sensitivity Level",
      )
    }

    item {
      Heading(title = "Storage Settings")
    }
    item {
      SettingsItem(
        title = "Storage Setting",
        description = "Functioning as expected."
      )
    }
    item {
      SettingsItem(
        title = "Reset WiFi",
      )
    }

    item {
      Heading(title = "Power Information")
    }
    item {
      SettingsItem(
        title = "Power Source",
      )
    }

    item {
      Heading(title = "Bell/Chime Connection Settings")
    }
    item {
      SettingsItem(
        title = "Chime Type",
        description = "Mechanical chime type requires the doorbell to be wired to the home doorbell chime system. Digital chime doesn’t require to be wired."
      )
    }
  }
}

@Composable
fun Heading(
  title: String
) {
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
      xl18 = true
    )
  }
}

@Composable
fun SettingsItem(
  title: String,
  description: String = "",
  withSwitch: Boolean = false,
  checked: Boolean = false,
) {

  Column(
    modifier = Modifier
      .background(LocalColor.Secondary.White)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth(0.7f)
          .padding(vertical = 10.dp)
      ) {
        Label(
          modifier = Modifier,
          id = title.lowercase(),
          title = title,
          m = true,
          grey = true,
          bold = true,
        )

        if (description.isNotBlank()) {
          Label(
            modifier = Modifier
              .padding(top = 5.dp),
            id = title.lowercase() + "Description",
            title = description,
            maxLines = 4,
            s = true,
            lightGrey = true,
            semiBold = true,
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
              checked = checked
            ) {

            }
          }
//                   else -> {
//                        Icon(
//                            modifier = Modifier
//                                .size(20.dp)
//                                .padding(start = 15.dp, end = 20.dp),
//                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_blue_right),
//                            contentDescription = "rightArrow",
//                        )
//                    }
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
