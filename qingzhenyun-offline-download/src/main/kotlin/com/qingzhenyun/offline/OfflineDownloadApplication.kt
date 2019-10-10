package com.qingzhenyun.offline

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class OfflineDownloadApplication

fun main(args: Array<String>) {
    runApplication<OfflineDownloadApplication>(*args)
}
