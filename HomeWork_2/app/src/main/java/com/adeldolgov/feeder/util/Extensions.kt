package com.adeldolgov.feeder.util

import android.content.Context
import android.content.res.Resources
import android.text.format.DateUtils
import android.widget.Toast
import androidx.core.view.isVisible
import com.adeldolgov.feeder.FeederApp
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
        isVisible = shimmering
        if (shimmering) startShimmer() else stopShimmer()
    }

fun Context.toast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Context.feederApp(): FeederApp {
    return this.applicationContext as FeederApp
}

fun Int.toBoolean(): Boolean {
    return this == 1
}


val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Int.sp: Int
    get() = (this * Resources.getSystem().displayMetrics.scaledDensity).toInt()
