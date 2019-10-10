package com.qingzhenyun.common.jooq.service

import com.qingzhenyun.common.entity.RecordPage
import org.jooq.*
import org.slf4j.LoggerFactory

abstract class AbstractJooqDslRepository<T : Record, PRIMARY_KEY, out POJO>(private val dslContext: DSLContext, private val table: Table<T>, private val pojoClass: Class<POJO>) {

    operator fun get(id: PRIMARY_KEY, otherDslContext: DSLContext? = null, table: Table<T>? = null): T? {
        val tTableField = primaryKeyTable ?: return null
        val cTable = table ?: this.table
        return if (otherDslContext != null) {
            otherDslContext.fetchOne(cTable, tTableField.eq(id))
        } else {
            dslContext.fetchOne(cTable, tTableField.eq(id))
        }
    }

    operator fun get(condition: Condition, otherDslContext: DSLContext? = null, table: Table<T>? = null): T? {
        val cTable = table ?: this.table
        return if (otherDslContext != null) {
            otherDslContext.fetchOne(cTable, condition)
        } else {
            dslContext.fetchOne(cTable, condition)
        }
    }

    @Suppress("UNUSED")
    open fun getDslContext(): DSLContext {
        return dslContext
    }

    private val primaryKeyTable: TableField<T, PRIMARY_KEY>?
        get() {
            val fields = table.primaryKey.fields
            return if (fields.size > 0) {
                @Suppress("UNCHECKED_CAST")
                fields[0] as TableField<T, PRIMARY_KEY>
            } else null
        }

    /*
     *
     * ------------------------------- GET -----------------------------------
     */
    @Suppress("UNUSED")
    fun <E> getPojo(id: PRIMARY_KEY, type: Class<out E>, otherDslContext: DSLContext? = null, table: Table<T>? = null): E? {
        val value = get(id, otherDslContext, table) ?: return null
        return value.into(type)
    }

    @Suppress("UNUSED")
    fun getPojo(id: PRIMARY_KEY, otherDslContext: DSLContext? = null, table: Table<T>? = null): POJO? {
        val value = get(id, otherDslContext, table) ?: return null
        return value.into(pojoClass)
    }

    @Suppress("UNUSED")
    fun <E> getPojo(condition: Condition, type: Class<out E>, otherDslContext: DSLContext? = null, table: Table<T>? = null): E? {
        val value = get(condition, otherDslContext, table) ?: return null
        return value.into(type)
    }

    @Suppress("UNUSED")
    fun getPojo(condition: Condition, otherDslContext: DSLContext? = null, table: Table<T>? = null): POJO? {
        val value = get(condition, otherDslContext, table) ?: return null
        return value.into(pojoClass)
    }

    /*
     *
     * ------------------------------- DELETE-----------------------------------
     */

    @Suppress("UNUSED")
    fun delete(id: PRIMARY_KEY, otherDslContext: DSLContext? = null, table: Table<T>? = null): T? {
        val data = get(id, otherDslContext, table)
        return if (data == null) {
            null
        } else {
            if (data is UpdatableRecord<*>) {
                data.delete()
            } else {
                LOGGER.warn("Delete none updatable record, {}", data.javaClass.toString())
            }
            data
        }
    }

    @Suppress("UNUSED")
    fun delete(ids: List<PRIMARY_KEY>, otherDslContext: DSLContext? = null, table: Table<T>? = null): Int {
        val primaryKey = primaryKeyTable
        val cTable = table ?: this.table
        if (primaryKey == null) {
            LOGGER.warn("Trying delete none primary key table")
            return 0
        }
        val context = otherDslContext ?: dslContext
        return context.delete(cTable).where(primaryKey.`in`(ids)).execute()
    }

    @Suppress("UNUSED")
    fun delete(ids: Array<PRIMARY_KEY>, otherDslContext: DSLContext? = null, table: Table<T>? = null): Int {
        val primaryKey = primaryKeyTable
        val cTable = table ?: this.table
        if (primaryKey == null) {
            LOGGER.warn("Table {} has no primary key", cTable.name)
            return 0
        }
        val context = otherDslContext ?: dslContext
        return context.delete(cTable).where(primaryKey.`in`(*ids)).execute()
    }

    @Suppress("UNUSED")
    fun delete(condition: Condition, otherDslContext: DSLContext? = null, table: Table<T>? = null): Int {
        val primaryKey = primaryKeyTable
        val cTable = table ?: this.table
        if (primaryKey == null) {
            LOGGER.warn("Table {} has no primary key", cTable.name)
            return 0
        }
        val context = otherDslContext ?: dslContext
        return context.delete(cTable).where(condition).execute()
    }

    @Suppress("UNUSED")
    fun deleteOne(condition: Condition, otherDslContext: DSLContext? = null, table: Table<T>? = null): T? {
        val data = get(condition, otherDslContext, table)
        return if (data == null) {
            null
        } else {
            if (data is UpdatableRecord<*>) {
                data.delete()
            } else {
                LOGGER.warn("Delete none updatable record, {}", data.javaClass.toString())
            }
            data
        }
    }

    @Suppress("UNUSED")
    fun <E> deleteOne(condition: Condition, type: Class<out E>, otherDslContext: DSLContext? = null): E? {
        return deleteOne(condition, otherDslContext)?.into(type)
    }


    /*
     *
     * ------------------------------- CREATE -----------------------------------
     */

    @Suppress("UNUSED")
    fun create(otherDslContext: DSLContext? = null, table: Table<T>? = null): T {
        val context = otherDslContext ?: dslContext
        return context.newRecord(table ?: this.table)
    }

    @Suppress("UNUSED")
    fun create(from: Any? = null, otherDslContext: DSLContext? = null, table: Table<T>? = null): T {
        val context = otherDslContext ?: dslContext
        val record = context.newRecord(table ?: this.table)
        if (from != null) {
            record.from(from)
        }
        if (record is UpdatableRecord<*>) {
            record.store()
        }
        return record
    }

    @Suppress("UNUSED")
    fun <E> create(from: Any? = null, type: Class<out E>, otherDslContext: DSLContext? = null): E {
        return create(from, otherDslContext).into(type)
    }


    /*
     *
     * ------------------------------- LIST -----------------------------------
     */


    //vararg not use
    @Suppress("UNUSED")
    fun <A> getList(start: Int, size: Int, type: Class<out A>, orderBy: SortField<*>? = null, condition: Condition? = null, otherDslContext: DSLContext? = null, table: Table<T>? = null): List<A> {
        return getRecordList(start, size, orderBy, condition, otherDslContext, table).into(type)
    }

    @Suppress("UNUSED")
    fun <A> getList(start: Int, size: Int, type: Class<out A>, orderBy: List<SortField<*>>, condition: Condition? = null, otherDslContext: DSLContext? = null, table: Table<T>? = null): List<A> {
        return getRecordList(start, size, orderBy, condition, otherDslContext, table).into(type)
    }

    fun getRecordList(start: Int, size: Int, orderBy: SortField<*>? = null, condition: Condition? = null, otherDslContext: DSLContext? = null, table: Table<T>? = null): Result<T> {
        val context = otherDslContext ?: dslContext
        @Suppress("UNCHECKED_CAST")
        val from = context.select().from(table ?: this.table) as SelectJoinStep<T>
        val where = if (condition == null) from else from.where(condition)
        //}
        var before: SelectLimitStep<T> = where
        if (orderBy != null) {
            before = where.orderBy(orderBy)
        }
        return execList(before, start, size)
    }

    fun getRecordList(start: Int, size: Int, orderBy: List<SortField<*>>, condition: Condition? = null, otherDslContext: DSLContext? = null, table: Table<T>? = null): Result<T> {
        val context = otherDslContext ?: dslContext
        @Suppress("UNCHECKED_CAST")
        val from = context.select().from(table ?: this.table) as SelectJoinStep<T>
        val where = if (condition == null) from else from.where(condition)
        //}
        var before: SelectLimitStep<T> = where
        if (orderBy.isNotEmpty()) {
            before = where.orderBy(orderBy)
        }
        return execList(before, start, size)
    }

    private fun execList(before: SelectLimitStep<T>, start: Int, size: Int): Result<T> {
        if (start > 0) {
            val offset = before.offset(start)
            return if (size > 0) {
                offset.limit(size).fetch()
            } else offset.fetch()
        }
        return if (size > 0) {
            before.limit(size).fetch()
        } else before.fetch()
    }

    /*
     *
     * ------------------------------- PAGE -----------------------------------
     */
    @Suppress("UNUSED")
    fun getRecordPage(page: Int?, pageSize: Int?, orderBy: List<SortField<*>>, condition: Condition? = null, otherDslContext: DSLContext? = null, table: Table<T>? = null): RecordPage<T> {
        val context = otherDslContext ?: dslContext
        var checkedPage = page
        var checkedPageSize = pageSize
        //var checkedOrderBy = orderBy
        if (checkedPage == null) {
            checkedPage = 1
        }
        if (checkedPageSize == null) {
            checkedPageSize = 20
        }
        @Suppress("UNCHECKED_CAST")
        val from = context.select().from(table ?: this.table) as SelectJoinStep<T>
        val where = if (condition == null) from else from.where(condition)
        val count = context.fetchCount(where)
        val fetch = if (orderBy.isNotEmpty()) {
            where.orderBy(orderBy).offset(calcStart(checkedPage, checkedPageSize)).limit(checkedPageSize).fetch()
        } else {
            where.offset(calcStart(checkedPage, checkedPageSize)).limit(checkedPageSize).fetch()
        }
        // Access Data
        val result = RecordPage<T>(checkedPage, checkedPageSize)
        result.totalCount = count
        // val into = fetch.into(type)
        result.list = fetch
        return result
    }


    @Suppress("UNUSED")
    fun <E> getPage(page: Int?, pageSize: Int?, type: Class<out E>, orderBy: SortField<*>?, condition: Condition? = null, otherDslContext: DSLContext? = null, table: Table<T>? = null): RecordPage<E> {
        //val queryResult = getRecordPage(page, pageSize, if (orderBy == null) emptyList() else listOf(orderBy), condition, otherDslContext)

        return getPage(page, pageSize, type, if (orderBy == null) emptyList() else listOf(orderBy), condition, otherDslContext, table)
    }

    @Suppress("UNUSED")
    fun <E> getPage(page: Int?, pageSize: Int?, type: Class<out E>, orderBy: List<SortField<*>>, condition: Condition? = null, otherDslContext: DSLContext? = null, table: Table<T>? = null): RecordPage<E> {
        val queryResult = getRecordPage(page, pageSize, orderBy, condition, otherDslContext, table)
        val resultList = queryResult.list as Result<T>
        val result = RecordPage<E>(queryResult.page, queryResult.pageSize)
        result.totalCount = queryResult.totalCount
        result.list = resultList.into(type)
        return result
    }

    private fun calcStart(page: Int = 1, pageSize: Int): Int {
        var checkedPage = page
        var checkedPageSize = pageSize
        if (checkedPage < 1) {
            checkedPage = 1
        }
        if (checkedPageSize < 1) {
            checkedPageSize = 1
        }
        return (checkedPage - 1) * checkedPageSize
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(this::class.java)
    }
}