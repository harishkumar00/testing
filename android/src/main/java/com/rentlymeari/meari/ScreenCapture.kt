package com.rentlymeari.meari

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.MutableState
import com.meari.sdk.MeariUser
import com.meari.sdk.listener.MeariDeviceListener
import com.meari.sdk.listener.MeariDeviceRecordMp4Listener
import com.rentlymeari.MeariActivity
import com.rentlymeari.util.PermissionHandler
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume

object ScreenCapture {

  private var videoFilePath: String? = null

  suspend fun screenShot(context: Context): Boolean {
    return if (PermissionHandler.isPermissionGranted(
        context,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
      )
    ) {
      val appContext = MeariActivity.getAppContext() ?: return false
      val directory: File? = appContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
      directory?.let {
        val fileName = "screenshot_${System.currentTimeMillis()}.jpg"
        val file = File(it, fileName)
        val path = file.absolutePath

        suspendCancellableCoroutine<Boolean> { continuation ->
          MeariUser.getInstance().controller.snapshot(path, object : MeariDeviceListener {
            override fun onSuccess(s: String) {
              try {
                Log.i("Doorbell", "Screenshot success: $s")
                val bitmap = BitmapFactory.decodeFile(s)
                saveImageToStorage(bitmap, appContext)
                continuation.resume(true)
              } catch (e: Exception) {
                Log.i("Doorbell", "Error in saving image: ${e.message.toString()}")
                continuation.resume(false)
              }
            }

            override fun onFailed(s: String) {
              Log.i("Doorbell", "Screenshot failed: $s")
              continuation.resume(false)
            }
          })
        }
      } ?: run {
        Log.i("Doorbell", "Screenshot Directory is null")
        false
      }
    } else {
      PermissionHandler.requestPermission(
        activity = context as Activity,
        permission = Manifest.permission.WRITE_EXTERNAL_STORAGE,
        requestCode = 10
      )
      false
    }
  }

  fun startRecording(
    context: Context,
    isRecording: MutableState<Boolean>
  ) {
    if (PermissionHandler.isPermissionGranted(
        context,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
      )
    ) {
      try {
        val appContext = MeariActivity.getAppContext()
        val directory: File? = appContext?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        directory?.let {
          val fileName = "recording_${System.currentTimeMillis()}.mp4"
          val file = File(it, fileName)
          val path = file.absolutePath
          videoFilePath = path
          MeariUser.getInstance().controller.startRecordMP4(path, object : MeariDeviceListener {
            override fun onSuccess(s: String) {
              Log.i("Doorbell", "Recording Started: $s")
              isRecording.value = true
            }

            override fun onFailed(s: String) {
              Log.i("Doorbell", "Recording Failed: $s")
              isRecording.value = false
            }
          }, MeariDeviceRecordMp4Listener { i ->
//          if (i > 0) {
//          } else {
//          }
          })
        } ?: run {
          Log.i("Doorbell", "Recording Directory is null")
          isRecording.value = false
        }
      } catch (e: Exception) {
        isRecording.value = false
        Log.i("Doorbell", "Error in start recording: ${e.message}")
      }
    } else {
      PermissionHandler.requestPermission(
        activity = context as Activity,
        permission = Manifest.permission.WRITE_EXTERNAL_STORAGE,
        requestCode = 10
      )
    }
  }

  suspend fun stopRecording(isRecording: MutableState<Boolean>): Boolean {
    val context = MeariActivity.getAppContext() ?: return false

    return suspendCancellableCoroutine { continuation ->
      MeariUser.getInstance().controller.stopRecordMP4(object : MeariDeviceListener {
        override fun onSuccess(s: String) {
          try {
            videoFilePath?.let { path ->
              saveVideoToMedia(path, context)
            }
            continuation.resume(true)
            isRecording.value = false
            Log.i("Doorbell", "Stop Recording Success: $s")
          } catch (e: Exception) {
            continuation.resume(false)
            isRecording.value = false
            Log.e("Doorbell", "Exception while saving video: ${e.message}")
          }
        }

        override fun onFailed(s: String) {
          continuation.resume(false)
          isRecording.value = true
          Log.i("Doorbell", "Stop Recording Failed: $s")
        }
      })
    }
  }

  private fun saveImageToStorage(bitmap: Bitmap, context: Context) {
    try {
      val filename = "${System.currentTimeMillis()}.jpg"
      context.contentResolver?.also { resolver ->
        val contentValues = ContentValues().apply {
          put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
          put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
          put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val imageUri: Uri? =
          resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        imageUri?.let { uri ->
          resolver.openOutputStream(uri)?.use { fos ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
          }
          Log.i("Doorbell", "Screenshot saved to: $uri")
        }
      }
    } catch (e: Exception) {
      Log.i("Doorbell", "Error in saving image: ${e.message.toString()}")
    }
  }

  private fun saveVideoToMedia(filePath: String, context: Context) {
    try {
      val filename = "${System.currentTimeMillis()}.mp4"
      context.contentResolver?.also { resolver ->
        val contentValues = ContentValues().apply {
          put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
          put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
          put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MOVIES)
        }

        val videoUri: Uri? =
          resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
        videoUri?.let { uri ->
          resolver.openOutputStream(uri)?.use { fos ->
            File(filePath).inputStream().use { fis ->
              fis.copyTo(fos)
            }
          }
          Log.i("Doorbell", "Video saved to: $uri")
        }
      }
    } catch (e: Exception) {
      Log.i("Doorbell", "Error in saving video: ${e.message.toString()}")
    }
  }
}
