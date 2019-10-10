package com.qingzhenyun.ice.test

import com.qingzhenyun.common.ice.usercenter.UserCenterServiceHandlerPrx
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class IceTestApplication

fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger(IceTestApplication::class.java)
    runApplication<IceTestApplication>(*args)
    logger.info("Init...")
    val communicator = com.zeroc.Ice.Util.initialize(args)
    val base = communicator.stringToProxy("UserCenterServiceHandler")
    val checked = UserCenterServiceHandlerPrx.checkedCast(base)
    if (checked != null) {
        logger.info("Checked!")
        val res = checked.test()
        logger.info(res.toString())
    }
}
