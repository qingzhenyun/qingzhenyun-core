package com.qingzhenyun.usercenter

import com.qingzhenyun.usercenter.service.UserCenterService
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class UserCenterServiceTest {
    @Autowired
    private lateinit var userCenterService: UserCenterService

    @Test
    fun addUserTest() {
        assertNotNull(userCenterService.addUser())
    }
}