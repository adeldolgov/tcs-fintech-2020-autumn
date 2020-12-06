package com.adeldolgov.feeder.util.extension

import android.view.View
import androidx.core.view.isVisible
import com.adeldolgov.feeder.util.OnSingleClickListener
import com.facebook.shimmer.ShimmerFrameLayout

var ShimmerFrameLayout.isShimmering: Boolean
    get() {
        return this.isShimmerStarted
    }
    set(shimmering) {
        isVisible = shimmering
        if (shimmering) startShimmer() else stopShimmer()
    }

fun View.debounceClick(action: (view: View) -> Unit) {
    this.setOnClickListener(object : OnSingleClickListener() {
        override fun onSingleClick(v: View) {
            action(v)
        }
    })
}