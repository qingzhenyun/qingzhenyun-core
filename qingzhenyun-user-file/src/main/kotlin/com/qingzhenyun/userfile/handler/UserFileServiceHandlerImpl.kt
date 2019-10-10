package com.qingzhenyun.userfile.handler

import com.qingzhenyun.common.ice.IceHandler
import com.qingzhenyun.common.ice.userfile.*
import com.qingzhenyun.generated.file.tables.pojos.UserFile
import com.qingzhenyun.userfile.repository.FileOperationRepository
import com.qingzhenyun.userfile.service.UserFileService
import com.zeroc.Ice.Current
import org.springframework.beans.factory.annotation.Autowired

@IceHandler("UserFileServiceHandler")
class UserFileServiceHandlerImpl : UserFileServiceHandler {
    override fun updateDirectorySize(userId: Long, uuid: String?, fileSize: Long, current: Current?): Int {
        return userFileService.updateDirectorySize(userId, uuid, fileSize)
    }

    override fun deleteFile(userId: Long, uuid: String?, current: Current?): Int {
        return userFileService.deleteFile(userId, uuid)
    }

    override fun copy(userId: Long, uuid: String?, path: String?, destUuid: String?, destPath: String?, current: Current?): Int {
        return userFileService.preCopy(userId, uuid, path, destUuid, destPath)
    }

    override fun fetchFileOperation(size: Int, current: Current?): Array<FileOperation> {
        return fileOperationRepository.fetchFileOperation(size, FileOperation::class.java).toTypedArray()
    }

    override fun getSimpleFileWithStoreIdList(userId: Long, pathList: Array<out String>, current: Current?): Array<SimpleFileWithStoreId> {
        return userFileService.getSimpleFileWithStoreIdList(userId, pathList, SimpleFileWithStoreId::class.java).toTypedArray()
    }


    override fun unlock(userId: Long, uuid: String?, current: Current?): Int {
        return userFileService.unlock(userId, uuid)
    }

    override fun listDirectory(userId: Long, uuid: String?, type: Int, start: Int, size: Int, orderBy: Int, current: Current?): Array<UserFileResponse> {
        return userFileService.listDirectory(userId, uuid, type, start, size, orderBy, UserFileResponse::class.java).toTypedArray()
    }

    override fun remove(userId: Long, uuid: String?, path: String?, current: Current?): Int {
        return userFileService.preDelete(userId, uuid, path)
    }

    override fun rename(userId: Long, uuid: String?, path: String?, newName: String?, current: Current?): Int {
        return userFileService.preRename(userId, uuid, path, newName ?: "")
    }

    override fun move(userId: Long, uuid: String?, path: String?, destUuid: String?, destPath: String?, current: Current?): Int {
        return userFileService.preMove(userId, uuid, path, destUuid, destPath)
    }

    override fun createFile(userId: Long, parent: String?, path: String?, name: String?, size: Long, storeId: String?, current: Current?): UserFileResponse? {
        return userFileService
                .createFile(UserFile().setPath(path).setUserId(userId).setName(name).setParent(parent)
                        .setSize(size).setStoreId(storeId))
                ?.into(UserFileResponse::class.java)
    }

    override fun get(userId: Long, uuid: String?, path: String?, current: Current?): UserFileResponse? {
        return userFileService.get(userId, uuid, path)?.into(UserFileResponse::class.java)
    }

    override fun listDirectoryPage(userId: Long, uuid: String?, type: Int, page: Int, pageSize: Int, orderBy: Int, current: Current?): UserFilePageResponse {
        val data = this.userFileService.listDirectoryPage(userId, uuid, type, page, pageSize, orderBy, UserFileResponse::class.java)
        return UserFilePageResponse(data.page, data.pageSize, data.totalCount, data.totalPage, (data.list
                ?: emptyList()).toTypedArray())
    }

    override fun createDirectory(userId: Long, parent: String?, path: String?, name: String?, current: Current?): UserFileResponse? {
        return userFileService
                .createDirectory(UserFile().setPath(path).setUserId(userId).setName(name).setParent(parent))
                ?.into(UserFileResponse::class.java)
    }

    @Autowired
    private
    lateinit var fileOperationRepository: FileOperationRepository

    @Autowired
    private lateinit var userFileService: UserFileService
    companion object {
        // private val logger = LoggerFactory.getLogger(UserFileServiceHandlerImpl::class.java)
    }
}