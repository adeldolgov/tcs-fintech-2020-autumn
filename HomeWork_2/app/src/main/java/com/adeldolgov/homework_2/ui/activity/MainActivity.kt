package com.adeldolgov.homework_2.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.adeldolgov.homework_2.R
import com.adeldolgov.homework_2.domain.viewmodel.PostsViewModel
import com.adeldolgov.homework_2.ui.fragment.FavoriteNewsFragment
import com.adeldolgov.homework_2.ui.fragment.NewsFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(R.layout.activity_main), NewsFragment.OnPostLikeListener, FavoriteNewsFragment.OnPostLikeListener {

    private var postViewModel: PostsViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postViewModel = ViewModelProvider(this).get(PostsViewModel::class.java)
        mainBottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.news -> {
                    commitFragment(NewsFragment.newInstance(), NewsFragment.TAG)
                    true
                }
                R.id.favorites -> {
                    commitFragment(FavoriteNewsFragment.newInstance(), FavoriteNewsFragment.TAG)
                    true
                }
                else -> false
            }
        }

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                finish()
            } else {
                val backEntry = supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1)
                backEntry.name?.let {
                    setBottomTabByTag(it)
                }
            }
        }

        if (savedInstanceState == null) mainBottomNavigation.selectedItemId = R.id.news
    }

    override fun onFavoriteVisibility(visible: Boolean) {
        mainBottomNavigation.menu.findItem(R.id.favorites).isVisible = visible
        if (!visible && mainBottomNavigation.selectedItemId != R.id.news) {
            mainBottomNavigation.selectedItemId = R.id.news
        }
    }

    private fun setBottomTabByTag(tag: String) {
        when(tag) {
            NewsFragment.TAG -> mainBottomNavigation.menu.findItem(R.id.news).isChecked = true
            FavoriteNewsFragment.TAG -> mainBottomNavigation.menu.findItem(R.id.favorites).isChecked = true
        }
    }

    private fun commitFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.fade_out
            )
            .addToBackStack(tag)
            .replace(R.id.fragmentContainerView, fragment, tag)
            .commit()
    }


}