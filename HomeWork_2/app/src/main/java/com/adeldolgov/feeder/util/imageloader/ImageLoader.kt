package com.adeldolgov.feeder.util.imageloader

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import java.io.File

interface ImageLoader {

    fun loadPoster(url: String, targetView: ImageView)

    fun loadPoster(url: String, targetView: ImageView, onImageLoadListener: OnImageLoadListener)

    fun loadRoundedAvatar(url: String, targetView: ImageView)

    fun getFileFromUrl(url: String, context: Context): File

    interface OnImageLoadListener {

        fun onSuccess(bitmap: Bitmap?)

        fun onError(exception: Exception?)
    }
}
