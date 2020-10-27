package com.adeldolgov.feeder.util.imageloader

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target

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

    override fun loadRoundedAvatar(url: String, targetView: ImageView) {
        Glide.with(targetView)
            .load(url)
            .circleCrop()
            .into(targetView)
    }
}
