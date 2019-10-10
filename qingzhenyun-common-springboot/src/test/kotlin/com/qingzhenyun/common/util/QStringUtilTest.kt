package com.qingzhenyun.common.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class QStringUtilTest {

    @Test
    fun formatPath() {
        assertEquals("/", QStringUtil.formatPath(""))
        assertEquals("/", QStringUtil.formatPath("/"))
        assertEquals("/", QStringUtil.formatPath("//"))
        assertEquals("/", QStringUtil.formatPath("//\\"))
        assertEquals("/D", QStringUtil.formatPath("D"))
        assertEquals("/D", QStringUtil.formatPath("/D"))
        assertEquals("/D", QStringUtil.formatPath("/D "))
        assertEquals("/D A", QStringUtil.formatPath("/D A"))
        assertEquals("/D", QStringUtil.formatPath("//D"))
        assertEquals("/D", QStringUtil.formatPath("//D\\"))
        assertEquals("/D", QStringUtil.formatPath("//\\D"))
        assertEquals("/D/S", QStringUtil.formatPath("D//\\S"))
        assertEquals("/D/S", QStringUtil.formatPath("//D/S"))
        assertEquals("/D/S", QStringUtil.formatPath("//D\\\\S"))
        assertEquals("/D/S", QStringUtil.formatPath("//\\D\\\\\\//\\S\\\\\\\\"))
        assertEquals("/D/S", QStringUtil.formatPath("//\\D\\\\/\\S\\\\\\\\"))
        assertEquals("/D/S", QStringUtil.formatPath("//\\D\\\\/\\S\\//\\\\"))
        assertEquals("/d/s", QStringUtil.formatPath("//D\\\\S", true))
        assertEquals("/d/s", QStringUtil.formatPath("//\\D\\\\\\//\\S\\\\\\\\", true))
        assertEquals("/d/s", QStringUtil.formatPath("//\\D\\\\/\\S\\\\\\\\", true))
        assertEquals("/d/s", QStringUtil.formatPath("//\\D\\\\/\\S\\//\\\\", true))
    }

    @Test
    fun getFileExt() {

        assertEquals("1.txt", QStringUtil.getFileName(QStringUtil.formatPath("1.txt")))
        assertEquals("1.txt", QStringUtil.getFileName(QStringUtil.formatPath("/a/b/1.txt")))
        assertNotEquals("1.txt", QStringUtil.getFileName(QStringUtil.formatPath("/a/b/1.pac")))

        assertEquals(".txt", QStringUtil.getFileExt(QStringUtil.formatPath("1.txt")))
        assertEquals(".txt", QStringUtil.getFileExt(QStringUtil.formatPath("/a/b/1.txt")))
        assertNotEquals(".txt", QStringUtil.getFileExt(QStringUtil.formatPath("/a/b/1.pac")))
        assertEquals(".txt", QStringUtil.getFileExt(QStringUtil.formatPath(".txt")))
        assertEquals(".txt", QStringUtil.getFileExt(QStringUtil.formatPath(".txt")))
        assertEquals("", QStringUtil.getFileExt(QStringUtil.formatPath("txt")))
        assertEquals("", QStringUtil.getFileExt(QStringUtil.formatPath("a.b.c/txt")))
        assertEquals("", QStringUtil.getFileExt(QStringUtil.formatPath("a.b.c/txt.")))

        assertEquals("", QStringUtil.getFileParentPath(""))
        assertEquals("", QStringUtil.getFileParentPath("/"))
        assertEquals("/a", QStringUtil.getFileParentPath("/a/c"))
        assertEquals("/a/c", QStringUtil.getFileParentPath("/a/c/d"))
    }
}