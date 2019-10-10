package com.qingzhenyun.userfile.repository

import com.qingzhenyun.common.jooq.service.AbstractJooqDslRepository
import com.qingzhenyun.generated.file.Tables
import com.qingzhenyun.generated.file.tables.pojos.FileOperation
import com.qingzhenyun.generated.file.tables.records.FileOperationRecord
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Service

@Service
class FileOperationRepository(dslContext: DSLContext) : AbstractJooqDslRepository<FileOperationRecord, Long, FileOperation>(dslContext, Tables.FILE_OPERATION, FileOperation::class.java) {
    fun <T> fetchFileOperation(size: Int, clazz: Class<T>): List<T> {
        return getDslContext().transactionResult { configuration ->
            val dslContext = DSL.using(configuration)
            val data = this.getRecordList(-1, size, Tables.FILE_OPERATION.TASK_ID.asc(), null, dslContext)
            if (data.isNotEmpty) {
                val t = data.into(clazz)
                dslContext.batchDelete(data).execute()
                return@transactionResult t
            } else {
                return@transactionResult emptyList()
            }
        }
    }
}