package com.adeldolgov.feeder.util.extension

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*

fun Long.toRelativeDateString(): String {
    val now = System.currentTimeMillis()
    return DateUtils.getRelativeTimeSpanString(this * DateUtils.SECOND_IN_MILLIS, now, DateUtils.YEAR_IN_MILLIS,
        DateUtils.FORMAT_SHOW_TIME or DateUtils.FORMAT_SHOW_DATE).toString()
}

fun Long.toRelativeDateStringDay(): String {
    val now = System.currentTimeMillis()
    return DateUtils.getRelativeTimeSpanString(this * DateUtils.SECOND_IN_MILLIS, now, DateUtils.DAY_IN_MILLIS).toString()
}

fun Date.compareToExcludeTime(date: Date): Boolean {
    val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    return sdf.format(this) == sdf.format(date)
}

