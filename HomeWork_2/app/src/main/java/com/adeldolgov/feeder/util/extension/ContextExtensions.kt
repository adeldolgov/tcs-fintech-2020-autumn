package com.adeldolgov.feeder.util.extension

import android.Manifest
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.adeldolgov.feeder.BuildConfig
import com.adeldolgov.feeder.FeederApp
import com.adeldolgov.feeder.R
import com.adeldolgov.feeder.util.error.EmptyDataException
import com.adeldolgov.feeder.util.error.NoNetworkException
import java.io.File
import java.io.FileOutputStream

fun Context.toast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Context.topToast(message: CharSequence, @DrawableRes iconRes: Int) {
    val view = LayoutInflater.from(this).inflate(R.layout.view_custom_toast, null)
    view.findViewById<ImageView>(R.id.toastIcon).setImageResource(iconRes)
    view.findViewById<TextView>(R.id.toastText).text = message
    val toast = Toast(this)
    toast.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 0)
    toast.duration = Toast.LENGTH_LONG
    toast.view = view
    toast.show()
}

fun Context.initNetworkReceiver(): BroadcastReceiver {
    return object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            intent?.getBooleanExtra(FeederApp.NETWORK_AVAILABILITY_EXTRA, false)?.let {
                if (!it) topToast(getString(R.string.no_internet_connection), R.drawable.ic_wifi_off)
            }
        }
    }
}

fun Context.registerLocalReceiver(broadcastReceiver: BroadcastReceiver, intentFilter: IntentFilter) {
    val localBroadcastManager = LocalBroadcastManager.getInstance(this)
    localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter)
}

fun Context.unregisterLocalReceiver(broadcastReceiver: BroadcastReceiver) {
    val localBroadcastManager = LocalBroadcastManager.getInstance(this)
    localBroadcastManager.unregisterReceiver(broadcastReceiver)
}

fun Context.sharePhotoFile(shareText: String, subjectText: String, uri: Uri) {
    val sharingIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        putExtra(Intent.EXTRA_SUBJECT, subjectText)
        putExtra(Intent.EXTRA_TEXT, shareText)
        putExtra(Intent.EXTRA_STREAM, uri)
    }
    startActivity(
        Intent.createChooser(sharingIntent, subjectText)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )
}

fun Context.sharePhotoFile(shareText: String, uri: Uri) {
    val sharingIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        putExtra(Intent.EXTRA_STREAM, uri)
    }
    startActivity(
        Intent.createChooser(sharingIntent, shareText)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )
}

fun Context.shareTextMessage(shareText: String, subjectText: String) {
    val sharingIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
        putExtra(Intent.EXTRA_SUBJECT, subjectText)
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
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

fun Context.saveImageToCache(contentUri: Uri): File {
    val imagePath = File(cacheDir, "images/uploaded")
    val fileName = "latest_upload.jpg"
    val newFile = File(imagePath, fileName)
    if (!imagePath.exists()) imagePath.mkdirs()
    this.contentResolver.openInputStream(contentUri)?.use { stream ->
        FileOutputStream(newFile, false).use {
            stream.copyBytesTo(it)
        }
    }

    return newFile
}

fun Context.saveImageFileToPictures(file: File): Uri? {
    val values = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, file.name)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        put(MediaStore.MediaColumns.SIZE, file.length())
        put(MediaStore.MediaColumns.DATE_ADDED, file.lastModified().toRelativeDateString())
    }
    val uri: Uri?

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val relativeLocation = Environment.DIRECTORY_PICTURES + File.separator + getString(R.string.app_name)
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
    return applicationContext as FeederApp
}

fun Context.parseErrorToLocalizedText(error: Throwable): String {
    return when (error) {
        is NoNetworkException -> getString(R.string.no_internet_connection)
        is EmptyDataException -> getString(R.string.error_no_data)
        else -> error.message ?: getString(R.string.error_null)
    }
}

fun Context.writeStoragePermissionGranted(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
}

fun Activity.requestStoragePermission(requestCode: Int) {
    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
        toast(getString(R.string.permission_storage))
    }
    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), requestCode)
}