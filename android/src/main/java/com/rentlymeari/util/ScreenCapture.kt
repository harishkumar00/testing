package com.rentlymeari.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import java.io.OutputStream

object ScreenCapture {

  fun captureBitmap(view: View): Bitmap? {
    var bitmap: Bitmap? = null
    try {
      bitmap =
        Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)


      val canvas = Canvas(bitmap)
      view.draw(canvas)
    } catch (e: Exception) {
      e.printStackTrace()
      Log.e("Error", "Failed to capture bitmap because:" + e.message)
    }

    return bitmap
  }

  fun saveMediaToStorage(bitmap: Bitmap, context: Context) {

    val filename = "${System.currentTimeMillis()}.jpg"

    var fos: OutputStream? = null

    context.contentResolver?.also { resolver ->

      val contentValues = ContentValues().apply {

        // Putting file information in content values
        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
      }

      val imageUri: Uri? =
        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
      fos = imageUri?.let { resolver.openOutputStream(it) }
    }


    fos?.use {
      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
      Toast.makeText(context, "Screenshot Captured", Toast.LENGTH_SHORT).show()
    }
  }
}
