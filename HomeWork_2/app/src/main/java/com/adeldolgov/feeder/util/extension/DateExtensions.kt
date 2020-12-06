package com.adeldolgov.feeder.util.extension

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun Long.toRelativeDateString(): String {
    val now = System.currentTimeMillis()
    return DateUtils.getRelativeTimeSpanString(this * DateUtils.SECOND_IN_MILLIS, now, DateUtils.YEAR_IN_MILLIS,
        DateUtils.FORMAT_SHOW_TIME or DateUtils.FORMAT_SHOW_DATE).toString()
}

fun Long.toRelativeDateStringDay(): String {
    val now = System.currentTimeMillis()
    return DateUtils.getRelativeTimeSpanString(this * DateUtils.SECOND_IN_MILLIS, now, DateUtils.DAY_IN_MILLIS).toString()
}

fun Long.toRelativeDateStringMinutes(): String {
    val now = System.currentTimeMillis()
    return DateUtils.getRelativeTimeSpanString(
        this * DateUtils.SECOND_IN_MILLIS, now, DateUtils.MINUTE_IN_MILLIS,
        DateUtils.FORMAT_ABBREV_RELATIVE).toString()
}

fun Long.toMinutes(): Int {
    return TimeUnit.MILLISECONDS.toMinutes(this).toInt()
}



fun String.currentDateYears(): Int {
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val date = sdf.parse(this)
    val years = (Date(System.currentTimeMillis()).time - date.time) / 86400000 / 365
    return years.toInt()
}

fun Date.compareToExcludeTime(date: Date): Boolean {
    val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    return sdf.format(this) == sdf.format(date)
}

