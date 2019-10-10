package com.qingzhenyun.common.entity

import java.io.Serializable
import java.util.*
import java.util.function.Consumer

class RecordPage<T> : Serializable, Iterable<T> {

    var page = 1

    var pageSize = 20
        private set

    // 总页面数
    var totalCount: Int = 0
        set(totalCount) {
            field = totalCount
            totalPage = Math
                    .ceil(1.0 * this.totalCount / this.pageSize).toInt()

            if (totalPage == 0) {
                totalPage = 1
            }
            if (page > totalPage) {
                page = totalPage
            }
        }

    var totalPage = 0
        private set

    var list: List<T>? = null

    val start: Int
        get() = (this.page - 1) * this.pageSize

    constructor(page: Int?, pageSize: Int?) {
        if (page != null && page > 0) {
            this.page = page
        } else {
            this.page = 1
        }
        this.pageSize = if (pageSize == null) 20 else if (pageSize > 9999) 9999 else if (pageSize < 1) 1 else pageSize
    }

    constructor(page: Int) {
        if (page > 0) {
            this.page = page
        }
    }

    constructor()

    fun setPageSize(pageSize: Int?) {
        this.pageSize = if (pageSize == null) 20 else if (pageSize > 9999) 9999 else if (pageSize < 1) 1 else pageSize
    }


    override fun iterator(): Iterator<T> {
        return list!!.iterator()
    }

    override fun forEach(action: Consumer<in T>?) {
        list!!.forEach(action)
    }

    override fun spliterator(): Spliterator<T> {
        return list!!.spliterator()
    }

}
