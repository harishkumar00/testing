package com.rentlymeari.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionHandler {

  fun isPermissionGranted(context: Context, permission: String): Boolean {
    return ContextCompat.checkSelfPermission(
      context,
      permission
    ) == PackageManager.PERMISSION_GRANTED
  }

  fun arePermissionsGranted(context: Context, permissions: Array<String>): Boolean {
    return permissions.all { permission ->
      ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
  }

  fun checkAndRequestPermissions(
    activity: Activity,
    permissions: Array<String>,
    requestCode: Int
  ): Boolean {
    try {
      val notGrantedPermissions = permissions.filter { permission ->
        ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED
      }

      return if (notGrantedPermissions.isNotEmpty()) {
        ActivityCompat.requestPermissions(
          activity,
          notGrantedPermissions.toTypedArray(),
          requestCode
        )
        false
      } else {
        true
      }
    } catch (e: Exception) {
      e.printStackTrace()
      return false
    }
  }

  fun requestPermission(activity: Activity, permission: String, requestCode: Int) {
    try {
      if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
        ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
      } else {
        ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }
}
