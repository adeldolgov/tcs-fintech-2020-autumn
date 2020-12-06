package com.adeldolgov.feeder.util.imageloader

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.ObjectKey
import java.io.File

class GlideImageLoader : ImageLoader {

    override fun loadPoster(url: String, targetView: ImageView) {
        Glide.with(targetView)
            .load(url)
            .fitCenter()
            .apply(
                RequestOptions()
                    .override(Target.SIZE_ORIGINAL)
                    .format(DecodeFormat.PREFER_ARGB_8888)
            )
            .into(targetView)
    }

    override fun loadPoster(url: String, targetView: ImageView, onImageLoadListener: ImageLoader.OnImageLoadListener) {
        Glide.with(targetView)
            .asBitmap()
            .load(url)
            .fitCenter()
            .apply(
                RequestOptions()
                    .override(Target.SIZE_ORIGINAL)
                    .format(DecodeFormat.PREFER_ARGB_8888)
            )
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(
                    e: GlideException?, model: Any?, target: Target<Bitmap>?,
                    isFirstResource: Boolean): Boolean {
                    onImageLoadListener.onError(e)
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?,
                    isFirstResource: Boolean): Boolean {
                    onImageLoadListener.onSuccess(resource)
                    return false
                }
            })
            .into(targetView)
    }

    override fun loadRoundedAvatar(url: String, targetView: ImageView) {
        Glide.with(targetView)
            .load(url)
            .circleCrop()
            .into(targetView)
    }

    override fun loadAvatar(url: String, targetView: ImageView) {
        Glide.with(targetView)
            .load(url)
            .into(targetView)
    }

    override fun getFileFromUrl(url: String, context: Context): File {
        return Glide.with(context)
            .asFile()
            .load(url)
            .submit()
            .get()
    }

    override fun loadPhotoFromFile(file: File, targetView: ImageView) {
        Glide.with(targetView)
            .load(file)
            .signature(ObjectKey(System.currentTimeMillis()))
            .into(targetView)
    }

}
