package com.adeldolgov.homework_2.data.repository

import com.adeldolgov.homework_2.api.DataGenerator
import com.adeldolgov.homework_2.data.pojo.Source

class GroupRepository {
    fun getGroups(): List<Source> {
        return DataGenerator().getGroups()
    }
    fun getGroupBySourceId(sourceId: Long): Source? {
        return DataGenerator().getGroups().find {
            it.id == sourceId
        }
    }
}