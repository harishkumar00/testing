package com.rentlymeari.util

import com.facebook.react.bridge.ReadableMap

object ReactParamsCheck {
  fun checkParams(keys: Array<String>, params: ReadableMap): Boolean {
    for (key in keys) {
      if (!params.hasKey(key)) {
        throw IllegalArgumentException("$key Required")
      }
    }
    return true
  }
}
