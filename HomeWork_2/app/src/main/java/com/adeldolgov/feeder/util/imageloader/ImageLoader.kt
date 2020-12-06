package com.adeldolgov.feeder.util.imageloader

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import java.io.File

interface ImageLoader {

    fun loadPoster(url: String, targetView: ImageView)

    fun loadPoster(url: String, targetView: ImageView, onImageLoadListener: OnImageLoadListener)

    fun loadRoundedAvatar(url: String, targetView: ImageView)

    fun loadAvatar(url: String, targetView: ImageView)

    fun getFileFromUrl(url: String, context: Context): File

    fun loadPhotoFromFile(file: File, targetView: ImageView)

    interface OnImageLoadListener {

        fun onSuccess(bitmap: Bitmap?)

        fun onError(exception: Exception?)
    }
}
