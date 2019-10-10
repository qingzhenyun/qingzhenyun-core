package com.qingzhenyun.offline.service

import com.qingzhenyun.common.jooq.service.AbstractJooqDslRepository
import com.qingzhenyun.generated.offline.Tables
import com.qingzhenyun.generated.offline.tables.pojos.WaitingQueue
import com.qingzhenyun.generated.offline.tables.records.WaitingQueueRecord
import com.qingzhenyun.offline.constants.DownloadTaskStatus
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class WaitingQueueService(dslContext: DSLContext) : AbstractJooqDslRepository<WaitingQueueRecord, String, WaitingQueue>(dslContext, Tables.WAITING_QUEUE, WaitingQueue::class.java) {
    fun addTask(taskId: String?, type: Int, name: String): WaitingQueueRecord? {
        if (taskId.isNullOrEmpty()) {
            return null
        }
        return getDslContext().transactionResult { configuration ->
            val dslContext = DSL.using(configuration)
            val result = dslContext.selectFrom(Tables.WAITING_QUEUE).where(Tables.WAITING_QUEUE.TYPE.eq(type).and(Tables.WAITING_QUEUE.TASK_ID.eq(taskId)))
                    .forUpdate().fetch()
            if (result.isNotEmpty) {
                return@transactionResult result[0]
            } else {
                val res = dslContext.newRecord(Tables.WAITING_QUEUE)
                res.setTaskId(taskId).setType(type).setName(name)
                        .setCreateTime(System.currentTimeMillis()).setStatus(DownloadTaskStatus.WAITING).updateTime = 0
                res.store()
                return@transactionResult res
            }
        }
    }

    fun finishTask(taskId: String?) {
        getDslContext().deleteFrom(Tables.WAITING_QUEUE).where(Tables.WAITING_QUEUE.TASK_ID.eq(taskId)).execute()
    }


    fun updateDownloadingStatus(taskId: String?, serverId: String?, status: Int, message: String?, force: Boolean): Boolean {
        if (force) {
            return getDslContext().update(Tables.WAITING_QUEUE)
                    .set(Tables.WAITING_QUEUE.UPDATE_TIME, System.currentTimeMillis())
                    .set(Tables.WAITING_QUEUE.STATUS, status).set(Tables.WAITING_QUEUE.MESSAGE, message)
                    .where(Tables.WAITING_QUEUE.TASK_ID.eq(taskId))
                    .execute() > 0
        } else {
            return getDslContext().transactionResult { configuration ->
                val dslContext = DSL.using(configuration)
                val result = dslContext.selectFrom(Tables.WAITING_QUEUE).where(Tables.WAITING_QUEUE.TASK_ID.eq(taskId)).limit(1)
                        .forUpdate().fetch()
                if (result.isEmpty()) {
                    logger.warn("Update {} failed. cannot find record.", taskId)
                    return@transactionResult false
                }
                val data = result[0]

                if (data.serverId != serverId) {
                    logger.warn("Update {} on different server id [{}] -> [{}]", data.serverId, serverId)
                    data.serverId = serverId
                }
                val oldStatus = data.status
                if (oldStatus > 0) {
                    if (status > oldStatus) {
                        data.status = status
                    } else {
                        logger.warn("Update {} failed. cannot find record. cannot rollback {} to {}, use force.", taskId, oldStatus, status)
                    }
                } else {
                    data.status = status
                }
                if (!message.isNullOrEmpty()) {
                    data.message = message
                }
                data.updateTime = System.currentTimeMillis()
                data.update()
                return@transactionResult true
            }
        }
    }

    fun <T> fetchTask(serverId: String?, types: IntArray?, status: IntArray, nextStatus: Int, size: Int, clazz: Class<T>): List<T> {
        if (size < 1) {
            return emptyList()
        }
        val suitCondition = Tables.WAITING_QUEUE.TYPE.`in`(types!!.asList()).and(Tables.WAITING_QUEUE.STATUS.`in`(status.asList()))
        return getDslContext().transactionResult { configuration ->
            val dslContext = DSL.using(configuration)
            val result = dslContext.selectFrom(Tables.WAITING_QUEUE).where(suitCondition).limit(size)
                    .forUpdate().fetch()
            result.forEach { data -> data.setUpdateTime(System.currentTimeMillis()).setStatus(nextStatus).serverId = serverId }
            dslContext.batchUpdate(result).execute()
            return@transactionResult result.into(clazz)
        }
    }

    fun fetchTaskRecord(serverId: String?, types: IntArray?, status: IntArray, nextStatus: Int, size: Int): List<WaitingQueueRecord> {
        if (size < 1) {
            return emptyList()
        }
        val suitCondition = Tables.WAITING_QUEUE.TYPE.`in`(types!!.asList()).and(Tables.WAITING_QUEUE.STATUS.`in`(status.asList()))
        return getDslContext().transactionResult { configuration ->
            val dslContext = DSL.using(configuration)
            val result = dslContext.selectFrom(Tables.WAITING_QUEUE).where(suitCondition).limit(size)
                    .forUpdate().fetch()
            result.forEach { data -> data.setUpdateTime(System.currentTimeMillis()).setStatus(nextStatus).serverId = serverId }
            dslContext.batchUpdate(result).execute()
            return@transactionResult result
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(WaitingQueueService::class.java)
    }
}