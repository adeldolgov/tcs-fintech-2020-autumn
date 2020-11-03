package com.adeldolgov.feeder.util.extension

import android.content.res.Resources

fun Int.toBoolean(): Boolean = this == 1

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Int.sp: Int
    get() = (this * Resources.getSystem().displayMetrics.scaledDensity).toInt()