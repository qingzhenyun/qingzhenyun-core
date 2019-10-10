package com.qingzhenyun.offline.service

import com.qingzhenyun.common.jooq.service.AbstractJooqDslRepository
import com.qingzhenyun.generated.offline.Tables
import com.qingzhenyun.generated.offline.tables.pojos.FakeCopyQueue
import com.qingzhenyun.generated.offline.tables.records.FakeCopyQueueRecord
import org.jooq.Condition
import org.jooq.DSLContext
import org.springframework.stereotype.Service

@Service
class FakeCopyQueueService(dslContext: DSLContext) : AbstractJooqDslRepository<FakeCopyQueueRecord, String, FakeCopyQueue>(dslContext, Tables.FAKE_COPY_QUEUE, FakeCopyQueue::class.java) {
    fun addTask(taskId: String, userId: Long, progress: Int?, status: Int?) {
        val rec = FakeCopyQueueRecord().setTaskId(taskId)
                .setProgress(progress)
                .setUpdateTime(System.currentTimeMillis())
                .setUserId(userId)
                .setStatus(status)
                .setCopied(0)
                .setNeedCopySize(0)
        getDslContext().insertInto(Tables.FAKE_COPY_QUEUE).set(rec).onDuplicateKeyUpdate()
                .set(Tables.FAKE_COPY_QUEUE.UPDATE_TIME, System.currentTimeMillis())
                .set(Tables.FAKE_COPY_QUEUE.PROGRESS, progress)
                .set(Tables.FAKE_COPY_QUEUE.STATUS, status)
                .set(Tables.FAKE_COPY_QUEUE.COPIED, 0)
                .set(Tables.FAKE_COPY_QUEUE.NEED_COPY_SIZE, 0)
                .execute()
    }

    fun finishUploadTask(taskId: String?, status: Int?) {
        getDslContext().update(Tables.FAKE_COPY_QUEUE)
                .set(Tables.FAKE_COPY_QUEUE.STATUS, status)
                .set(Tables.FAKE_COPY_QUEUE.UPDATE_TIME, System.currentTimeMillis())
                .where(Tables.FAKE_COPY_QUEUE.TASK_ID.eq(taskId))
                .execute()
    }

    fun updateSingleTask(taskId: String?, userId: Long, copied: Long, needCopySize: Long): Boolean {
        return getDslContext().update(Tables.FAKE_COPY_QUEUE)
                .set(Tables.FAKE_COPY_QUEUE.UPDATE_TIME, System.currentTimeMillis())
                .set(Tables.FAKE_COPY_QUEUE.USER_ID, userId)
                .set(Tables.FAKE_COPY_QUEUE.COPIED, copied)
                .set(Tables.FAKE_COPY_QUEUE.NEED_COPY_SIZE, needCopySize)
                .where(Tables.FAKE_COPY_QUEUE.TASK_ID.eq(taskId).and(Tables.FAKE_COPY_QUEUE.USER_ID.eq(userId)))
                .execute() > 0
    }

    fun deleteTask(taskId: String?): Boolean {
        return getDslContext().deleteFrom(Tables.FAKE_COPY_QUEUE)
                .where(Tables.FAKE_COPY_QUEUE.TASK_ID.eq(taskId))
                .execute() > 0
    }

    fun finishSingleTask(taskId: String?, userId: Long): Boolean {
        return getDslContext().deleteFrom(Tables.FAKE_COPY_QUEUE)
                .where(Tables.FAKE_COPY_QUEUE.TASK_ID.eq(taskId).and(Tables.FAKE_COPY_QUEUE.USER_ID.eq(userId)))
                .execute() > 0
    }

    fun <T> fetchTask(start: Int, size: Int, status: Int?, taskId: String?, clazz: Class<T>): List<T> {
        val dslContext = getDslContext()
        var condition: Condition? = null
        if (status != null && status > 0) {
            condition = Tables.FAKE_COPY_QUEUE.STATUS.eq(status)
        }
        if (!taskId.isNullOrEmpty()) {
            condition = if (condition != null) {
                condition.and(Tables.FAKE_COPY_QUEUE.TASK_ID.eq(taskId))
            } else {
                Tables.FAKE_COPY_QUEUE.TASK_ID.eq(taskId)
            }
        }
        //val condition = Tables.FAKE_COPY_QUEUE.STATUS
        return this.getList(start, size, clazz, Tables.FAKE_COPY_QUEUE.UPDATE_TIME.asc(), condition, dslContext)
    }
}