package com.rentlymeari

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.facebook.react.ReactFragment
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler

class JSActivity : AppCompatActivity(), DefaultHardwareBackBtnHandler {
    override fun onCreate(savedInstanceState: Bundle?) {
      Log.i("Harish", "came here JS Activity")
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_jsactivity)
      val reactNativeFragment = ReactFragment.Builder()
        .setComponentName("Home")
        .setLaunchOptions(intent.extras)
        .build()
      supportFragmentManager
        .beginTransaction()
        .add(R.id.main, reactNativeFragment)
        .commit()
    }

  override fun invokeDefaultOnBackPressed() {
    Log.i("Harish", "Back invoked")
  }
}
