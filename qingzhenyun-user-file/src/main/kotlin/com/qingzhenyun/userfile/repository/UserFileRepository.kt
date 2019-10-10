package com.qingzhenyun.userfile.repository

import com.qingzhenyun.common.constants.UserFileConstants
import com.qingzhenyun.common.constants.UserFileOperate
import com.qingzhenyun.common.entity.RecordPage
import com.qingzhenyun.common.entity.TrustedPath
import com.qingzhenyun.common.jooq.service.AbstractJooqDslRepository
import com.qingzhenyun.generated.file.Tables
import com.qingzhenyun.generated.file.tables.pojos.UserFile
import com.qingzhenyun.generated.file.tables.records.UserFileRecord
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.springframework.stereotype.Service

@Service
class UserFileRepository(dslContext: DSLContext) : AbstractJooqDslRepository<UserFileRecord, String, UserFile>(dslContext, Tables.USER_FILE, UserFile::class.java) {
    fun getDslContext(userId: Long, readOnly: Boolean = false): DSLContext {
        if (userId > -1) {
            return super.getDslContext()
        }
        if (readOnly) {
            return super.getDslContext()
        }
        return super.getDslContext()
    }

    fun <T> listDirectoryPage(userId: Long, uuid: String?, type: Int, page: Int, pageSize: Int, orderBy: Int, clazz: Class<T>): RecordPage<T> {
        val dslContext = this.getDslContext(userId)
        val table = getTable(userId)
        var condition = table.USER_ID.eq(userId).and(table.PARENT.eq(uuid))
        if (type > -1) {
            condition = condition.and(table.TYPE.eq(type))
        }

        return this.getPage(page, pageSize, clazz, getOrderBy(userId, orderBy), condition, dslContext, table)
    }

    private fun getOrderBy(userId: Long, orderBy: Int?): List<SortField<*>> {
        val table = getTable(userId)
        val orderByCondition: SortField<*> = table.TYPE.desc()
        return when (orderBy) {
            -2 -> emptyList()
            1 -> listOf(orderByCondition, table.CTIME.asc())
            2 -> listOf(table.NAME.asc())
            3 -> listOf(table.NAME.desc())
            4 -> listOf(table.TYPE.desc(), table.NAME.desc())
            5 -> listOf(table.TYPE.asc(), table.NAME.desc())
            6 -> listOf(table.TYPE.desc(), table.NAME.asc())
            7 -> listOf(table.TYPE.asc(), table.NAME.asc())
            8 -> listOf(table.TYPE.desc(), table.SIZE.desc())
            9 -> listOf(table.TYPE.asc(), table.SIZE.desc())
            10 -> listOf(table.TYPE.desc(), table.SIZE.asc())
            11 -> listOf(table.TYPE.asc(), table.SIZE.asc())
            12 -> listOf(table.SIZE.asc())
            13 -> listOf(table.SIZE.desc())
            14 -> listOf(table.TYPE.desc(), table.CTIME.desc())
            15 -> listOf(table.TYPE.asc(), table.CTIME.desc())
            16 -> listOf(table.TYPE.desc(), table.CTIME.asc())
            17 -> listOf(table.TYPE.asc(), table.CTIME.asc())
            else -> listOf(orderByCondition, table.CTIME.desc())
        }
    }

    fun <T> listDirectory(userId: Long, uuid: String?, type: Int, start: Int, size: Int, orderBy: Int, clazz: Class<T>): List<T> {
        val dslContext = this.getDslContext(userId)
        val table = getTable(userId)
        var condition = table.USER_ID.eq(userId).and(table.PARENT.eq(uuid))
        if (type > -1) {
            condition = condition.and(table.TYPE.eq(type))
        }
        return this.getList(start, size, clazz, getOrderBy(userId, orderBy), condition, dslContext, table)
    }

    fun <T> listDirectory(userId: Long, condition: Condition?, start: Int, size: Int, orderBy: Int, clazz: Class<T>): List<T> {
        val dslContext = this.getDslContext(userId)
        val table = getTable(userId)
        return this.getList(start, size, clazz, getOrderBy(userId, orderBy), condition, dslContext, table)
    }

    fun preOperate(userId: Long, uuid: List<String>, operate: Int): Int {
        if (uuid.isEmpty()) {
            return 0
        }
        val dslContext = getDslContext(userId, false)
        val table = getTable(userId)
        val condition = table.USER_ID.eq(userId).and(table.UUID.`in`(uuid))
        return dslContext.update(table)
                .set(table.LOCKING, true)
                .set(table.MTIME, System.currentTimeMillis())
                .set(table.OP, operate)
                .where(condition).execute()
    }

    fun preOperate(userId: Long, uuid: String?, operate: Int): Int {
        if (uuid.isNullOrEmpty()) {
            return 0
        }
        val dslContext = getDslContext(userId, false)
        val table = getTable(userId)
        val condition = table.USER_ID.eq(userId).and(table.UUID.eq(uuid))
        return dslContext.update(table)
                .set(table.LOCKING, true)
                .set(table.MTIME, System.currentTimeMillis())
                .set(table.OP, operate)
                .where(condition).execute()
    }

    fun unLock(userId: Long, uuid: String?, operate: Int): Int {
        if (uuid.isNullOrEmpty()) {
            return 0
        }
        val dslContext = getDslContext(userId, false)
        val table = getTable(userId)
        val condition = table.USER_ID.eq(userId).and(table.UUID.eq(uuid))
        return dslContext.update(table)
                .set(table.LOCKING, false)
                .set(table.MTIME, System.currentTimeMillis())
                .set(table.OP, operate)
                .where(condition).execute()
    }

    fun get(userId: Long, uuid: String?, otherDslContext: DSLContext? = null, table: com.qingzhenyun.generated.file.tables.UserFile? = null): UserFileRecord? {
        val cTable = table ?: getTable(userId)
        if (uuid.isNullOrEmpty()) {
            return findRoot(userId)
        }
        val condition = cTable.USER_ID.eq(userId).and(cTable.UUID.eq(uuid))
        val dslContext = otherDslContext ?: getDslContext(userId)
        return super.get(condition, dslContext, cTable)
    }

    fun findRoot(userId: Long, otherDslContext: DSLContext? = null, table: com.qingzhenyun.generated.file.tables.UserFile? = null): UserFileRecord {
        val cTable = table ?: getTable(userId)
        val dslContext = otherDslContext ?: getDslContext(userId)
        val trustedPath = TrustedPath("")
        val condition = cTable.USER_ID.eq(userId).and(cTable.UUID.eq(trustedPath.uuid))
        val record = this[condition, dslContext]
        if (record != null) {
            return record
        }
        val newRecord = this.create(dslContext, cTable)
        val current = System.currentTimeMillis()
        newRecord.setUserId(userId).setSize(0)
                .setAtime(current).setMtime(current).setCtime(current)
                .setOp(UserFileOperate.ADD)
                .setName(UserFileConstants.ROOT_NAME).setLocking(false)
                .setPath(trustedPath.path).setUuid(trustedPath.uuid)
                .setParent("")
                .setVersion(0).type = UserFileConstants.DIRECTORY_TYPE
        newRecord.store()
        return newRecord
    }


    fun delete(userId: Long, uuid: String?): Int {
        if (uuid.isNullOrEmpty()) {
            return 0
        }
        val dslContext = getDslContext(userId, false)
        val table = getTable(userId)
        val condition = table.USER_ID.eq(userId).and(table.UUID.eq(uuid))
        return dslContext.deleteFrom(table)
                .where(condition).execute()
    }

    fun getTable(userId: Long): com.qingzhenyun.generated.file.tables.UserFile {
        if (userId > 0) {
            return Tables.USER_FILE
        }
        return Tables.USER_FILE.rename("cross")
    }
}