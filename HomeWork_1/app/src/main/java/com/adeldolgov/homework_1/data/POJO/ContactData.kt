package com.adeldolgov.homework_1.data.POJO

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ContactData(
    val id: Int,
    val displayName: String,
    val phoneNumber: String?
) : Parcelable