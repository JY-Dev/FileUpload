package com.jydev.domain.media.file.mock

import com.jydev.domain.media.file.FileStrategy
import com.jydev.domain.media.file.MediaFileRepository
import com.jydev.domain.media.file.StorageConfiguration

class FakeS3FileRepository : MediaFileRepository{

    override val storageConfigClass: Class<out StorageConfiguration> = StorageConfiguration.S3::class.java

    override fun save(storageConfiguration: StorageConfiguration, command: FileStrategy.StoreFileCommand) {
        TODO("Not yet implemented")
    }

    override fun delete(storageConfiguration: StorageConfiguration, command: FileStrategy.DeleteFileCommand) {
        TODO("Not yet implemented")
    }
}