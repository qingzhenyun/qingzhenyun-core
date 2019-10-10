package com.qingzhenyun.usercenter.repository

import com.qingzhenyun.common.jooq.service.AbstractJooqDslRepository
import com.qingzhenyun.generated.user.Tables
import com.qingzhenyun.generated.user.tables.pojos.UserData
import com.qingzhenyun.generated.user.tables.records.UserDataRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Service

@Service
class UserCenterRepository(dslContext: DSLContext) : AbstractJooqDslRepository<UserDataRecord, Long, UserData>(dslContext, Tables.USER_DATA, UserData::class.java)