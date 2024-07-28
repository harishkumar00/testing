package com.rentlymeari

import android.content.Intent
import android.util.Log
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap
import com.rentlymeari.meari.Meari
import com.rentlymeari.util.ReactParamsCheck
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RentlyMeariModule(private val reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

  override fun getName(): String {
    return NAME
  }

  private val scope = CoroutineScope(Dispatchers.IO)

  @ReactMethod
  fun openLivePreview(params: ReadableMap) {

    val intent = Intent(reactContext, MeariActivity::class.java)

    if (intent.resolveActivity(reactContext.packageManager) != null) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      intent.putExtra("deviceId", params.getString("deviceId"))

      reactContext.startActivity(intent)
    }
  }

  @ReactMethod
  fun getTokenForQRCode(promise: Promise) {
    try {
      scope.launch {
        val token = Meari.getToken()
        if (token != null) {
          promise.resolve(token)
        } else {
          promise.reject("Error", "Failed to get Token")
        }
      }
    } catch (e: Exception) {
      promise.reject("Error", e)
    }
  }

  @ReactMethod
  fun login(params: ReadableMap, promise: Promise) {
    try {
      if (ReactParamsCheck.checkParams(
          arrayOf("account", "password", "countryCode", "phoneCode"),
          params
        )
      ) {
        scope.launch {
          val success = Meari.login(
            account = params.getString("account")!!,
            password = params.getString("password")!!,
            countryCode = params.getString("countryCode")!!,
            phoneCode = params.getString("phoneCode")!!
          )
          if (success == true) {
            promise.resolve(true)
          } else {
            promise.reject("false", "Failed to login")
          }
        }
      } else {
        promise.reject("false", "Invalid params")
      }
    } catch (e: Exception) {
      Log.i("Doorbell", "Error in @ReactMethod login: ${e.message}")
      promise.reject("false", e)
    }
  }

  companion object {
    const val NAME = "RentlyMeari"
  }
}
