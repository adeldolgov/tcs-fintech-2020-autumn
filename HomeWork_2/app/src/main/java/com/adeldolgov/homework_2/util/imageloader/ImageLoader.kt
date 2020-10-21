package com.adeldolgov.homework_2.util.imageloader

import android.widget.ImageView

interface ImageLoader {

    fun loadPoster(url: String, targetView: ImageView)

    fun loadRoundedAvatar(url: String, targetView: ImageView)
}
