package com.adeldolgov.feeder.data.mapper

import com.adeldolgov.feeder.data.database.entity.SourceEntity
import com.adeldolgov.feeder.data.pojo.Profile
import com.adeldolgov.feeder.data.pojo.Source

fun Source.toSourceEntity(): SourceEntity =
    SourceEntity(
        id = id,
        type = type,
        name = name,
        photo100 = photo100
    )

fun SourceEntity.toSource(): Source = Source(id, type, name, photo100)

fun Profile.toSource(): Source = Source(id, "post", "$firstName $lastName", photo100)