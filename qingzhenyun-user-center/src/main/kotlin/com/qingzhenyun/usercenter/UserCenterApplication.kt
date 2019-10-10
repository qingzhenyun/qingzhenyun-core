package com.qingzhenyun.usercenter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class UserCenterApplication

fun main(args: Array<String>) {
    runApplication<UserCenterApplication>(*args)
}
