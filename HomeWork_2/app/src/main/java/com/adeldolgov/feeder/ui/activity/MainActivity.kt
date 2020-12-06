package com.adeldolgov.feeder.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.adeldolgov.feeder.R
import com.adeldolgov.feeder.ui.fragment.NewsFeedFragment
import com.adeldolgov.feeder.ui.fragment.ProfileFragment
import com.adeldolgov.feeder.util.extension.feederApp
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseMvpActivity(R.layout.activity_main), NewsFeedFragment.OnPostLikeListener {

    companion object {
        private const val IS_FAVORITE_ENABLED = "IS_FAVORITE_ENABLED"
        fun createIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        feederApp().addNewsFeedComponent()
        feederApp().addPostDetailComponent()
        mainBottomNavigation.setOnNavigationItemSelectedListener {
            lateinit var tag: String
            val fragment: Fragment? = when (it.itemId) {
                R.id.news -> {
                    feederApp().clearProfileComponent()
                    tag = NewsFeedFragment::class.java.name.plus(":true")
                    findFragmentByTag(tag) ?: NewsFeedFragment.newInstance(false)
                }
                R.id.favorites -> {
                    feederApp().clearProfileComponent()
                    tag = NewsFeedFragment::class.java.name.plus(":false")
                    findFragmentByTag(tag) ?: NewsFeedFragment.newInstance(true)
                }
                R.id.profile -> {
                    feederApp().addProfileComponent()
                    tag = ProfileFragment::class.java.name
                    findFragmentByTag(tag) ?: ProfileFragment.newInstance()
                }
                else -> null
            }
            fragment?.let { commitFragment(fragment, tag) }
            fragment != null
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
        if (!visible && mainBottomNavigation.selectedItemId == R.id.favorites) {
            mainBottomNavigation.selectedItemId = R.id.news
        }
    }

    private fun setBottomTabByTag(tag: String) {
        when(tag) {
            NewsFeedFragment::class.java.name.plus(":true") -> mainBottomNavigation.menu.findItem(R.id.news).isChecked = true
            NewsFeedFragment::class.java.name.plus(":false") -> mainBottomNavigation.menu.findItem(R.id.favorites).isChecked = true
            ProfileFragment::class.java.name -> mainBottomNavigation.menu.findItem(R.id.profile).isChecked = true
        }
    }

    private fun commitFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
            .addToBackStack(tag)
            .replace(R.id.fragmentContainerView, fragment, tag)
            .commit()
    }

    private fun findFragmentByTag(tag: String): Fragment? {
        return supportFragmentManager.findFragmentByTag(tag)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(IS_FAVORITE_ENABLED, mainBottomNavigation.menu.findItem(R.id.favorites).isVisible)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mainBottomNavigation.menu.findItem(R.id.favorites).isVisible =
            savedInstanceState.getBoolean(IS_FAVORITE_ENABLED, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        feederApp().clearNewsFeedComponent()
        feederApp().clearPostDetailComponent()
    }
}