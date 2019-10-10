package com.qingzhenyun.offline.handler

import com.qingzhenyun.common.ice.IceHandler
import com.qingzhenyun.common.ice.offline.*
import com.qingzhenyun.generated.offline.tables.pojos.DownloadTask
import com.qingzhenyun.offline.constants.DownloadTaskStatus
import com.qingzhenyun.offline.service.*
import com.zeroc.Ice.Current
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

@IceHandler("OfflineTaskServiceHandler")

class OfflineDownloadServiceHandlerImpl : OfflineTaskServiceHandler {


    override fun updateTaskMime(taskId: String?, mime: String?, current: Current?): Boolean {
        downloadTaskService.updateMime(taskId, mime)
        return true
    }

    override fun getSystemOfflineTaskList(taskIdList: Array<out String>?, current: Current?): Array<SystemOfflineTaskResponse> {
        return downloadTaskService.multiFetch(taskIdList!!.toList(), SystemOfflineTaskResponse::class.java).toTypedArray()
    }

    override fun listOfflineTask(userId: Long, start: Int, size: Int, order: Int, current: Current?): Array<UserOfflineTaskResponse> {
        return userTaskService.listOfflineTask(userId, start, size, order, UserOfflineTaskResponse::class.java).toTypedArray()
    }

    override fun listOfflineTaskPage(userId: Long, page: Int, pageSize: Int, order: Int, current: Current?): UserOfflinePageResponse {
        val data = userTaskService.listOfflineTaskPage(userId, page, pageSize, order, UserOfflineTaskResponse::class.java)
        return UserOfflinePageResponse(data.page, data.pageSize, data.totalCount, data.totalPage, data.list!!.toTypedArray())
    }

    override fun deleteCopyTask(taskId: String?, current: Current?): Boolean {
        return fakeCopyQueueService.deleteTask(taskId)
    }

    override fun finishCopy(taskId: String?, userId: Long, current: Current?): Boolean {
        fakeCopyQueueService.finishSingleTask(taskId, userId)
        return userTaskService.finishUserTask(taskId, userId, DownloadTaskStatus.DOWNLOAD_COPY_FINISHED)
    }

    override fun copyUserFile(taskId: String?, userId: Long, status: Int, progress: Int, copied: Long, needCopySize: Long, copiedFile: String?, filePath: String?, current: Current?): Boolean {
        // update fake copy
        var statusX: Int? = null
        val first = if (status == DownloadTaskStatus.DOWNLOAD_UPLOAD_FINISHED || status < 0) {
            statusX = status
            if (statusX == DownloadTaskStatus.DOWNLOAD_UPLOAD_FINISHED) {
                statusX = DownloadTaskStatus.DOWNLOAD_COPY_FINISHED
            }
            fakeCopyQueueService.finishSingleTask(taskId, userId)
        } else {
            fakeCopyQueueService.updateSingleTask(taskId, userId, copied, needCopySize)
        }
        val sec = userTaskService.updateUserTask(taskId, userId, copied, copiedFile, filePath, statusX)
        return first && sec
    }

    override fun fetchUserTask(taskId: String?, userId: Long, current: Current?): UserOfflineTaskResponse? {
        return userTaskService.get(taskId, userId)?.into(UserOfflineTaskResponse::class.java)
    }

    override fun fetchCopyTask(start: Int, size: Int, status: Int, taskId: String?, current: Current?): Array<CopyTaskResponse> {
        return fakeCopyQueueService.fetchTask(start, size, status, taskId, CopyTaskResponse::class.java).toTypedArray()
    }

    override fun addUserTask(taskId: String?, userId: Long, copyFile: String?, savePath: String?, current: Current?): UserOfflineTaskResponse {
        return userTaskService.addUserTask(taskId, userId, copyFile, savePath).into(UserOfflineTaskResponse::class.java)
    }

    override fun updateTaskProgress(taskId: String?, status: Int, progress: Int, size: Long, finishedSize: Long, current: Current?): Boolean {
        return downloadTaskService.updateTaskProgress(taskId, status, progress, size, finishedSize)
    }

    override fun finishOfflineTask(taskId: String?, errorCode: Int, current: Current?): Boolean {
        if (taskId.isNullOrEmpty()) {
            return false
        }
        val status = DownloadTaskStatus.DOWNLOAD_UPLOAD_FINISHED
        downloadTaskService.finishTask(taskId, status, errorCode)
        // 1.Update SystemFlag
        fakeCopyQueueService.finishUploadTask(taskId, status)
        // 2.Update CopyFlag
        waitingQueueService.finishTask(taskId)
        // 3. clean files
        return true
    }

    override fun updateSystemTaskDetail(data: SystemTaskDetailResponse?, current: Current?): Boolean {
        if (data == null) {
            return false
        }
        return taskDetailService.updateDetail(data.taskId, data.order, data.path, data.size, data.completed, data.progress, data.storeId)
    }

    override fun updateSystemTaskMetadata(data: SystemOfflineTaskWithDetailResponse?, current: Current?): Boolean {
        // Metadata update
        if (data == null) {
            logger.warn("updateSystemTaskMetadata has no data")
            return false
        }
        if (data.task == null) {
            logger.warn("updateSystemTaskMetadata has no task data")
            return false
        }
        val mime = if (data.detail.isNotEmpty()) "application/x-directory" else ""
        val task = downloadTaskService.fetchAndUpdateMetadata(data.task.taskId, data.task.name, data.task.size, data.task.serverId, mime)
        if (task == null) {
            logger.warn("updateSystemTaskMetadata %s (server id %s) cannot be found.", data.task.taskId, data.task.serverId)
            return false
        }
        if (task.status == DownloadTaskStatus.DOWNLOAD_UPLOAD_FINISHED) {
            logger.warn("updateSystemTaskMetadata %s (server id %s) already finished.", data.task.taskId, data.task.serverId)
        }
        return taskDetailService.updateMetadata(task.taskId, data.detail.toList())
    }

    override fun getSystemTask(taskId: String?, current: Current?): SystemOfflineTaskWithDetailResponse? {
        val task = downloadTaskService.fetch(taskId) ?: return null
        val detail = taskDetailService.fetchAll(taskId, SystemTaskDetailResponse::class.java)
        return SystemOfflineTaskWithDetailResponse(task.into(SystemOfflineTaskResponse::class.java), detail.toTypedArray())
    }

    override fun updateDownloadingStatus(taskId: String?, serverId: String?, status: Int, message: String?, force: Boolean, current: Current?): Boolean {

        return if (status < 0) {
            //val status = DownloadTaskStatus.DOWNLOAD_COPY_FINISHED
            downloadTaskService.finishTask(taskId, status, status)
            logger.error("Download Failed (Server id {}) {} - {}", serverId, status, message)
            // 1.Update SystemFlag
            fakeCopyQueueService.finishUploadTask(taskId, status)
            // 2.Update CopyFlag
            waitingQueueService.finishTask(taskId)
            // 3. clean files
            true
        } else {
            waitingQueueService.updateDownloadingStatus(taskId, serverId, status, message, force)
        }
    }

    override fun fetchTask(serverId: String?, types: IntArray?, status: IntArray, nextStatus: Int, size: Int, current: Current?): Array<SystemOfflineTaskResponse> {
        val rec = waitingQueueService.fetchTask(serverId, types, status, nextStatus, size, DownloadTask::class.java)
        if (rec.isEmpty()) {
            return emptyArray()
        }
        return downloadTaskService.multiFetch(rec.map { v -> v.taskId }, SystemOfflineTaskResponse::class.java).toTypedArray()
    }


    override fun addSystemTask(taskId: String?, type: Int, name: String?, createUser: Long, createIp: String?, detail: String?, current: Current?): SystemOfflineTaskResponse? {
        val task = downloadTaskService.addTask(taskId, type, name, createUser, createIp, detail)?.into(SystemOfflineTaskResponse::class.java)
                ?: return null
        if (task.status != DownloadTaskStatus.DOWNLOAD_UPLOAD_FINISHED) {
            // add
            waitingQueueService.addTask(taskId, type, name ?: "Unknown")

        }
        if (taskId != null && createUser > 0) {
            fakeCopyQueueService.addTask(taskId, createUser, task.progress, task.status)
        }
        return task
    }

    @Autowired
    private lateinit var userTaskService: UserTaskService
    @Autowired
    private lateinit var downloadTaskService: DownloadTaskService
    @Autowired
    private lateinit var waitingQueueService: WaitingQueueService
    @Autowired
    private lateinit var taskDetailService: TaskDetailService
    @Autowired
    private lateinit var fakeCopyQueueService: FakeCopyQueueService

    companion object {
        private val logger = LoggerFactory.getLogger(OfflineTaskServiceHandler::class.java)
    }
}