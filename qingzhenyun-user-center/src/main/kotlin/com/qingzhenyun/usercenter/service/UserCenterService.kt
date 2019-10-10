package com.qingzhenyun.usercenter.service

import com.qingzhenyun.common.ice.usercenter.LoginFailedException
import com.qingzhenyun.common.ice.usercenter.RegisterFailedException
import com.qingzhenyun.common.util.HASH
import com.qingzhenyun.common.util.QStringUtil
import com.qingzhenyun.generated.user.Tables
import com.qingzhenyun.generated.user.tables.pojos.UserData
import com.qingzhenyun.generated.user.tables.records.UserDataRecord
import com.qingzhenyun.usercenter.repository.UserCenterRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserCenterService {
    @Autowired
    private lateinit var userCenterRepository: UserCenterRepository


    fun addUser(): UserDataRecord {
        val userData = UserData()
        userData.setCreateIp("127.0.0.1").setName("papaya2").setPassword("1234").setSalt("5678").setCreateTime(System.currentTimeMillis())
                .setEmail("papaya@dkexx.com").setCountryCode("86").setPhone("13800132000").ssid = hashMapOf("phone" to "fuck1238")
        return userCenterRepository.create(userData)
    }

    fun getUserByPhone(countryCode: String?, phone: String?): UserDataRecord? {
        val condition = Tables.USER_DATA.COUNTRY_CODE.eq(countryCode).and(Tables.USER_DATA.PHONE.eq(phone))
        return userCenterRepository[condition]
    }

    fun getUserByName(name: String?): UserDataRecord? {
        val condition = Tables.USER_DATA.NAME.eq(name)
        return userCenterRepository[condition]
    }

    fun get(uuid: Long): UserDataRecord? {
        return userCenterRepository[uuid]
    }

    fun updateUserSpaceUsage(uuid: Long, spaceUsed: Long): Int {
        val user = get(uuid) ?: return 0
        if (user.spaceUsed != spaceUsed) {
            user.setSpaceUsed(spaceUsed).update()
            return 1
        }
        return 0
    }

    fun <T> walkUser(uuid: Long, size: Int, clazz: Class<T>): List<T> {
        return userCenterRepository.getList(-1, size, clazz, Tables.USER_DATA.UUID.asc(), Tables.USER_DATA.UUID.gt(uuid))
    }

    fun changePasswordByUuid(uuid: Long, newPassword: String?): Boolean {
        val user = get(uuid) ?: return false
        val salt = user.salt
        val version = USER_VERSION
        user.password = generateHashedPassword(newPassword ?: "", salt, version)
        user.update()
        return true
    }

    fun changePassword(uuid: Long, oldPassword: String?, newPassword: String?): Boolean {
        val user = get(uuid) ?: return false
        val password = user.password
        val salt = user.salt
        val version = USER_VERSION
        return if (generateHashedPassword(oldPassword ?: "", salt, version) == password) {
            user.password = generateHashedPassword(newPassword ?: "", salt, version)
            user.update()
            true
        } else {
            false
        }
    }

    fun logout(uuid: Long, device: String?): UserDataRecord? {
        val res = get(uuid) ?: return null
        res.ssid.remove(device ?: "pc")
        res.update()
        return res
    }

    fun loginByMessage(countryCode: String?, phone: String?, device: String?): UserDataRecord? {
        val res = getUserByPhone(countryCode, phone)
        if (res == null) {
            throw LoginFailedException(404, "USER_NOT_FOUND")
        } else {
            changeSsid(res, device).update()
            return res
        }
    }

    private fun changeSsid(res: UserDataRecord, device: String?): UserDataRecord {
        val map = mutableMapOf<String, String>()
        map.putAll(res.ssid)
        map[device ?: "pc"] = QStringUtil.randomString(16)
        res.ssid = map
        return res
    }

    fun loginByName(name: String?, password: String?, device: String?): UserDataRecord? {
        return checkUser(this.getUserByName(name), password, device)
    }

    fun loginByPhone(countryCode: String?, phone: String?, password: String?, device: String?): UserDataRecord? {
        return checkUser(this.getUserByPhone(countryCode, phone), password, device)
    }

    private fun checkUser(user: UserDataRecord?, password: String?, device: String?): UserDataRecord {
        if (user != null) {
            if (generateHashedPassword(password!!,
                            user.salt) == user.password) {
                //login success.
                changeSsid(user, device).update()
                return user
            } else {
                throw LoginFailedException(403, "USER_LOGIN_FAILED")
            }
        } else {
            throw LoginFailedException(404, "USER_NOT_FOUND")
        }
    }

    fun registerUser(name: String?, password: String?, countryCode: String?, phone: String?, ip: String?, device: String?): UserDataRecord? {
        val userInName = userCenterRepository[Tables.USER_DATA.NAME.eq(name)]
        if (userInName != null) {
            throw RegisterFailedException(101, "USER_NAME_EXIST")
        }
        val userInPhone = userCenterRepository[Tables.USER_DATA.PHONE.eq(phone).and(Tables.USER_DATA.COUNTRY_CODE.eq(countryCode))]
        if (userInPhone != null) {
            throw RegisterFailedException(102, "USER_PHONE_EXIST")
        }

        // User name,phone,email available, create random hash
        val salt = QStringUtil.randomString(8)
        //val uuid = UUID.randomUUID().toString()
        val user = UserData()
        val version = USER_VERSION

        // Auto set uuid
        user.setName(name).setPhone(phone).setCreateIp(ip).salt = salt
        // Hash
        val time = System.currentTimeMillis()
        user.setPassword(generateHashedPassword(password!!, salt, version))
                .setSalt(salt)
                .setStatus(0)
                .setCountryCode(countryCode)
                .setPhone(phone)
                .setSsid(mapOf<String, String>((device ?: "pc") to QStringUtil.randomString(12)))
                .setEmail("").setType(0)
                .setSpaceCapacity(8192).setSpaceUsed(0)
                .setCreateIp(ip)
                .setCreateTime(time)
                .setVersion(USER_VERSION)
                .setName(name)
                .icon = "default.jpg"
        return userCenterRepository.create(user)
    }

    private fun generateHashedPassword(password: String, salt: String, version: Int = 1): String {
        if (version == 1) {
            return HASH.sha256(password + "f@ck" + salt) //"f@ck is a magic number"
        }
        return HASH.sha256(password + "f@ck" + salt) //"f@ck is a magic number"
    }

    companion object {
        private const val USER_VERSION = 1
    }
}