package com.jydev.domain.media.file.mock

import com.jydev.domain.media.file.MediaFileAction
import com.jydev.domain.media.file.MediaFileRepository
import com.jydev.domain.media.file.StorageConfiguration

class FakeMediaFileRepository : MediaFileRepository {

    override val storageConfigClass: Class<out StorageConfiguration>
        = StorageConfiguration.S3::class.java

    var isSaveCall = false
        private set
    var isDeleteCall = false
        private set

    override fun save(storageConfiguration: StorageConfiguration, command: MediaFileAction.StoreFileCommand) {
        isSaveCall = true
    }

    override fun delete(storageConfiguration: StorageConfiguration, command: MediaFileAction.DeleteFileCommand) {
        isDeleteCall = true
    }
}