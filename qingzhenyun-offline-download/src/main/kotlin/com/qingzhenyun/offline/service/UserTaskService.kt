package com.qingzhenyun.offline.service

import com.qingzhenyun.common.entity.RecordPage
import com.qingzhenyun.common.entity.TrustedPath
import com.qingzhenyun.common.jooq.service.AbstractJooqDslRepository
import com.qingzhenyun.generated.offline.Tables
import com.qingzhenyun.generated.offline.tables.pojos.UserTask
import com.qingzhenyun.generated.offline.tables.records.UserTaskRecord
import org.jooq.DSLContext
import org.jooq.SortField
import org.springframework.stereotype.Service

@Service
class UserTaskService(dslContext: DSLContext) : AbstractJooqDslRepository<UserTaskRecord, String, UserTask>(dslContext, Tables.USER_TASK, UserTask::class.java) {
    @Suppress("UNUSED")
    override fun getDslContext(): DSLContext {
        throw RuntimeException("Not single table")
    }

    private fun getDslContext(userId: Long?): DSLContext {
        if (userId == null) {
            return super.getDslContext()
        }
        return super.getDslContext()
    }


    fun updateUserTask(taskId: String?, userId: Long, copied: Long, copiedFile: String?, filePath: String?, statusX: Int? = null): Boolean {
        val dslContext = getDslContext(userId)
        var step = dslContext.update(Tables.USER_TASK).set(Tables.USER_TASK.COPIED, copied)
        if (!copiedFile.isNullOrEmpty()) {
            step = step.set(Tables.USER_TASK.COPIED_FILE, copiedFile)
        }
        if (!filePath.isNullOrEmpty()) {
            step = step.set(Tables.USER_TASK.FILE_PATH, TrustedPath(filePath).path)
        }
        if (statusX != null) {
            step = step.set(Tables.USER_TASK.STATUS, statusX)
        }

        return step.where(Tables.USER_TASK.TASK_ID.eq(taskId).and(Tables.USER_TASK.USER_ID.eq(userId))).execute() > 0
    }

    fun finishUserTask(taskId: String?, userId: Long, status: Int? = null): Boolean {
        val dslContext = getDslContext(userId)
        return dslContext.update(Tables.USER_TASK).set(Tables.USER_TASK.STATUS, status)
                .where(Tables.USER_TASK.TASK_ID.eq(taskId).and(Tables.USER_TASK.USER_ID.eq(userId))).execute() > 0
    }

    fun <T> listOfflineTask(userId: Long, start: Int, size: Int, order: Int, clazz: Class<T>): List<T> {
        val dslContext = getDslContext(userId)
        val condition = Tables.USER_TASK.USER_ID.eq(userId)

        var orderByCondition: SortField<*> = Tables.USER_TASK.CREATE_TIME.desc()
        if (order == 1) {
            orderByCondition = Tables.USER_TASK.CREATE_TIME.asc()
        }
        return getList(start, size, clazz, orderByCondition, condition, dslContext)
    }

    fun <T> listOfflineTaskPage(userId: Long, page: Int, pageSize: Int, order: Int, clazz: Class<T>): RecordPage<T> {
        val dslContext = getDslContext(userId)
        val condition = Tables.USER_TASK.USER_ID.eq(userId)

        var orderByCondition: SortField<*> = Tables.USER_TASK.CREATE_TIME.desc()
        if (order == 1) {
            orderByCondition = Tables.USER_TASK.CREATE_TIME.asc()
        }
        return getPage(page, pageSize, clazz, orderByCondition, condition, dslContext)
    }

    fun addUserTask(taskId: String?, userId: Long, copyFile: String?, savePath: String?): UserTaskRecord {
        val dslContext = getDslContext(userId)
        val trust = TrustedPath(savePath).path
        val rec = UserTaskRecord()
                .setTaskId(taskId)
                .setUserId(userId)
                .setCopyFile(copyFile)
                .setSavePath(trust)
                .setStatus(0)
                .setCopied(0)
                .setFilePath("")
                .setCopiedFile("{}")
                .setCreateTime(System.currentTimeMillis())
        dslContext.insertInto(Tables.USER_TASK).set(rec).onDuplicateKeyUpdate()
                .set(Tables.USER_TASK.COPY_FILE, copyFile)
                .set(Tables.USER_TASK.COPIED_FILE, "{}")
                .set(Tables.USER_TASK.FILE_PATH, "")
                .set(Tables.USER_TASK.SAVE_PATH, trust)
                .set(Tables.USER_TASK.COPIED, 0)
                .set(Tables.USER_TASK.STATUS, 0)
                .set(Tables.USER_TASK.CREATE_TIME, System.currentTimeMillis())
                .execute()
        return rec
    }

    fun get(taskId: String?, userId: Long): UserTaskRecord? {
        val dslContext = getDslContext(userId)
        return get(Tables.USER_TASK.TASK_ID.eq(taskId).and(Tables.USER_TASK.USER_ID.eq(userId)), dslContext)
    }
}