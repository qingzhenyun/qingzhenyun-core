package com.qingzhenyun.offline.repository

import com.qingzhenyun.common.jooq.service.AbstractJooqDslRepository
import com.qingzhenyun.generated.offline.Tables
import com.qingzhenyun.generated.offline.tables.pojos.DownloadTask
import com.qingzhenyun.generated.offline.tables.records.DownloadTaskRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Service

@Service
class DownloadTaskRepository(dslContext: DSLContext) : AbstractJooqDslRepository<DownloadTaskRecord, String, DownloadTask>(dslContext, Tables.DOWNLOAD_TASK, DownloadTask::class.java)