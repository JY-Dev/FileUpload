package com.jydev.media.file.infra

import com.jydev.media.file.MediaFileRepository
import com.jydev.media.file.MediaFileStrategy
import com.jydev.media.file.StorageConfiguration
import org.springframework.stereotype.Repository

//TODO : 나중에 작업
@Repository
class S3FileRepository : MediaFileRepository {

    private val s3ConfigClass = StorageConfiguration.S3::class.java
    override val storageConfigClass: Class<out StorageConfiguration> = s3ConfigClass

    override fun save(storageConfiguration: StorageConfiguration, command: MediaFileStrategy.StoreFileCommand) {
        val config = castConfiguration(storageConfiguration, s3ConfigClass)

    }

    override fun delete(storageConfiguration: StorageConfiguration, command: MediaFileStrategy.DeleteFileCommand) {
        val config = castConfiguration(storageConfiguration, s3ConfigClass)

    }

    override fun resolveConfiguration(configuration: StorageConfiguration): Boolean {
        return configuration is StorageConfiguration.S3
    }

}