package com.adeldolgov.feeder.data.mapper

import com.adeldolgov.feeder.data.pojo.Career
import com.adeldolgov.feeder.data.pojo.Location
import com.adeldolgov.feeder.data.pojo.Source
import com.adeldolgov.feeder.ui.item.CareerItem


fun Career.toCareerItem(source: Source?, country: Location?, city: Location?): CareerItem =
    CareerItem(
        countryName = country?.title,
        cityName = city?.title,
        company = company ?: source!!.name,
        from = from,
        until = until,
        groupId = groupId,
        groupPhoto = source?.photo100,
        position = position
    )