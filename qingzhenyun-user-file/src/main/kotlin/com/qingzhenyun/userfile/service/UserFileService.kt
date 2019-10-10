package com.qingzhenyun.userfile.service


import com.qingzhenyun.common.constants.UserFileConstants
import com.qingzhenyun.common.constants.UserFileOperate
import com.qingzhenyun.common.entity.RecordPage
import com.qingzhenyun.common.entity.TrustedPath
import com.qingzhenyun.common.util.QStringUtil
import com.qingzhenyun.generated.file.tables.pojos.FileOperation
import com.qingzhenyun.generated.file.tables.pojos.UserFile
import com.qingzhenyun.generated.file.tables.records.UserFileRecord
import com.qingzhenyun.userfile.repository.FileOperationRepository
import com.qingzhenyun.userfile.repository.UserFileRepository
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserFileService {
    @Autowired
    private
    lateinit var fileOperationRepository: FileOperationRepository
    @Autowired
    lateinit var userFileRepository: UserFileRepository

    fun <T> listDirectoryPage(userId: Long, uuid: String?, type: Int, page: Int, pageSize: Int, orderBy: Int, clazz: Class<T>): RecordPage<T> {
        return userFileRepository.listDirectoryPage(userId, uuid, type, page, pageSize, orderBy, clazz)
    }

    fun <T> listDirectory(userId: Long, uuid: String?, type: Int, start: Int, size: Int, orderBy: Int, clazz: Class<T>): List<T> {
        return userFileRepository.listDirectory(userId, uuid, type, start, size, orderBy, clazz)
    }

    fun preDelete(userId: Long, uuid: String?, path: String? = null): Int {
        if (uuid.isNullOrEmpty() && path.isNullOrEmpty()) {
            return 0
        }
        val op = UserFileOperate.DELETE
        return if (!uuid.isNullOrEmpty()) {
            val result = userFileRepository.preOperate(userId, uuid, op)
            if (result > 0) {
                addOperateTable(userId, uuid!!, null, op)
            }
            result
        } else {
            val pathUuid = TrustedPath(path).uuid
            val result = userFileRepository.preOperate(userId, pathUuid, op)
            if (result > 0) {
                addOperateTable(userId, pathUuid, null, op)
            }
            result
        }
    }

    fun get(userId: Long, uuid: String?, path: String?, dslContext: DSLContext? = null, table: com.qingzhenyun.generated.file.tables.UserFile? = null): UserFileRecord? {
        //val dest: String = if (uuid.isNullOrEmpty()) TrustedPath(path).uuid else uuid ?: ""
        if (uuid.isNullOrEmpty()) {
            val tp = TrustedPath(path)
            if (tp.isRoot()) {
                return userFileRepository.findRoot(userId, dslContext, table)
            }
            return userFileRepository.get(userId, tp.uuid, dslContext, table)
        }
        return userFileRepository.get(userId, uuid, dslContext, table)
    }

    fun <T> getSimpleFileWithStoreIdList(userId: Long, pathList: Array<out String>, clazz: Class<T>): List<T> {
        // get req list
        val reqList = List(pathList.size, init = { index ->
            TrustedPath(pathList[index]).uuid
        })
        val table = userFileRepository.getTable(userId)
        val condition = table.USER_ID.eq(userId).and(table.UUID.`in`(reqList))
        return userFileRepository.listDirectory(userId, condition, -1, -1, -1, clazz)
    }

    fun preRename(userId: Long, uuidTemp: String?, path: String?, name: String): Int {
        if (uuidTemp.isNullOrEmpty() && path.isNullOrEmpty()) {
            return 0
        }
        if (uuidTemp.isNullOrEmpty() && TrustedPath(path).isRoot()) {
            return 0
        }
        val uuid = if (uuidTemp.isNullOrEmpty()) TrustedPath(path).uuid else uuidTemp
        val op = UserFileOperate.RENAME
        return if (!uuid.isNullOrEmpty()) {
            val result = userFileRepository.preOperate(userId, uuid, op)
            if (result > 0) {
                addOperateTable(userId, uuid!!, name, op)
            }
            result
        } else {
            val pathUuid = TrustedPath(path).uuid
            val result = userFileRepository.preOperate(userId, pathUuid, op)
            if (result > 0) {
                addOperateTable(userId, pathUuid, name, op)
            }
            result
        }
    }

    fun preMove(userId: Long, uuidTemp: String?, path: String?, destUuid: String?, destPath: String?): Int {
        if (uuidTemp.isNullOrEmpty() && path.isNullOrEmpty()) {
            return 0
        }
        if (uuidTemp.isNullOrEmpty() && TrustedPath(path).isRoot()) {
            return 0
        }
        val uuid = if (uuidTemp.isNullOrEmpty()) TrustedPath(path).uuid else uuidTemp
        val op = UserFileOperate.MOVE
        val dest: String = if (destUuid.isNullOrEmpty()) TrustedPath(destPath).uuid else destUuid ?: ""
        return if (!uuid.isNullOrEmpty()) {
            val result = userFileRepository.preOperate(userId, uuid, op)
            if (result > 0) {
                addOperateTable(userId, uuid!!, dest, op)
            }
            result
        } else {
            val pathUuid = TrustedPath(path).uuid
            val result = userFileRepository.preOperate(userId, pathUuid, op)
            if (result > 0) {
                addOperateTable(userId, pathUuid, dest, op)
            }
            result
        }
    }

    fun preCopy(userId: Long, uuidTemp: String?, path: String?, destUuid: String?, destPath: String?): Int {
        if (uuidTemp.isNullOrEmpty() && path.isNullOrEmpty()) {
            return 0
        }
        val uuid = if (uuidTemp.isNullOrEmpty()) TrustedPath(path).uuid else uuidTemp
        val op = UserFileOperate.COPY
        val dest: String = if (destUuid.isNullOrEmpty()) TrustedPath(destPath).uuid else destUuid ?: ""
        return if (!uuid.isNullOrEmpty()) {
            val result = userFileRepository.preOperate(userId, uuid, op)
            if (result > 0) {
                addOperateTable(userId, uuid!!, dest, op)
            }
            result
        } else {
            val pathUuid = TrustedPath(path).uuid
            val result = userFileRepository.preOperate(userId, pathUuid, op)
            if (result > 0) {
                addOperateTable(userId, pathUuid, dest, op)
            }
            result
        }
    }

    fun updateDirectorySize(userId: Long, uuid: String?, fileSize: Long): Int {
        val dat = userFileRepository.get(userId, uuid)
        return if (dat == null) {
            0
        } else {
            if (dat.size != fileSize)
                dat.setSize(fileSize).update()
            1
        }
    }

    fun unlock(userId: Long, uuid: String?): Int {
        if (uuid.isNullOrEmpty()) {
            return 0
        }
        val op = UserFileOperate.ADD
        //val dest:String = if(destUuid.isNullOrEmpty()) TrustedPath(destPath).uuid else destUuid?:""
        return userFileRepository.unLock(userId, uuid, op)
    }

    fun deleteFile(userId: Long, uuid: String?): Int {
        if (uuid.isNullOrEmpty()) {
            return 0
        }
        return userFileRepository.delete(userId, uuid)
    }

    private fun createUserFileByPathInner(userFile: UserFile, dslContext: DSLContext, table: com.qingzhenyun.generated.file.tables.UserFile): UserFileRecord? {
        val prev = (userFile.path ?: "") + (if (userFile.name.isNullOrEmpty()) "" else "/${userFile.name}")
        val checkPath = TrustedPath(prev)
        if (checkPath.isRoot()) {
            return userFileRepository.findRoot(userFile.userId)
        }
        userFile.path = checkPath.path
        userFile.uuid = checkPath.uuid
        userFile.name = ""
        val userFileByPath = this.get(userFile.userId, null, userFile.path, dslContext)
        if (userFileByPath != null) {
            return if (UserFileConstants.DIRECTORY_TYPE == userFile.type) {
                userFileByPath
            } else {
                if (userFile.version != null && userFile.version > 0) {
                    //Auto rename
                    userFile.version = 0
                    userFile.name = "${QStringUtil.getFileName(userFile.path)}.rename.${System.currentTimeMillis()}.${QStringUtil.getFileExt(userFile.path)}"
                    createUserFileByPathInner(userFile, dslContext, table)
                } else {
                    if (!userFile.storeId.isNullOrEmpty() && userFile.storeId != userFileByPath.storeId) {
                        userFileByPath.storeId = userFile.storeId
                        userFileByPath.mtime = System.currentTimeMillis()
                        userFileByPath.version += 1
                        userFileByPath.update()
                    }
                    userFileByPath
                }
            }
        } else {
            // now, check it's parent directory.
            val parentDirPath = QStringUtil.getFileParentPath(userFile.path)
            // check parent directory exist
            var parentDirectory = this.get(userFile.userId, null, parentDirPath)
            if (parentDirectory != null) {
                // Create new...
                if (UserFileConstants.DIRECTORY_TYPE != parentDirectory.type) {
                    //
                    LOGGER.warn("User {} path : {} ({})should be a directory but is a file, operation skipped.",
                            userFile.userId, parentDirPath, parentDirectory.uuid)
                    return null
                }

            } else {
                // create new directory..
                val parentDirectoryPojo = UserFile()
                        .setPath(parentDirPath)
                        .setUserId(userFile.userId)
                        .setName("")
                        .setSize(0)
                        .setType(UserFileConstants.DIRECTORY_TYPE)
                        .setVersion(0)
                parentDirectory = createUserFileByPathInner(parentDirectoryPojo, dslContext, table)
            }
            if (parentDirectory == null) {
                LOGGER.warn("{} null, return null", parentDirPath)
                return null
            }
            val res = addUserFileNodeOnParent(parentDirectory, userFile, dslContext, table)
            addOperateTable(res.userId, res.uuid, null, UserFileOperate.ADD)
            return res
        }
    }

    private fun addUserFileNodeOnParent(parentDirectory: UserFileRecord, newFile: UserFile, dslContext: DSLContext, table: com.qingzhenyun.generated.file.tables.UserFile): UserFileRecord {
        val ext = if (UserFileConstants.DIRECTORY_TYPE == newFile.type) null else QStringUtil.getFileExt(newFile.path)
        val filename = QStringUtil.getFileName(newFile.path)
        val current = System.currentTimeMillis()
        newFile.setExt(ext).setName(filename).setParent(parentDirectory.uuid)
                .setAtime(current)
                .setOp(UserFileOperate.ADD)
                .setMtime(current)
                .setCtime(current).setLocking(false).version = 0

        return userFileRepository.create(newFile, dslContext, table)
    }

    fun createDirectory(userFile: UserFile): UserFileRecord? {
        return createUserFile(userFile.setType(UserFileConstants.DIRECTORY_TYPE))
    }

    fun createFile(userFile: UserFile): UserFileRecord? {
        return createUserFile(userFile.setType(UserFileConstants.FILE_TYPE))
    }

    private fun createUserFile(userFile: UserFile): UserFileRecord? {
        val parentUuid = userFile.parent
        val table = userFileRepository.getTable(userFile.userId)
        /*
        if (parentUuid.isNullOrEmpty()) {
            return createUserFileByPathInner(userFile, getDslContext(userFile.userId))
        } else {
        */
        return userFileRepository.getDslContext(userFile.userId).transactionResult { configuration ->
            // Lock whole tables
            val dslContext = DSL.using(configuration)
            if (parentUuid.isNullOrEmpty()) {
                return@transactionResult createUserFileByPathInner(userFile, dslContext, table)
            }
            val parent = this.get(userFile.userId, parentUuid, null, dslContext, table)
            if (parent != null && UserFileConstants.DIRECTORY_TYPE == parent.type) {
                if (userFile.path.isNullOrEmpty()) {
                    userFile.path = parent.path + "/" + userFile.path
                } else {
                    userFile.path = parent.path
                }
                //userFile.uuid = null
            }
            return@transactionResult createUserFileByPathInner(userFile, dslContext, table)
            //}
        }
    }


    private fun addOperateTable(userId: Long, source: String, dest: String?, operation: Int) {
        fileOperationRepository.create(FileOperation()
                .setUserId(userId)
                .setSource(source)
                .setDest(dest)
                .setOperation(operation)
        )
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(this::class.java)
    }
}