package com.qingzhenyun.common.entity

import com.qingzhenyun.common.util.QStringUtil

class TrustedPath(sourcePath: String?) {
    val path: String = QStringUtil.formatPath(sourcePath)
    val uuid: String = QStringUtil.pathHash(path, false)
    fun isRoot(): Boolean {
        return path.isEmpty() || uuid.isEmpty() || "/" == path
    }
}