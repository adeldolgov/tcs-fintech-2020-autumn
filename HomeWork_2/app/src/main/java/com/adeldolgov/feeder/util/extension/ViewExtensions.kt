package com.adeldolgov.feeder.util.extension

import androidx.core.view.isVisible
import com.facebook.shimmer.ShimmerFrameLayout

var ShimmerFrameLayout.isShimmering: Boolean
    get() {
        return this.isShimmerStarted
    }
    set(shimmering) {
        isVisible = shimmering
        if (shimmering) startShimmer() else stopShimmer()
    }

