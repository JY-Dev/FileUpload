package com.jydev.domain.media.file

import com.jydev.domain.media.file.mock.FakeMediaFileRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.InputStream

class MediaFileProcessorTest {


    @Test
    fun `MediaFileProcessor에서 MediaFileAction이 StoreMediaFile인 경우 executeAction 함수 호출 시 FileStrategy의 save가 호출 되어야 한다`() {
        val fileRepository = FakeMediaFileRepository()
        val command = MediaFileAction.StoreFileCommand(InputStream.nullInputStream(),"","",0L)
        val storeAction = MediaFileAction.StoreMediaFile(command, StorageConfiguration.S3("",""))

        MediaFileProcessor(storeAction,fileRepository).executeAction()

        Assertions.assertEquals(fileRepository.isSaveCall, true)
    }

    @Test
    fun `MediaFileProcessor에서 MediaFileAction이 DeleteMediaFile인 경우 executeAction 함수 호출 시 FileStrategy의 delete가 호출 되어야 한다`() {
        val fileRepository = FakeMediaFileRepository()
        val command = MediaFileAction.DeleteFileCommand("")
        val storeAction = MediaFileAction.DeleteMediaFile(command, StorageConfiguration.S3("",""))

        MediaFileProcessor(storeAction, fileRepository).executeAction()

        Assertions.assertEquals(fileRepository.isDeleteCall, true)
    }

    @Test
    fun `MediaFileProcessor에서 MediaFileAction이 StoreMediaFile인 경우 executeAction 함수 호출 시 FileStrategy의 save만 호출되고 delete는 호출 되지 않아야 한다`() {
        val fileStrategy = FakeMediaFileRepository()
        val command = MediaFileAction.StoreFileCommand(InputStream.nullInputStream(),"","",0L)
        val storeAction = MediaFileAction.StoreMediaFile(command, StorageConfiguration.S3("", ""))

        MediaFileProcessor(storeAction, fileStrategy).executeAction()

        Assertions.assertEquals(fileStrategy.isSaveCall, true)
        Assertions.assertEquals(fileStrategy.isDeleteCall, false)
    }

    @Test
    fun `MediaFileProcessor에서 MediaFileAction이 DeleteMediaFile인 경우 executeAction 함수 호출 시 FileStrategy의 delete만 호출되고 save는 호출 되지 않아야 한다`() {
        val fileStrategy = FakeMediaFileRepository()
        val command = MediaFileAction.DeleteFileCommand("")
        val storeAction = MediaFileAction.DeleteMediaFile(command, StorageConfiguration.S3("",""))

        MediaFileProcessor(storeAction, fileStrategy).executeAction()

        Assertions.assertEquals(fileStrategy.isDeleteCall, true)
        Assertions.assertEquals(fileStrategy.isSaveCall, false)
    }
}