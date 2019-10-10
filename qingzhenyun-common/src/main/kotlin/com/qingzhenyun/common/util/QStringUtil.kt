package com.qingzhenyun.common.util

import java.net.URLEncoder

object QStringUtil {
    fun randomString(outputStrLength: Int): String {
        val len = if (outputStrLength > 1) outputStrLength - 1 else 1
        val chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        var passWord = ""
        for (i in 0..len) {
            passWord += chars[Math.floor(Math.random() * chars.length).toInt()]
        }
        return passWord
    }

    // Like Javascript's encodeURI function
    fun encodeUri(str: String?): String? {
        if (str.isNullOrEmpty()) {
            return str
        }
        val sb = StringBuilder()
        str!!.split('/').forEach { v -> sb.append(URLEncoder.encode(v, "UTF-8")).append('/') }
        //val len = sb.length
        if (sb.isNotEmpty()) {
            return sb.substring(0, sb.length - 1)
        }
        return sb.toString()
    }

    fun getFileName(filePath: String): String {
        val xNameArr = filePath.split('/')
        return xNameArr[xNameArr.size - 1]
    }

    fun getFileParentPath(filePath: String): String {
        val pos = filePath.lastIndexOf('/')
        return if (pos == -1) {
            ""
        } else {
            filePath.substring(0, pos)
        }
    }

    fun getFileExt(filePath: String): String? {
        val xNameArr = getFileName(filePath).split('.')
        if (xNameArr.size < 2) {
            return ""
        }
        val ext = xNameArr[xNameArr.size - 1]
        return if (ext.isEmpty()) {
            ext
        } else {
            ".$ext"
        }
    }


    fun formatPath(path: String?, ignoreCase: Boolean = false): String {
        if (path == null) {
            return "/"
        }
        if (path.isNullOrEmpty()) {
            return "/"
        }
        var xPath = if (ignoreCase) path.trim().toLowerCase() else path.trim()
        while (xPath.contains("\\")) {
            xPath = xPath.replace('\\', '/')
        }
        /*
        while (xPath.contains("//")) {
            xPath = xPath.replace("//", "/")
        }
        */

        xPath = xPath.split('/')
                .filter { c -> c.isNotEmpty() }
                .joinToString("/") { c -> if (c.length > 16384) c.substring(0, 16384) else c }

        if (xPath.endsWith('/')) {
            xPath = xPath.substring(0, xPath.length - 1)
        }
        if (xPath.isEmpty()) {
            return "/"
        }
        if (!xPath.startsWith('/')) {
            xPath = "/$xPath"
        }
        return xPath
    }

    fun pathHash(path: String?, clean: Boolean = true): String {
        val xPath = if (!clean) path ?: "" else formatPath(path)
        //HASH.md5()
        if (xPath.isEmpty()) {
            return ""
        }
        return HASH.md5(xPath)
    }
}
