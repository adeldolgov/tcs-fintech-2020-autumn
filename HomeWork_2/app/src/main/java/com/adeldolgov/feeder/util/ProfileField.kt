package com.adeldolgov.feeder.util

import com.adeldolgov.feeder.ui.item.ProfileItem

sealed class ProfileViewType {
    class Profile(val profileItem: ProfileItem) : ProfileViewType()
    class FieldHeader(val header: String) : ProfileViewType()
    class FieldItem(val primaryValue: String, val secondaryValue: String) : ProfileViewType()
    class TimeFieldWithImageItem(
        val startDate: Int?,
        val endDate: Int?,
        val primaryValue: String,
        val secondaryValue: String,
        val imageUrl: String?
    ) : ProfileViewType()
}