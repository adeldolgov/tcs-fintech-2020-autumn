package com.adeldolgov.homework_2.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.adeldolgov.homework_2.R
import com.adeldolgov.homework_2.util.imageloader.GlideImageLoader
import com.adeldolgov.homework_2.util.imageloader.ImageLoader
import kotlinx.android.synthetic.main.activity_details_image.*


class ImageDetailsActivity : AppCompatActivity(R.layout.activity_details_image) {

    companion object {
        const val IMAGE_ARG = "IMAGE_ARG"
    }

    private val imageLoader: ImageLoader = GlideImageLoader()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.getStringExtra(IMAGE_ARG)?.let {
            imageLoader.loadPoster(it, detailsImage)
        }
    }

}