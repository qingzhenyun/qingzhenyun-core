package com.qingzhenyun.offline.service

import com.qingzhenyun.generated.offline.Tables
import com.qingzhenyun.generated.offline.tables.records.DownloadTaskRecord
import com.qingzhenyun.offline.repository.DownloadTaskRepository
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DownloadTaskService {


    @Autowired
    private lateinit var downloadTaskRepository: DownloadTaskRepository
    @Autowired
    private
    lateinit var defaultDSLContext: DSLContext

    // fun <T> getList(hashList: Array<out String>?, clazz: Class<T>): List<T> {
    fun <T> multiFetch(taskId: List<String>, clazz: Class<T>): List<T> {
        /*
        val reqRow = row(Tables.DOWNLOAD_TASK.TASK_ID,Tables.DOWNLOAD_TASK.TYPE)
        val req:Array<Row2<String, Int>> = Array(task.size, init = { index ->
            val da = task[index]
            return@Array row(da.taskId,da.type)
        })
        */


        val dslContext = getDslContext(null)
        return dslContext.selectFrom(Tables.DOWNLOAD_TASK).where(Tables.DOWNLOAD_TASK.TASK_ID.`in`(taskId))
                .fetch().into(clazz)

    }

    fun fetch(taskId: String?): DownloadTaskRecord? {
        val dslContext = getDslContext(taskId)
        val data = dslContext.selectFrom(Tables.DOWNLOAD_TASK).where(Tables.DOWNLOAD_TASK.TASK_ID.eq(taskId))
                .fetch()
        if (data.isNotEmpty) {
            return data[0]
        }
        return null
    }

    fun updateTaskProgress(taskId: String?, status: Int, progress: Int, size: Long, finishedSize: Long): Boolean {
        val context = getDslContext(taskId)
        return context.transactionResult { configuration ->
            val dslContext = DSL.using(configuration)
            val data = dslContext.fetchOne(Tables.DOWNLOAD_TASK, Tables.DOWNLOAD_TASK.TASK_ID.eq(taskId))
                    ?: return@transactionResult false
            var changed = false
            if (status > 0 && data.status < status) {
                changed = true
                data.status = status
            }
            if (progress > 0 && data.progress < progress) {
                changed = true
                data.progress = progress
            }
            if (size > 0 && data.size < size) {
                changed = true
                data.size = size
            }
            if (finishedSize > 0 && data.finishedSize < finishedSize) {
                changed = true
                data.finishedSize = finishedSize
            }
            if (changed) {
                data.store()
            }
            return@transactionResult changed
        }

    }

    fun finishTask(taskId: String?, status: Int?, errorCode: Int?) {
        getDslContext(taskId).update(Tables.DOWNLOAD_TASK).set(Tables.DOWNLOAD_TASK.STATUS, status)
                .set(Tables.DOWNLOAD_TASK.ERROR_CODE, errorCode)
                .set(Tables.DOWNLOAD_TASK.UPDATE_TIME, System.currentTimeMillis())
                .where((Tables.DOWNLOAD_TASK.TASK_ID.eq(taskId)))
                .execute()
    }

    fun updateMime(taskId: String?, mime: String?) {
        getDslContext(taskId).update(Tables.DOWNLOAD_TASK)
                .set(Tables.DOWNLOAD_TASK.MIME, mime)
                .set(Tables.DOWNLOAD_TASK.UPDATE_TIME, System.currentTimeMillis())
                .where((Tables.DOWNLOAD_TASK.TASK_ID.eq(taskId)))
                .execute()
    }


    fun fetchAndUpdateMetadata(taskId: String?, name: String?, size: Long, serverId: String?, mime: String?): DownloadTaskRecord? {
        val data = fetch(taskId) ?: return null
        if (!name.isNullOrEmpty()) {
            data.name = name
        }
        if (size > 0) {
            data.size = size
        }
        data.updateTime = System.currentTimeMillis()
        if (!serverId.isNullOrEmpty()) {
            data.serverId = serverId
        }
        if (!mime.isNullOrEmpty()) {
            data.mime = mime
        }
        data.update()
        return data
    }

    fun addTask(taskId: String?, type: Int, name: String?, createUser: Long, createIp: String?, detail: String?): DownloadTaskRecord? {
        if (taskId.isNullOrEmpty()) {
            return null
        }
        return getDslContext(taskId).transactionResult { configuration ->
            val dslContext = DSL.using(configuration)
            val result = dslContext.selectFrom(Tables.DOWNLOAD_TASK).where(Tables.DOWNLOAD_TASK.TASK_ID.eq(taskId))
                    .forUpdate().fetch()
            if (result.isNotEmpty) {
                return@transactionResult result[0]
            } else {
                val res = dslContext.newRecord(Tables.DOWNLOAD_TASK)
                val current = System.currentTimeMillis()
                res.setTaskId(taskId).setType(type).setName(name).setCreateUser(createUser)
                        .setCreateTime(current).setProgress(0).setSize(0).setStatus(0)
                        .setUpdateTime(current).setFinishedSize(0)
                        .setCreateIp(createIp).detail = detail
                res.store()
                return@transactionResult res
            }
        }
    }

    private fun getDslContext(taskId: String?, readOnly: Boolean = false): DSLContext {
        if (taskId.isNullOrEmpty()) {
            return defaultDSLContext
        }
        if (readOnly) {
            return defaultDSLContext
        }
        return defaultDSLContext
    }
}