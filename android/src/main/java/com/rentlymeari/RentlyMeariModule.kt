package com.rentlymeari

import android.content.Intent
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap

class RentlyMeariModule(private val reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

  override fun getName(): String {
    return NAME
  }

  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  fun openLivePreview(params: ReadableMap) {

    val intent = Intent(reactContext, MeariActivity::class.java)

    if (intent.resolveActivity(reactContext.packageManager) != null) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      intent.putExtra("deviceId", params.getString("deviceId"))

      reactContext.startActivity(intent)
    }
  }

  companion object {
    const val NAME = "RentlyMeari"
  }
}
