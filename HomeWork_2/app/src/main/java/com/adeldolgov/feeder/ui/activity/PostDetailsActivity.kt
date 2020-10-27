package com.adeldolgov.feeder.ui.activity

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Pair
import androidx.appcompat.app.AppCompatActivity
import com.adeldolgov.feeder.R
import com.adeldolgov.feeder.data.item.PostItem
import com.adeldolgov.feeder.util.imageloader.GlideImageLoader
import com.adeldolgov.feeder.util.imageloader.ImageLoader
import kotlinx.android.synthetic.main.activity_post_details.*
import kotlinx.android.synthetic.main.view_social_post_details.*

class PostDetailsActivity : AppCompatActivity() {

    companion object {
        private const val POST_EXTRA = "post_extra"

        fun createIntent(context: Context, postItem: PostItem): Intent {
            return Intent(context, PostDetailsActivity::class.java).apply {
                putExtra(POST_EXTRA, postItem)
            }
        }
    }

    private val imageLoader: ImageLoader = GlideImageLoader()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_details)
        val postItem = intent.getParcelableExtra<PostItem>(POST_EXTRA)
        if (postItem != null) {
            postDetails.setPostDetails(postItem, imageLoader) {
                startImageDetailsActivity(it)
            }
        }
    }


    private fun startImageDetailsActivity(url: String) {
        val options = ActivityOptions.makeSceneTransitionAnimation(
            this,
            Pair.create(postContentImage, postContentImage.transitionName)
        )
        startActivity(
            ImageDetailsActivity.createIntent(this, url),
            options.toBundle()
        )
    }
}