package com.qingzhenyun.common.ice

import org.springframework.stereotype.Service

@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Service
annotation class IceHandler(val name: String)