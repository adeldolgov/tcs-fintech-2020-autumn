package com.adeldolgov.homework_2.data.repository

import com.adeldolgov.homework_2.api.DataGenerator
import com.adeldolgov.homework_2.data.pojo.Source

object GroupRepository {
    fun getGroups(): Array<Source> {
        return DataGenerator().getGroups()
    }
    fun getGroupBySourceId(sourceId: Long): Source? {
        return DataGenerator().getGroups().find {
            it.id == sourceId
        }
    }
}