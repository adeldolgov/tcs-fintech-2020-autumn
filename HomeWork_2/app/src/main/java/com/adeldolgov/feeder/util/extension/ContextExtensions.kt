package com.adeldolgov.feeder.util.extension

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.adeldolgov.feeder.BuildConfig
import com.adeldolgov.feeder.FeederApp
import com.adeldolgov.feeder.R
import java.io.File
import java.io.FileOutputStream

fun Context.toast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Context.sharePhotoFile(shareText: String, subjectText: String, uri: Uri) {
    val sharingIntent = Intent(Intent.ACTION_SEND)
    sharingIntent.type = "image/png"
    sharingIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, subjectText)
    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareText)
    sharingIntent.putExtra(Intent.EXTRA_STREAM, uri)
    startActivity(
        Intent.createChooser(sharingIntent, subjectText)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )
}

fun Context.sharePhotoFile(shareText: String, uri: Uri) {
    val sharingIntent = Intent(Intent.ACTION_SEND)
    sharingIntent.type = "image/png"
    sharingIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    sharingIntent.putExtra(Intent.EXTRA_STREAM, uri)
    startActivity(
        Intent.createChooser(sharingIntent, shareText)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )
}

fun Context.shareTextMessage(shareText: String, subjectText: String) {
    val sharingIntent = Intent(Intent.ACTION_SEND)
    sharingIntent.type = "text/plain"
    sharingIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, subjectText)
    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareText)
    startActivity(
        Intent.createChooser(sharingIntent, subjectText)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )
}

fun Context.saveImageToCache(bitmap: Bitmap): Uri {
    val imagePath = File(cacheDir, "images/shared")
    val authority = BuildConfig.APPLICATION_ID
    val fileName = "shared_image.jpg"
    val newFile = File(imagePath, fileName)
    val uri: Uri = FileProvider.getUriForFile(this, authority, newFile)
    if (!imagePath.exists()) imagePath.mkdirs()
    FileOutputStream(newFile, false).use {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, it)
    }
    return uri
}

fun Context.saveImageFileToPictures(file: File): Uri? {
    val values = ContentValues()
    values.put(MediaStore.MediaColumns.DISPLAY_NAME, file.name)
    values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
    values.put(MediaStore.MediaColumns.SIZE, file.length())
    values.put(MediaStore.MediaColumns.DATE_ADDED, file.lastModified().toRelativeDateString())

    val uri: Uri?
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val relativeLocation = Environment.DIRECTORY_PICTURES + File.pathSeparator + getString(R.string.app_name)
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation)
        values.put(MediaStore.Images.Media.IS_PENDING, false)
        uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        uri?.let {
            contentResolver.openOutputStream(it).use { stream ->
                stream?.write(file.readBytes())
            }
        }
    } else {
        val storageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            .absolutePath + "//" + getString(R.string.app_name))
        if (!storageDir.exists()) storageDir.mkdirs()
        val newFile = File(storageDir, file.name)
        newFile.outputStream().use {
            it.write(file.readBytes())
        }
        values.put(MediaStore.Images.Media.DATA, newFile.absolutePath)
        uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }

    return uri
}

fun Context.feederApp(): FeederApp {
    return this.applicationContext as FeederApp
}

fun Context.writeStoragePermissionGranted(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
}