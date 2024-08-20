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
import android.app.Activity
import com.facebook.react.ReactActivity
import com.facebook.react.ReactApplication
import com.facebook.react.ReactFragment
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactRootView

class RentlyMeariModule(private val reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

  override fun getName(): String {
    return NAME
  }

  private val scope = CoroutineScope(Dispatchers.IO)

  @ReactMethod
  fun openLivePreview(params: ReadableMap, promise: Promise) {
    try {
      val intent = Intent(reactContext, MeariActivity::class.java)
      promise.resolve(true)

      if (intent.resolveActivity(reactContext.packageManager) != null) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("deviceId", params.getString("deviceId"))

        reactContext.startActivity(intent)
      }
    } catch (e: Exception) {
      promise.reject("Error", "${e.message}")
      e.printStackTrace()
    }
  }

  @ReactMethod
  fun openJSScreen(params: ReadableMap, promise: Promise) {
    try {
      Log.i("Harish", "called here")
      val intent = Intent(reactContext, JSActivity::class.java)
      promise.resolve(true)

      if (intent.resolveActivity(reactContext.packageManager) != null) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("name", params.getString("name"))

        reactContext.startActivity(intent)
      }
    } catch (e: Exception) {
      promise.reject("Error", "${e.message}")
      e.printStackTrace()
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
  fun setupPushNotification(params: ReadableMap, promise: Promise) {
    try {
      if (ReactParamsCheck.checkParams(arrayOf("token"), params)) {
        scope.launch {
          val success = Meari.setupPushNotification(token = params.getString("token")!!)
          if (success == true) {
            promise.resolve(true)
          } else {
            promise.reject("Error", "")
          }
        }
      } else {
        promise.reject("Error", "Invalid params")
      }
    } catch (e: Exception) {
      e.printStackTrace()
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
