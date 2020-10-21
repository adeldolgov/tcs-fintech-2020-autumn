package com.adeldolgov.homework_2.data.repository

import com.adeldolgov.homework_2.api.DataGenerator
import com.adeldolgov.homework_2.data.pojo.Source
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class GroupRepository {
    fun getGroups(): Single<List<Source>> {
        return DataGenerator().getGroups().subscribeOn(Schedulers.io())
    }
    /*fun getGroupBySourceId(sourceId: Long): Source? {
        return DataGenerator().getGroups().find {
            it.id == sourceId
        }
    }*/
}