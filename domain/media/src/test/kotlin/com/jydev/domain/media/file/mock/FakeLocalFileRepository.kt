package com.jydev.domain.media.file.mock

import com.jydev.domain.media.file.MediaFileAction
import com.jydev.domain.media.file.MediaFileRepository
import com.jydev.domain.media.file.StorageConfiguration

class FakeLocalFileRepository : MediaFileRepository {

    override val storageConfigClass: Class<out StorageConfiguration> = StorageConfiguration.Local::class.java
    override fun save(storageConfiguration: StorageConfiguration, command: MediaFileAction.StoreFileCommand) {
        TODO("Not yet implemented")
    }

    override fun delete(storageConfiguration: StorageConfiguration, command: MediaFileAction.DeleteFileCommand) {
        TODO("Not yet implemented")
    }
}