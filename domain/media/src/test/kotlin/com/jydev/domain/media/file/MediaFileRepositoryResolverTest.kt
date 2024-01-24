package com.jydev.domain.media.file

import com.jydev.domain.media.file.mock.FakeLocalFileRepository
import com.jydev.domain.media.file.mock.FakeS3FileRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class MediaFileRepositoryResolverTest {
    val localFileRepository = FakeLocalFileRepository()
    val s3FileRepository = FakeS3FileRepository()
    val fileRepositoryList = listOf(localFileRepository, s3FileRepository)
    val fileRepositoryResolver = MediaFileRepositoryResolver(fileRepositoryList)

    @Test
    fun `FileRepositoryResolver에서 resolver의 입력으로 StorageConfiguration_S3가 들어왔을 때 해당 Configuration을 처리할 수 있는 FileRepository가 반환되야 합니다`() {
        val storageConfiguration = StorageConfiguration.S3("","")

        val repository = fileRepositoryResolver.resolve(storageConfiguration)

        Assertions.assertEquals(s3FileRepository , repository)
    }

    @Test
    fun `FileRepositoryResolver에서 resolver의 입력으로 StorageConfiguration_Local이 들어왔을 때 해당 Configuration을 처리할 수 있는 FileRepository가 반환되야 합니다`() {
        val storageConfiguration = StorageConfiguration.Local("","")

        val repository = fileRepositoryResolver.resolve(storageConfiguration)

        Assertions.assertEquals(localFileRepository , repository)
    }

    @Test
    fun `FileRepositoryResolver에서 resolver의 입력으로 StorageConfiguration_Local이 들어왔을 때 해당 Configuration을 처리할 수 없으면 예외가 발생해야합니다`() {
        val storageConfiguration = StorageConfiguration.Local("","")
        val fileRepositoryList = listOf(s3FileRepository)
        val fileRepositoryResolver = MediaFileRepositoryResolver(fileRepositoryList)

        Assertions.assertThrows(IllegalArgumentException::class.java) {
            fileRepositoryResolver.resolve(storageConfiguration)
        }
    }
}