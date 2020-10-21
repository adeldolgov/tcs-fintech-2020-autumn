package com.adeldolgov.homework_2.util

import android.content.res.Resources
import android.text.format.DateUtils
import androidx.core.view.isVisible
import com.facebook.shimmer.ShimmerFrameLayout
import java.text.SimpleDateFormat
import java.util.*

fun Long.toRelativeDateString(): String {
    val now = System.currentTimeMillis()
    return DateUtils.getRelativeTimeSpanString(this * 1000, now, DateUtils.YEAR_IN_MILLIS,
        DateUtils.FORMAT_SHOW_TIME or DateUtils.FORMAT_SHOW_DATE).toString()
}

fun Long.toRelativeDateStringDay(): String {
    val now = System.currentTimeMillis()
    return DateUtils.getRelativeTimeSpanString(this * 1000, now, DateUtils.DAY_IN_MILLIS).toString()
}

fun Date.compareToExcludeTime(date: Date): Boolean {
    val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    return sdf.format(this) == sdf.format(date)
}

var ShimmerFrameLayout.isShimmering: Boolean
    get() {
        return this.isShimmerStarted
    }
    set(shimmering) {
        if (shimmering) {
            isVisible = true
            startShimmer()
        } else {
            stopShimmer()
            isVisible = false
        }
    }

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Int.sp: Int
    get() = (this * Resources.getSystem().displayMetrics.scaledDensity).toInt()
