package com.qingzhenyun.userfile

import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
//@SpringBootTest(classes = [UserFileApplicationTests::class,UserFileService::class])
class UserFileServiceTest {

/*
    @Test
    fun findRoot() {
        userFileService.deleteUserSpace(testUserId)
        val userId = System.currentTimeMillis()
        val findRootFirst = userFileService.findRoot(userId)
        val findRootSecond = userFileService.findRoot(userId)
        assertEquals(findRootFirst.userId, findRootSecond.userId)
        assertEquals(findRootFirst.uuid, findRootSecond.uuid)
        val userIdSep = System.currentTimeMillis()
        val findRootThird = userFileService.findRoot(userIdSep)
        assertEquals(findRootFirst.uuid, findRootThird.uuid)
    }

    @Test
    fun validateRoot() {
        val success = userFileService.validateUserFile(5)
        assertTrue(success)
    }

    @Test
    fun lockUserTable() {
        val success = userFileService.lockUserTableTest(5)
        assertTrue(success)
    }

    @Test
    fun countUserPathTest() {
        userFileService.deleteUserSpace(testUserId)
        val oneFile = UserFile().setPath("/1/2/3/4/5/6")
                .setName("file.avi")
                .setSize(19890604).setUserId(testUserId)
                .setStoreId("engineeringDrawing").setType(UserFileConstants.FILE_TYPE)
        val twoFile = UserFile().setPath("/1/2/3/7/8/")
                .setName("file2.avi")
                .setSize(19890604).setUserId(testUserId)
                .setStoreId("engineeringDrawing").setType(UserFileConstants.FILE_TYPE)
        val fileOne = userFileService.createUserFileByPath(oneFile)
        val fileTwo = userFileService.createUserFileByPath(twoFile)

        assertNotNull(fileOne)
        assertNotNull(fileTwo)

        if (fileOne == null) {
            return
        }

        if (fileTwo == null) {
            return
        }
        //
        val fileThree = userFileService.get(testUserId, null, "/1/2/3")
        assertNotNull(fileThree)
        if (fileThree == null) {
            return
        }
        assertEquals(8, userFileService.countChildren(fileThree))
        val seven = userFileService.get(testUserId, null, "/1/2/3/7")
        assertNotNull(seven)
        if (seven == null) {
            return
        }
        assertEquals("7", seven.name)
        val three = userFileService.get(testUserId, seven.parent)
        assertNotNull(three)
        if (three == null) {
            return
        }
        assertEquals(three.uuid, fileThree.uuid)
        val deleted = userFileService.preDelete(testUserId, three.uuid, null)
        assertEquals(8, deleted)
    }


    @Test
    fun createUserFileByPathTest() {
        userFileService.deleteUserSpace(testUserId)
        val oneSecond = UserFile().setPath("/Michael Wallace///a/v/")
                .setName("/interview/tank.avi")
                .setSize(19890604).setUserId(testUserId)
                .setStoreId("engineeringDrawing").setType(UserFileConstants.FILE_TYPE)
        val blackRimmedGlasses = userFileService.createUserFileByPath(oneSecond)
        assertNotNull(blackRimmedGlasses)
        if (blackRimmedGlasses != null) {
            assertEquals("/Michael Wallace/a/v/interview/tank.avi", oneSecond.path)
            assertEquals("/Michael Wallace/a/v/interview/tank.avi", blackRimmedGlasses.path)
            assertEquals(19890604, blackRimmedGlasses.size)
            assertEquals(UserFileConstants.FILE_TYPE, blackRimmedGlasses.type)
            assertEquals(".avi", blackRimmedGlasses.ext)
            assertEquals("tank.avi", blackRimmedGlasses.name)
            assertEquals("engineeringDrawing", blackRimmedGlasses.storeId)


            val redCloth = UserFile().setPath("/Michael Wallace///a/v/")
                    .setName("/interview2/talk.avi")
                    .setSize(19260817).setUserId(testUserId)
                    .setStoreId("Tiananmen").setType(UserFileConstants.FILE_TYPE)
            val belt = userFileService.createUserFileByPath(redCloth)
            // get belt
            assertNotNull(belt)
            if (belt == null) {
                return
            }
            val frog = userFileService.get(testUserId, belt.parent, null)
            assertNotNull(frog)
            if (frog == null) {
                return
            }
            assertEquals("/Michael Wallace/a/v/interview2", frog.path)
        }
    }

    @Test
    fun moveOrCopyByPathTest() {
        userFileService.deleteUserSpace(testUserId)
        val oneFile = UserFile().setPath("/1/2/3/4/5/6")
                .setName("file.avi")
                .setSize(19890604).setUserId(testUserId)
                .setStoreId("engineeringDrawing").setType(UserFileConstants.FILE_TYPE)
        val twoFile = UserFile().setPath("/1/2/3/7/8/")
                .setName("file2.avi")
                .setSize(19260817).setUserId(testUserId)
                .setStoreId("engineeringDrawing2").setType(UserFileConstants.FILE_TYPE)
        val fileOne = userFileService.createUserFileByPath(oneFile)
        val fileTwo = userFileService.createUserFileByPath(twoFile)

        assertNotNull(fileOne)
        assertNotNull(fileTwo)

        if (fileOne == null) {
            return
        }

        if (fileTwo == null) {
            return
        }
        userFileService.moveOrCopyByPath(testUserId, "/1/2/3/7", "/1/2", false)
    }

    @Before
    fun before() {
        testUserId = System.currentTimeMillis()
        LOGGER.info("Create User: {}", testUserId)
        userFileService.deleteUserSpace(testUserId)

    }

    @After
    fun after() {
        LOGGER.info("Clean User: {}", testUserId)
        userFileService.deleteUserSpace(testUserId)
        LOGGER.info("Cleaned User: {}", testUserId)
    }

    private var testUserId: Long = 0
    @Autowired
    private lateinit var userFileService: UserFileService

    companion object {
        private val LOGGER = LoggerFactory.getLogger(this::class.java)
    }
    */
}