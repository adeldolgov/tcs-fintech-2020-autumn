package com.adeldolgov.homework_2.ui.activity

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Pair
import androidx.appcompat.app.AppCompatActivity
import com.adeldolgov.homework_2.R
import com.adeldolgov.homework_2.data.item.PostItem
import com.adeldolgov.homework_2.util.imageloader.GlideImageLoader
import com.adeldolgov.homework_2.util.imageloader.ImageLoader
import com.adeldolgov.homework_2.util.toRelativeDateString
import kotlinx.android.synthetic.main.view_social_post_details.*

class PostDetailsActivity : AppCompatActivity() {

    companion object {
        const val POST_EXTRA = "post_extra"
    }

    private val imageLoader: ImageLoader = GlideImageLoader()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_details)
        val postItem = intent.getParcelableExtra<PostItem>(POST_EXTRA)
        if (postItem != null) setupContent(postItem)
    }

    private fun setupContent(post: PostItem) {
        post.attachments?.get(0)?.photo?.sizes?.get(0)?.let { url ->
            imageLoader.loadPoster(url, postContentImage)
            postContentImage.setOnClickListener {
                startImageDetailsActivity(url)
            }
        }
        imageLoader.loadRoundedAvatar(post.sourceImage, postOwnerImage)

        postContentText.text = post.text
        postOwnerText.text = post.sourceName

        if (post.isFavorite) {
            postLikeBtn.setImageDrawable(getDrawable(R.drawable.ic_favorite))
        } else {
            postLikeBtn.setImageDrawable(getDrawable(R.drawable.ic_favorite_outline))
        }
        postTimeText.text = post.date.toRelativeDateString()
        postLikeCountText.text = post.likes.toString()
        postShareCountText.text = post.reposts.toString()
    }

    private fun startImageDetailsActivity(url: String) {
        val options = ActivityOptions.makeSceneTransitionAnimation(
            this,
            Pair.create(postContentImage, postContentImage.transitionName)
        )
        startActivity(
            Intent(this, ImageDetailsActivity::class.java).putExtra(ImageDetailsActivity.IMAGE_ARG, url),
            options.toBundle()
        )
    }
}