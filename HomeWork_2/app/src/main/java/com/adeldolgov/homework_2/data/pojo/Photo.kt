package com.adeldolgov.homework_2.data.pojo

class Photo(val sizes: Array<String>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Photo

        if (!sizes.contentEquals(other.sizes)) return false

        return true
    }

    override fun hashCode(): Int {
        return sizes.contentHashCode()
    }
}