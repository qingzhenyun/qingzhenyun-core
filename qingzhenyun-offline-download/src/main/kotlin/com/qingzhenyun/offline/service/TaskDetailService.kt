package com.qingzhenyun.offline.service

import com.qingzhenyun.common.jooq.service.AbstractJooqDslRepository
import com.qingzhenyun.generated.offline.Tables
import com.qingzhenyun.generated.offline.tables.pojos.TaskDetail
import com.qingzhenyun.generated.offline.tables.records.TaskDetailRecord
import org.jooq.DSLContext
import org.jooq.Query
import org.jooq.impl.DSL
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class TaskDetailService(dslContext: DSLContext) : AbstractJooqDslRepository<TaskDetailRecord, String, TaskDetail>(dslContext, Tables.TASK_DETAIL, TaskDetail::class.java) {

    fun <T> fetchAll(taskId: String?, clazz: Class<T>): List<T> {
        return this.getDslContext(taskId).selectFrom(Tables.TASK_DETAIL).where(Tables.TASK_DETAIL.TASK_ID.eq(taskId)).fetch().into(clazz)
    }

    private fun getDslContext(taskId: String?): DSLContext {
        if (taskId.isNullOrEmpty()) {
            return getDslContext()
        }
        return getDslContext()
    }

    fun updateMetadata(taskId: String?, list: List<Any>): Boolean {
        val dslContext = getDslContext(taskId)
        val queries = mutableListOf<Query>()
        list.forEach { data ->
            val rec = TaskDetailRecord()
            rec.from(data)
            if (rec.order > 0 && !rec.taskId.isNullOrEmpty()) {
                val query = dslContext.insertInto(Tables.TASK_DETAIL).set(rec).onDuplicateKeyUpdate().set(rec)
                queries.add(query)
            }
        }

        logger.info("Update {}:{} meta", list.size, queries.size)
        return dslContext.batch(queries).execute().sum() > 0
    }

    fun updateDetail(taskId: String, order: Int, path: String?, size: Long?, completed: Long?, progress: Int?, storeId: String?): Boolean {
        val dsl = getDslContext(taskId)
        return dsl.transactionResult { configuration ->
            val dslContext = DSL.using(configuration)
            val data = dslContext.fetchOne(Tables.TASK_DETAIL, Tables.TASK_DETAIL.TASK_ID.eq(taskId).and(Tables.TASK_DETAIL.ORDER.eq(order)))
            if (data == null) {
                val detail = dslContext.newRecord(Tables.TASK_DETAIL)
                detail.setCompleted(completed)
                        .setOrder(order).setTaskId(taskId)
                        .setPath(path).setProgress(progress).setSize(size).setStoreId(storeId).store()
                return@transactionResult true
            } else {
                var change = false
                if (!path.isNullOrEmpty() && path != data.path) {
                    data.path = path
                    change = true
                }
                if (completed != null && completed > 0 && completed != data.completed) {
                    data.completed = completed
                    change = true
                }
                if (size != null && size > 0 && size != data.size) {
                    data.size = size
                    change = true
                }
                if (progress != null && progress > 0 && progress != data.progress) {
                    data.progress = progress
                    change = true
                }
                if (!storeId.isNullOrEmpty() && storeId != data.storeId) {
                    data.storeId = storeId
                    change = true
                }
                if (change) {
                    data.update()
                }
                return@transactionResult change
            }
        }


        //dslContext.insertInto(Tables.TASK_DETAIL).set(rec).onDuplicateKeyUpdate().set(rec)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(TaskDetailService::class.java)
    }
}