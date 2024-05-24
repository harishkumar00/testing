package com.rentlymeari.meari

import android.text.TextUtils
import com.meari.sdk.bean.CameraInfo
import com.meari.sdk.json.BaseJSONObject
import org.json.JSONException

object CommonUtils {
  fun getDefaultStreamId(cameraInfo: CameraInfo): String {
    var streamId = "1"
    if (cameraInfo.vst == 1) {
      streamId = "0"
    } else {
      if (!TextUtils.isEmpty(cameraInfo.bps2)) {
        try {
          val `object` = BaseJSONObject(cameraInfo.bps2)
          if (`object`.has("0")) {
            streamId = "100"
          } else if (`object`.has("1")) {
            streamId = "101"
          } else if (`object`.has("2")) {
            streamId = "102"
          } else if (`object`.has("3")) {
            streamId = "103"
          }
        } catch (e: JSONException) {
          e.printStackTrace()
        }
      } else if (cameraInfo.bps == 0 || cameraInfo.bps == -1) {
        streamId = "0"
      } else {
        streamId = "1"
      }
    }
    return streamId
  }

  fun getTalkType(cameraInfo: CameraInfo): Int {
    val vtk = cameraInfo.vtk
    return if (vtk == 0) {
      1
    } else {
      2
    }
  }
}
