package com.adeldolgov.feeder.util.extension

import android.content.res.Resources
import java.io.InputStream
import java.io.OutputStream

fun Int.toBoolean(): Boolean = this == 1

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Int.sp: Int
    get() = (this * Resources.getSystem().displayMetrics.scaledDensity).toInt()

fun InputStream.copyBytesTo(target: OutputStream) {
    val buf = ByteArray(2048)
    var length: Int
    while (this.read(buf).also { length = it } > 0) {
        target.write(buf, 0, length)
    }
}