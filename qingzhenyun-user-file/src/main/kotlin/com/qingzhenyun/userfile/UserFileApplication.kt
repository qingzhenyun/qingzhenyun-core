package com.qingzhenyun.userfile

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class UserFileApplication

fun main(args: Array<String>) {
    runApplication<UserFileApplication>(*args)
}
