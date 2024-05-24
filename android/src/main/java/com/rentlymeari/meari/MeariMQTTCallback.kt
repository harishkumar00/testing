package com.rentlymeari.meari

import android.text.TextUtils
import android.util.Log
import com.meari.sdk.MeariUser
import com.meari.sdk.bean.FamilyMqttMsg
import com.meari.sdk.bean.MqttMsgType
import com.meari.sdk.listener.MeariDeviceListener
import com.meari.sdk.mqtt.MqttMessageCallback
import org.json.JSONObject
import java.util.Locale

class MeariMQTTCallback : MqttMessageCallback {

  override fun otherMessage(i: Int, s: String) {
    Log.i("Doorbell", "otherMessage $i")
    if (s.contains("motion")) {
      val jsonObject = JSONObject(s)
      val deviceID = jsonObject.getString("deviceID")
    }
  }

  override fun loginOnOtherDevices() {
    // TODO:: Must deal with
    // Must handle account login on other devices
    // An account can only log in on one device at a time
    MeariUser.getInstance().disConnectMqttService()
    MeariUser.getInstance().removeUserInfo()
    val controller = MeariUser.getInstance().controller
    if (controller != null && controller.isConnected) {
      controller.stopConnect(object : MeariDeviceListener {
        override fun onSuccess(successMsg: String) {}
        override fun onFailed(errorMsg: String) {}
      })
    }
  }

  override fun onCancelSharingDevice(s: String, s1: String) {
    Log.i("Doorbell", "onCancelSharingDevice")
  }

  override fun deviceUnbundled() {
    Log.i("Doorbell", "deviceUnBundled")
  }

  override fun onDoorbellCall(s: String, b: Boolean) {
    Log.i("Doorbell", "onDoorbellCall $b")
    val jsonObject = JSONObject(s)
    val deviceID = jsonObject.getString("deviceID")
  }

  override fun onVoiceDoorbellCall(s: String) {
    Log.i("Doorbell", "onVoiceDoorbellCall")
  }

  override fun addDeviceSuccess(s: String) {
    Log.i("Doorbell", "addDeviceSuccess")
  }

  override fun addDeviceFailed(s: String) {
    Log.i("Doorbell", "addDeviceFailed")
  }

  override fun addDeviceFailedUnbundled(s: String) {
    Log.i("Doorbell", "addDeviceFailedUnbundled")
  }

  override fun onChimeDeviceLimit(s: String) {
    Log.i("Doorbell", "onChimeDeviceLimit")
  }

  override fun ReceivedDevice(s: String) {
    Log.i("Doorbell", "ReceivedDevice")
  }

  @Deprecated("Deprecated in Java")
  override fun requestReceivingDevice(s: String, s1: String, s2: String) {
  }

  override fun requestReceivingDevice(s: String, s1: String, s2: String, s3: String) {
    Log.i("Doorbell", "requestReceivingDevice")
  }

  @Deprecated("Deprecated in Java")
  override fun requestShareDevice(s: String, s1: String, s2: String) {
  }

  override fun requestShareDevice(s: String, s1: String, s2: String, s3: String) {
    Log.i("Doorbell", "requestShareDevice")
  }

  override fun onFamilyMessage(familyMqttMsg: FamilyMqttMsg) {
    Log.i("Doorbell", "onFamilyMessage")

    if (familyMqttMsg.itemList.size > 0) {
      for (msgItem in familyMqttMsg.itemList) {
        if (TextUtils.isEmpty(msgItem.name)) {
          val name: String? = if (familyMqttMsg.msgId == MqttMsgType.INVITE_JOIN_HOME) {
            familyMqttMsg.userName
          } else {
            MeariUser.getInstance().userInfo.nickName
          }
          msgItem.name = String.format(Locale.CHINA, "%s's home", name)
        }
      }
    }
    when (familyMqttMsg.msgId) {
      MqttMsgType.FAMILY_INFO_CHANGED -> {

      }

      MqttMsgType.FAMILY_MEMBER_INFO_CHANGED -> {

      }

      MqttMsgType.INVITE_JOIN_HOME -> {

      }

      MqttMsgType.INVITE_JOIN_HOME_SUCCESS -> {

      }

      MqttMsgType.APPLY_ENTER_HOME -> {

      }

      MqttMsgType.APPLY_ENTER_HOME_SUCCESS -> {

      }

      MqttMsgType.REMOVE_FROM_FAMILY -> {

      }
    }
  }

  override fun onCloudServiceDis() {
    Log.i("Doorbell", "onCloudServiceDis")
  }

  override fun onPermissionChanged(s: String) {
    Log.i("Doorbell", "onPermissionChanged")
  }
}
