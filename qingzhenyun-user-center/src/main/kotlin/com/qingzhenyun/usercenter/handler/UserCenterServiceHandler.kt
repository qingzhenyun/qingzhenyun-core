package com.qingzhenyun.usercenter.handler

import com.qingzhenyun.common.ice.IceHandler
import com.qingzhenyun.common.ice.usercenter.UserCenterServiceHandler
import com.qingzhenyun.common.ice.usercenter.UserDataResponse
import com.qingzhenyun.usercenter.service.UserCenterService
import com.zeroc.Ice.Current
import org.springframework.beans.factory.annotation.Autowired

@IceHandler("UserCenterServiceHandler")
class UserCenterServiceHandlerImpl : UserCenterServiceHandler {
    override fun updateUserSpaceUsage(uuid: Long, spaceUsed: Long, current: Current?): Int {
        return userCenterService.updateUserSpaceUsage(uuid, spaceUsed)
    }

    override fun walkUser(uuid: Long, size: Int, current: Current?): Array<UserDataResponse> {
        return userCenterService.walkUser(uuid, size, UserDataResponse::class.java).toTypedArray()
    }

    override fun changePasswordByUuid(uuid: Long, newPassword: String?, current: Current?): Boolean {
        return userCenterService.changePasswordByUuid(uuid, newPassword)
    }

    override fun changePassword(uuid: Long, oldPassword: String?, newPassword: String?, current: Current?): Boolean {
        return userCenterService.changePassword(uuid, oldPassword, newPassword)
    }

    override fun logout(uuid: Long, device: String?, current: Current?): UserDataResponse? {
        return userCenterService.logout(uuid, device)?.into(UserDataResponse::class.java)
    }

    override fun loginByMessage(countryCode: String?, phone: String?, device: String?, current: Current?): UserDataResponse? {
        return userCenterService.loginByMessage(countryCode, phone, device)?.into(UserDataResponse::class.java)
    }

    override fun loginByName(name: String?, password: String?, device: String?, current: Current?): UserDataResponse? {
        return userCenterService.loginByName(name, password, device)?.into(UserDataResponse::class.java)
    }

    override fun loginByPhone(countryCode: String?, phone: String?, password: String?, device: String?, current: Current?): UserDataResponse? {
        return userCenterService.loginByPhone(countryCode, phone, password, device)?.into(UserDataResponse::class.java)
    }

    override fun registerUser(name: String?, password: String?, countryCode: String?, phone: String?, ip: String?, device: String?, current: Current?): UserDataResponse? {
        return userCenterService.registerUser(name, password, countryCode, phone, ip, device)?.into(UserDataResponse::class.java)
    }

    override fun getUserByPhone(countryCode: String?, phone: String?, current: Current?): UserDataResponse? {
        return userCenterService.getUserByPhone(countryCode, phone)?.into(UserDataResponse::class.java)
    }


    override fun getUserByUuid(uuid: Long, current: Current?): UserDataResponse? {
        return userCenterService.get(uuid)?.into(UserDataResponse::class.java)
    }

    @Autowired
    private lateinit var userCenterService: UserCenterService

    /*
    companion object {
        private val logger = LoggerFactory.getLogger(UserCenterServiceHandlerImpl::class.java)
    }
    */
}