package com.adeldolgov.feeder.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.adeldolgov.feeder.R
import com.adeldolgov.feeder.ui.fragment.NewsFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(R.layout.activity_main), NewsFragment.OnPostLikeListener {

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.news -> {
                    commitFragment(NewsFragment.newInstance(false), NewsFragment::class.java.name.plus(":true"))
                    true
                }
                R.id.favorites -> {
                    commitFragment(NewsFragment.newInstance(true), NewsFragment::class.java.name.plus(":false"))
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
            NewsFragment::class.java.name.plus(":true") -> mainBottomNavigation.menu.findItem(R.id.news).isChecked = true
            NewsFragment::class.java.name.plus(":false") -> mainBottomNavigation.menu.findItem(R.id.favorites).isChecked = true
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