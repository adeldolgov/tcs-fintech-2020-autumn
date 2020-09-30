package com.adeldolgov.homework_2.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.adeldolgov.homework_2.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_social_post.view.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fillExampleSocialPost()
    }

    private fun fillExampleSocialPost() {
        socialPostExample.postOwnerText.text = getString(R.string.owner_tinkoff)
        socialPostExample.postTimeText.text = getString(R.string.time_ago)
        socialPostExample.postContentText.text = getString(R.string.post_content)
        socialPostExample.postLikeCountText.text = getString(R.string.likes_count)
        socialPostExample.postCommentCountText.text = getString(R.string.comments_count)
        socialPostExample.postShareCountText.text = getString(R.string.sharing_count)

        Glide.with(this)
            .load(ContextCompat.getDrawable(this, R.drawable.ic_tinkoff))
            .circleCrop()
            .into(socialPostExample.postOwnerImage)
        Glide.with(this)
            .load(ContextCompat.getDrawable(this, R.drawable.ic_post_example))
            .centerCrop()
            .into(socialPostExample.postContentImage)
    }
}