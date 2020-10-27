package com.adeldolgov.feeder.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.adeldolgov.feeder.R
import com.adeldolgov.feeder.util.imageloader.GlideImageLoader
import com.adeldolgov.feeder.util.imageloader.ImageLoader
import kotlinx.android.synthetic.main.activity_details_image.*


class ImageDetailsActivity : AppCompatActivity(R.layout.activity_details_image) {

    companion object {
        private const val IMAGE_ARG = "IMAGE_ARG"

        fun createIntent(context: Context, imageUrl: String): Intent {
            return Intent(context, ImageDetailsActivity::class.java).apply {
                putExtra(IMAGE_ARG, imageUrl)
            }
        }

    }

    private val imageLoader: ImageLoader = GlideImageLoader()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.getStringExtra(IMAGE_ARG)?.let {
            imageLoader.loadPoster(it, detailsImage)
        }
    }

}