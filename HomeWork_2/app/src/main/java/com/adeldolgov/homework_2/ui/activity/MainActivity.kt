package com.adeldolgov.homework_2.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import com.adeldolgov.homework_2.R
import com.adeldolgov.homework_2.data.item.PostItem
import com.adeldolgov.homework_2.data.pojo.toPostItem
import com.adeldolgov.homework_2.data.repository.GroupRepository
import com.adeldolgov.homework_2.data.repository.PostRepository
import com.adeldolgov.homework_2.ui.ItemTouchHelper.LDItemTouchHelperCallback
import com.adeldolgov.homework_2.ui.adapter.PostAdapter
import com.adeldolgov.homework_2.ui.decorator.DateItemDecoration
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val postAdapter = PostAdapter { position -> setLikeToPost(position) }.apply { list = getPosts() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        postRecyclerView.apply {
            adapter = postAdapter
            ItemTouchHelper(LDItemTouchHelperCallback(postAdapter)).attachToRecyclerView(this)
            addItemDecoration(DateItemDecoration(postAdapter))
        }

        swipeRefreshLayout.setOnRefreshListener {
            postAdapter.list = getPosts()
            swipeRefreshLayout.isRefreshing = false
            postRecyclerView.post {
                postRecyclerView.scrollToPosition(0)
            }
        }
    }

    private fun getPosts(): List<PostItem> {
        return PostRepository.getPosts().mapNotNull { post ->
            GroupRepository.getGroupBySourceId(post.sourceId)?.let {
                post.toPostItem(it)
            }
        }
    }

    private fun setLikeToPost(position: Int) {
        postAdapter.onItemLike(position)
    }

}